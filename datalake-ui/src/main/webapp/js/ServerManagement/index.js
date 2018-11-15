/**
 * Created by wangwenhu on 17/8/19.
 */
var trendData=[];
var refreshtime = 30000;
var scream_d = {};
$(function(){
    /*判断是否需要提示密码即将过期*/
    var pwdMessage=$.GetCookie('pwdMessage');
    if(pwdMessage!=null && pwdMessage!="" && pwdMessage!=undefined){
        $.delCookie('pwdMessage');
        console.log("pwdMessage:"+pwdMessage);
        alertWarn("提示信息",pwdMessage);
    }
    //获取菜单实例
    initMenu();
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width:$("#tendency").width(),
        height:$("#tendency").width()
    });
    /*中间图标区 显示事件数*/
    $("#chart_div_ltem").on("click",".bianqianNum",function(){
        if($(this).parents(".chart_ltem").find(".questionLayer").is(":hidden")){
            $(this).parents(".chart_ltem").find(".questionLayer").show();
        }else{
            $(this).parents(".chart_ltem").find(".questionLayer").hide();
        }
        $(this).parents(".chart_ltem").siblings().find(".questionLayer").hide();
    });
    instAjaxFun();
    /*中间大图 建立ws*/
    instWSDashScreenFun();
    setInterval(instWSDashScreenFun,refreshtime);

    $("#select1").change(function () {
    	 instWSDashScreenFun();
    	 instWSItemFun(scream_d);
    });
});

/*Ajax 加载*/
function instAjaxFun(){
    /*中间多模块加载*/
    $.ajax({
        type:"POST",
        url:http_service+"/dashboard/getSchemaList",
        dataType:"json",
        async:false,
        success:function(data){
            if(returnDataCheck(data)){
            	scream_d = data.data;
                instChartItem(data.data);
            }
        },
        error: function(e) {
        }
    });
}
/*中间首图*/
function instEcharts(data){
    echarts.dispose(echarts.getInstanceByDom(document.getElementById('chartDol')));
    lineChartNoBorderFun("chartDol",data.xArr,data.series);
}
/*中间多模块加载*/
function instChartItem(data){
    var html="";
    $.each(data,function(){
        html+="<div class=\"chart_ltem fl\">";
            html+=setDbIco(this.db_typ);
            html+="<div class=\"smpv-info\">";
                html+="<a href='dashboard.html?schema="+this.source_schema+"&instance="+this.source_instance+"'><p class=\"dolP\"><img src=\"images/DOL-icon.png\"/><span>"+this.source_schema+"</span></p></a>";
                html+="<p>"+this.sync_table_count+" / "+this.table_count+"</p>";
                html+="<a href='dashboard.html?schema="+this.source_schema+"&instance="+this.source_instance+"'><p>"+this.source_instance+"</p></a>";
            html+="</div>";
            html+="<div class=\"zhouqi\">";
                html+="<em class=\"em1\"></em>";
                html+="<span id='"+this.source_schema+"_"+this.source_instance+"_upper'>上一周期</span>";
                html+="<em class=\"em2\"></em>";
                html+="<span id='"+this.source_schema+"_"+this.source_instance+"_current'>这一周期</span>";
                html+="<p class=\"bianqianNum\" id='"+this.source_schema+"_"+this.source_instance+"_ul_cun'>: 7</p>";
            html+="</div>";
            html+="<div class=\"question_quxian\">";
                html+="<div id='"+this.source_schema+"_"+this.source_instance+"' class='question_quxian_echerts' style='width: 100%;height: 70px;'>";
                html+="</div>";
            html+="</div>";
            html+="<div class=\"questionLayer\">";
                html+="<span></span>";
                html+="<ul id='"+this.source_schema+"_"+this.source_instance+"_ul'>";
                html+="</ul>";
            html+="</div>";
        html+="</div>";
    });
    $("#chart_div_ltem").html(html);
    $(".question_quxian_echerts").css({
        width:$(".question_quxian").width()
    });
    instWSItemFun(data);
    window.setInterval(function() {instWSItemFun(data)}, refreshtime);
}

function setDbIco(type){
	if("ORACLE"==type){
		return  "<img class=\"mysql-icon\" src=\"images/oracle.gif\"/>";
	}else if("DB2"==type){
		return  "<img class=\"mysql-icon\" src=\"images/db2.gif\"/>";
	}else if("MYSQL"==type){
		return  "<img class=\"mysql-icon\" src=\"images/mysql-icon1.png\"/>";
	}else if("MSSQL"==type){
		return  "<img class=\"mysql-icon\" src=\"images/sql-service.png\"/>";
	}else if("MONGO"==type){
        return  "<img class=\"mysql-icon\" src=\"images/mongo.png\"/>";
    }
}
/*中间多模块 图表加载*/
function instItemChart(data){
    $.each(data,function() {
        var id = this.source_schema + "_" + this.source_instance;
        $("#" + id + "_upper").html(this.legendArr[0]);
        $("#" + id + "_current").html(this.legendArr[1]);
        $("#" + id + "_ul_cun").html(": "+this.event_elements.length);
        var html = "";
        $.each(this.event_elements, function () {
            html += "<li>" + this.message + "</li>";
        });
        $("#" + id + "_ul").html(html);
        echarts.dispose(echarts.getInstanceByDom(document.getElementById(id)));
        indexlineChartNoShaftFun(id,this.xArr,this.series);
    });
}
/* 中间大图 建立ws */
function instWSDashScreenFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/dashScreen",
        dataType:"json",
        data:{"particleSize":$("#select1").val()},
        success:function(data){
            if(returnDataCheck(data)){
                instEcharts(data.data);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*中间多模块 建立ws*/
function instWSItemFun(data){
    var urlData=[];
    $.each(data,function(){
        urlData.push({source_schema:this.source_schema,source_instance:this.source_instance});
    });
    var jsonData=JSON.stringify(urlData);
    $.ajax({
        type:"POST",
        url:http_service+"/dashSocket",
        dataType:"json",
        data:{"data":jsonData,"particleSize":$("#select1").val()},
        success:function(data){
            if(returnDataCheck(data)){
                instItemChart(data.data);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}

