/**
 * Created by wangwenhu on 17/8/19.
 */
var trendData = []; /*同步增量行数趋势 数据*/
var schema = "",
    instance = "",
    tableName = "";/*url参数*/
var srcdata = {};
var  refreshtime  =  60000;
$(function() {
    //获取菜单实例
    thisMenuFlag=$.QueryString("menuFlag");
    initMenu();
    /*获得url参数*/
    schema = $.QueryString("schema");
    instance = $.QueryString("instance");
    tableName =  $.QueryString("tableName");
    $(".left_span").html(schema);
    $(".right_span").html(instance);
    $(".title_tb").html(tableName);
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width: $("#tendency").width(),
        height: $("#tendency").width()
    });
    /*事件加载*/
    clickFun();
    aclList();
});
function clickFun(){
    /*点击复选框*/
    $(".power-list-title").on("click",".title-list li span.checkbox",function(){
        if( $(this).children("img").hasClass("checkedImg")){
            $(this).children("img").removeClass("checkedImg");
            $(".power-list li.checkboxLi span.checkbox").children("img").removeClass("checkedImg");
        }else{
            $(this).children("img").addClass("checkedImg");
            $(".power-list li.checkboxLi span.checkbox").children("img").addClass("checkedImg");
        }
    });
    /*点击复选框*/
    $("#cell-data").on("click",".power-list li.checkboxLi span.checkbox",function(){
        if( $(this).children("img").hasClass("checkedImg")){
            $(this).children("img").removeClass("checkedImg");
        }else{
            $(this).children("img").addClass("checkedImg");
        }
    });
    //单选
    $("#power-layer-data").on("click",".ulSelect1 li",function(){
        $(this).addClass("activeBg").siblings("li").removeClass("activeBg");
    });
    //复选
    $("#power-layer-data").on("click",".ulSelect2 li",function(){
        if($(this).hasClass("activeBg")){
            $(this).removeClass("activeBg");
        }else{
            $(this).addClass("activeBg");
        }
    });
    //确定按钮
    $("#power-layer-data").on("click","button.confirm",function(){
        $(".power-layer").stop().animate({"left":"-1000px"},500);
        $(".layerOpacity").hide();
        var checkedImg=$(".power-list-title .title-list li span.checkbox img").hasClass("checkedImg");
        /*给当前编辑对象赋值*/
        var cipherText=$("#power-layer-data .ulSelect1 li.activeBg").text();/*加密方法*/
        var cipherIndex=$("#power-layer-data .ulSelect1 li.activeBg").attr("valId");/*加密方法*/
        var tt1 = $('div.flag').find('li').eq(2);/*脱敏方式*/
        var tt2 = $('div.flag').find('li').eq(3);/*访问权限*/
        var mask=checkArr("#power-layer-data .checkUl1",tt1);
        var columnAcl=checkArr("#power-layer-data .checkUl2",tt2);
        var key=$("#cell-data div.flag").attr("keyVal");
        var imgMap=[];
        $("#cell-data .power-list li.checkboxLi span.checkbox img").each(function (i) {
            if($(this).hasClass("checkedImg")){
                imgMap.push($(this).attr("keyVal"));
            }
        });
        if(imgMap.length>0){
            $.each(imgMap, function(i) {
                var keys=imgMap[i]
                if(cipherText=="不加密"){
                    srcdata[keys]["encrypt"]=false;
                }else{
                    srcdata[keys]["encrypt"]=true;
                }
                srcdata[keys]["cipherIndex"]=parseInt(cipherIndex);
                srcdata[keys]["mask"]=(mask=="" || mask==null || mask==undefined ?[]:$.split(mask,","));
                srcdata[keys]["columnAcl"]=(columnAcl=="" || columnAcl==null || columnAcl==undefined ?[]:$.split(columnAcl,","));
            });
        }

        if(cipherText=="不加密"){
            srcdata[key]["encrypt"]=false;
        }else{
            srcdata[key]["encrypt"]=true;
        }
        srcdata[key]["cipherIndex"]=parseInt(cipherIndex);
        srcdata[key]["mask"]=(mask=="" || mask==null || mask==undefined ?[]:$.split(mask,","));
        srcdata[key]["columnAcl"]=(columnAcl=="" || columnAcl==null || columnAcl==undefined ?[]:$.split(columnAcl,","));
        var roleMap=[];
        $("#power-layer-data .ulSelect3 li").each(function(){
            var roleName=$(this).text();
            var filterValue=$(this).find("input.filterValue").val();
            roleMap.push({
                roleName:roleName,
                filterValue:filterValue
            });
        });
        srcdata[key]["rowAcl"]=roleMap;
        var urlData={
            "schema":schema,
            "instance":instance,
            "tableName":tableName,
            "jsonData":JSON.stringify(srcdata)
        };
        $.ajax({
            type: "POST",
            url: http_service + "/acl/aclUpdate",
            dataType: "json",
            data: urlData,
            success: function(data) {
                if (returnDataCheck(data)) {
                    aclList();
                    $(".power-list .bianji span").removeClass("active");
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    });
    $("#power-layer-data").on("click","button.cancel",function(){
        $(".power-layer").stop().animate({"left":"-1000px"},500);
        $(".layerOpacity").hide();
        $(".power-list .bianji span").removeClass("active");
    });
}
//多选
function checkArr(clsName,iptName){
    var checkStr= "";
    $(clsName).children(".activeBg").each(function(){
        checkStr+=$(this).find("b").html();
        checkStr+=",";
    });
    var Str=checkStr.slice(0,(checkStr.length-1));
    return Str;
}
//编辑
function edit(key,obj){
    //给当前的power-list一个状态
    $("[id ='"+key+"']").parent().parent('.power-list').addClass('flag').siblings('.power-list').removeClass('flag');
    $("[id ='"+key+"']").parent().parent(".power-list").find("li.bianji").children("span").removeClass("active");
	$("[id ='"+key+"']").children("span").addClass("active");
	queryAclSelectList(key,obj);
}
//列表数据
function aclList(){

    var imgMap={};
    $("#cell-data .power-list li.checkboxLi span.checkbox img").each(function (i) {
        if($(this).hasClass("checkedImg")){
            imgMap[$(this).attr("keyVal")]=true;
        }
    });
    var cipherMap=[];
    $.ajax({
        type: "POST",
        url: http_service + "/acl/queryCiphers",
        data: {},
        async:false,
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                cipherMap=data.data;
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
	 $.ajax({
	        type: "POST",
	        url: http_service + "/acl/aclList",
	        dataType: "json",
	        data: {
	            "schema": schema,
	            "instance": instance,
	            "tableName":tableName
	        },
	        success: function(data) {
	            if (returnDataCheck(data)) {
	            	srcdata = data.data;
	            	var html = "";
                    $.each(srcdata, function(key,value) {
                        html += "<div class=\"power-list\" keyVal='"+key+"'>";
                        html += "<ul class=\"clearfix\" rowAclData=\""+JSON.stringify(value.rowAcl).replace(/\"/g,"&quot;")+"\">";
                        html += "<li class=\"checkboxLi\"><span class=\"checkbox\"><img src=\"images/checked.png\" class=\"checkboxCon "+(imgMap[key]?'checkedImg':'')+" \" keyVal='"+key+"'></span>"+key+"</li>";
                        html += "<li class='cipher' cipherIndex=\""+value.cipherIndex+"\">"+getCipher(value.encrypt,value.cipherIndex,cipherMap)+"</li>";
                        html += "<li class='maskData'>"+setDefort(value.mask.join(","))+"</li>";
                        html += "<li class='columnAclData'>"+setDefort(value.columnAcl.join(","))+"</li>";
                        html += "<li class=\"bianji\" id=\""+key+"\"><span onclick=\"edit('"+key+"',this)\">编辑</span></li>";
                        html +="</ul>";
                        html += "</div>";
                    });
                    $("#cell-data").html(html);
	            }
	        },
	        error: function(e) {},
	        complete: function(XHR, TS) {
	            XHR = null
	        }
	    });
}

function setDefort(vlu){
	if (vlu==null||""==vlu){
		return "无";
	}else{
		return vlu;
	}
}
//组装选择框
function queryAclSelectList(key,obj){
    var cipherIndex=$(obj).parent().parent().find(".cipher").attr("cipherIndex");
    var cipherText=$(obj).parent().parent().find(".cipher").text();
    var maskData=$(obj).parent().parent().find(".maskData").text();
    var maskList=$.split(maskData,",");
    var columnAclData=$(obj).parent().parent().find(".columnAclData").text();
    var columnAclList=$.split(columnAclData,",");
    var rowAclData=$(obj).parent().parent().attr("rowAclData").replace(/\\"/g,"&quot;");
    var rowAclList=JSON.parse(rowAclData);
	 $.ajax({
	        type: "POST",
	        url: http_service + "/acl/queryAclSelectList",
	        dataType: "json",
	        async:false,
	        data: {},
	        success: function(data) {
	            if (returnDataCheck(data)) {
	            	var html = "<div class=\"power_select clearfix\">";
	            	var cipherIndex = data.data.cipherIndex;
	            	html += "<p class=\"power_p\">"+key+"</p>";
	            	html +="<div class=\"power_select_list\">";
                    html +="<div class=\"div_img\">";
                    html +="<img src=\"images/persona-icon.png\"/><span>" +cipherIndex.name +"</span>";
                    html +="</div>";
                    html +="<div class=\"scorllY_power scorllY_width\">";
                    html +="<ul class=\"ulSelect ulSelect1\">";
	            	var cipherIndexMap = cipherIndex.dataMap;
	            	$.each(cipherIndexMap, function(key,value) {
                        html+="<li";
                        if(key==cipherText){
                            html+=" class='activeBg' ";
                        }
                        html+=" valId='"+value+"'  title='"+key+"'>"+key+"<span></span></li>";
                    });
	            	html += "</ul>";
                    html +="</div>";
                    html +="</div>";
	            	var mask = data.data.mask;
	            	html += "<div class=\"power_select_list\">";
                    html +="<div class=\"div_img\">";
                    html +="<img src=\"images/tuomin.png\"/>";
                    html +="<span>"+mask.name+"</span>";
                    html +="</div>";
                    html +="<div class=\"scorllY_power scorllY_width\">";
                    html +="<ul class=\"ulSelect ulSelect2 checkUl1\">";
	            	var maskMap = mask.dataMap;
	            	$.each(maskMap, function(key,value) {
                        html += "<li";
                        $.each(maskList,function(i){
                            if(key==maskList[i]){
                                html+=" class='activeBg' ";
                                return false;
                            }
                        });
                        html+="  title='"+key+"'><b>"+key+"</b><span></span></li>";
                    });
	            	html += "</ul>";
                    html +="</div>";
                    html +="</div>";
	            	var columnAcl = data.data.columnAcl;
	            	html += "<div class=\"power_select_list\">";
                    html +="<div class=\"div_img\">";
                    html +="<img src=\"images/liequanxian.png\"/>";
                    html +="<span>"+columnAcl.name+"</span></div>";
                    html +="<div class=\"scorllY_power scorllY_width\">";
                    html +="<ul class=\"ulSelect ulSelect2 checkUl2\">";
	            	var columnAclMap = columnAcl.dataMap;
	            	$.each(columnAclMap, function(key,value) {
                        html += "<li";
                        $.each(columnAclList,function(i){
                            if(key==columnAclList[i]){
                                html+=" class='activeBg' ";
                                return false;
                            }
                        });
                        html+="  title='"+key+"'><b>"+key+"</b><span></span></li>";
                    });
	            	html += "</ul>";
                    html +="</div>";
                    html +="</div>";
	            	var rowAcl = data.data.rowAcl;
	            	html += "<div class=\"power_select_list power_select_list2\">";
                    html +="<div class=\"div_img\">";
                    html +="<img src=\"images/hangquanxian.png\"/>";
                    html +="<span>"+rowAcl.name+"</span>";
                    html +="</div>";
                    html +="<div class=\"scorllY_power scorllY_power2 scorllY_width\">";
                    html +="<ul class=\"ulSelect ulSelect3\">";
	            	var rowAcllMap = rowAcl.dataMap;
	            	$.each(rowAcllMap, function(key,value) {
                        var val=value;
                        $.each(rowAclList,function(){
                            if(key==this.roleName){
                                val=this.filterValue;
                                return false;
                            }
                        });
                        html += "<li title='"+key+"'>"+key+"<input type=\"text\" class='filterValue' value=\""+val+"\"></li>";
                    });
	            	html += "</ul>";
                    html +="</div>";
                    html +="</div>";
	            	html += "<div class=\"shuoming\">";
                    html +="<p>(<span>\"○\"</span>单选,<span>\"□\"</span>复选)</p>";
                    html +="<button class=\"insure confirm\">确认</button>";
                    html +="<button class=\"insure cancel\" style='margin-right: 0;'>取消</button>";
                    html +="</div>";
                    html +="</div>";
	            	$("#power-layer-data").html(html);

                    $(".layerOpacity").show();/*遮罩层*/
                    $(".power-layer").stop().animate({"left":"15%"},1000);
	            }
	        },
	        error: function(e) {},
	        complete: function(XHR, TS) {
	            XHR = null
	        }
	    });
}

function getCipher(flg,data,cipherMap){
	if(false==flg){
		return "不加密";
	}else{
        return cipherMap[data];
    }
}
function bak(){
	location.href=web_http+ "/dashboard.html?schema="+schema+"&instance="+instance+"&menuFlag="+thisMenuFlag;
}
