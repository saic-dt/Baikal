/**
 * Created by wangwenhu on 17/8/19.
 */
var trendData = []; /*同步增量行数趋势 数据*/
var schema = "",
    instance = "", /*,syncCount="",count=""*/  /*url参数*/
    tableName = "";
var srcdata = {};
var  refreshtime  =  60000;
$(function() {
    //获取菜单实例
    thisMenuFlag="userRoles";/*设置菜单选中*/
    initMenu();
    /*获得url参数*/
    schema = $.QueryString("schema");
    instance = $.QueryString("instance");
    tableName =  $.QueryString("tableName");
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width: $("#tendency").width(),
        height: $("#tendency").width()
    });
    /*事件加载*/
    clickFun();
    /*ajax加载*/
    ajaxFun();
    /*趋势事件 建立ws*/
    instWSHistoryMagFun();
    setInterval(instWSHistoryMagFun, refreshtime);
    initOGGTrend();
});
function clickFun(){
    /*点击列表出现对勾*/
    $(".border-Div").on("click",".ul1 li span",function(){
        if( $(this).children("img").hasClass("checkedImg")){
            $(this).children("img").removeClass("checkedImg");
        }else{
            $(this).children("img").addClass("checkedImg");
        }
    });
    //用户弹出框复选
    $("#addUserDivUl").on("click","li",function(){
        if($(this).hasClass("activeBg")){
            $(this).removeClass("activeBg");
        }else{
            $(this).addClass("activeBg");
        }
    });
    //角色反选
    $(".fanxuan1").click(function(){
        $.each($(".ul3 li span").children("img"),function(){
            if($(this).hasClass("checkedImg")){
                $(this).removeClass("checkedImg");
            }else{
                $(this).addClass("checkedImg");
            }
        });
    });
    //用户反选
    $(".fanxuan2").click(function(){
        $.each($(".ul2 li span").children("img"),function(){
            if($(this).hasClass("checkedImg")){
                $(this).removeClass("checkedImg");
            }else{
                $(this).addClass("checkedImg");
            }
        });
    });
    //新增用户
    $("#addUser").click(function(){
        $.ajax({
            type: "POST",
            url: http_service + "/userRole/roleList",
            data: {},
            dataType: "json",
            success: function(data) {
                if (returnDataCheck(data)) {
                    var html="";
                    $.each(data.data,function(i){
                        html+="<li title='"+this.rowKey+"'>"+this.rowKey+"<span></span></li>";
                    });
                    $("#addUserDivUl").html(html);
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
        $("#addUserType").val("add");
        $(".addUserDiv").show();
        $(".maskPosition").show();
    });
    //新增角色
    $("#addRole").click(function(){
        $(".addRoleDiv").show();
        $(".maskPosition").show();
    });
    //用户添加保存按钮
    $("button.userInsure").click(function(){
        var userNameVal=$("#userAddName").val();
        var roleList=checkArr("#addUserDivUl");
        var addUserType=$("#addUserType").val();
        var index=userNameVal.indexOf("_");
        if(userNameVal.length > 0 && (userNameVal[0] == "_" ||userNameVal[userNameVal.length-1] == "_"||userNameVal[0] == "@" ||userNameVal[userNameVal.length-1] == "@")){
            $(".addUserDiv .title_error").html("用户名称不能以下划线或@符号为开头或结尾！");
            return false;
        }
        var regNumber=new RegExp(regExType.number);
        var indexBer=userNameVal.substring(0,1);
        if(regNumber.test(indexBer)){
            $(".addUserDiv .title_error").html("用户名称数字不能为开头！");
            return false;
        }
        if(regNumber.test(userNameVal)){
            $(".addUserDiv .title_error").html("用户名称不能只包含数字！");
            return false;
        }
        if(userNameVal.length>50 || userNameVal.length<2){
            $(".addUserDiv .title_error").html("用户名称只能2～50个字！");
            return false;
        }
        var reg=new RegExp(regExType.addUserName);
        if(!reg.test(userNameVal)){
            $(".addUserDiv .title_error").html("用户名称只能为字母、数字、@和下划线！");
            return false;
        }
        $(".addUserDiv .title_error").html("");
        var url="/userRole/";
        if(addUserType=="add"){
            url+="userAdd";
        }else{
            url+="userEdit";
        }
        $.ajax({
            type: "POST",
            url: http_service + url,
            data: {"rowKey":userNameVal,"roleList":roleList},
            dataType: "json",
            success: function(data) {
                if (returnDataCheck(data)) {
                    $("#userAddName").removeAttr("disabled");
                    $(".addUserDiv").hide();
                    $(".maskPosition").hide();
                    $("#userAddName").val("");
                    /*用户列表*/
                    ajaxUserList();
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });

    });
    $("button.userCancel").click(function(){
        $("#userAddName").removeAttr("disabled");
        $(".addUserDiv").hide();
        $(".maskPosition").hide();
        $("#userAddName").val("");
    });
    //角色添加保存按钮
    $("button.roleInsure").click(function(){
        var roleName=$("#roleName").val();
        var description=$("#description").val();
        if(roleName.length > 0 && (roleName[0] == "_" ||roleName[roleName.length-1] == "_")){
            $(".addRoleDiv .title_error").html("角色名称下划线不能为开头或结尾！");
            return false;
        }
        var regNumber=new RegExp(regExType.number);
        var indexBer=roleName.substring(0,1);
        if(regNumber.test(indexBer)){
            $(".addRoleDiv .title_error").html("用户名称数字不能为开头！");
            return false;
        }
        if(regNumber.test(roleName)){
            $(".addRoleDiv .title_error").html("角色名称不能只包含数字！");
            return false;
        }
        if(roleName.length>12 || roleName.length<2){
            $(".addRoleDiv .title_error").html("角色名称只能2～12个字！");
            return false;
        }
        var reg=new RegExp(regExType.userName);
        if(!reg.test(roleName)){
            $(".addRoleDiv .title_error").html("角色名称只能为字母、数字和下划线！");
            return false;
        }
        if(description.length>25){
            $(".addRoleDiv .title_error").html("角色说明不能超过25个字！");
            return false;
        }
        $(".addRoleDiv .title_error").html("");
        $.ajax({
            type: "POST",
            url: http_service + "/userRole/roleAdd",
            data: {"rowKey":roleName,"description":description},
            dataType: "json",
            success: function(data) {
                if (returnDataCheck(data)) {
                    $(".addRoleDiv").hide();
                    $(".maskPosition").hide();
                    $("#roleName").val("");
                    $("#description").val("");
                    /*角色列表*/
                    ajaxRoleList();
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    });
    $("button.roleCancel").click(function(){
        $(".addRoleDiv").hide();
        $(".maskPosition").hide();
        $("#roleName").val("");
        $("#description").val("");
    });
    /*角色删除*/
    $("#roleDelAll").click(function(){
        var checkStr= "";
        $("#roleList").find(".checkedImg").each(function(i){
            if(i>0){
                checkStr+=",";
            }
            checkStr+=$(this).attr("keyVal");
        });
        var urlData=$.split(checkStr,",");
        var rows ={"rowKey":urlData};
        $.ajax({
            type: "POST",
            url: http_service + "/userRole/roleDelCheck",
            data: rows,
            dataType: "json",
            traditional: true,
            success: function(data) {
                if (returnDataCheck(data)) {
                    if(data.success){
                        alertFn("删除确认","角色与现有用户相关联，删除后用户","权限会受影响，确认是否删除?",true,ajaxRoleDel);
                    }else if(!data.success){
                        alertFn("删除确认","角色与现有用户没有关联","确定要删除信息?",true,ajaxRoleDel);
                    }
                }

            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });

    });
    /*用户删除*/
    $("#userDelAll").click(function(){
        var checkStr= "";
        $("#userList").find(".checkedImg").each(function(i){
            if(i>0){
                checkStr+=",";
            }
            checkStr+=$(this).attr("keyVal");
        });
        var urlData=$.split(checkStr,",");
        var rows ={"rowKey":urlData};
        $.ajax({
            type: "POST",
            url: http_service + "/userRole/userDel",
            data: rows,
            dataType: "json",
            traditional: true,
            success: function(data) {
                if (returnDataCheck(data)) {
                    /*用户列表*/
                    ajaxUserList();
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    });
    //弹框关闭
    $("#promptBox").on("click",".closeInsure,.cancel",function() {
        $("#promptBox").hide();
        $(".maskPosition").hide();
    })
}
//多选
function checkArr(clsName){
    var checkStr= "";
    $(clsName).children(".activeBg").each(function(){
        var t=$(this).text();
        checkStr+=t;
        checkStr+=",";
    });
    var Str=checkStr.slice(0,(checkStr.length-1));
   return Str;

}
function ajaxFun(){
    /*角色列表*/
    ajaxRoleList();
    /*用户列表*/
    ajaxUserList();
}
function ajaxRoleList(){
    $.ajax({
        type: "POST",
        url: http_service + "/userRole/roleList",
        data: {},
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var html="";
                $.each(data.data,function(i){
                    html+="<ul class=\"clearfix ul1 ul3\">";
                    html+="<li>";
                    html+="<span><img src=\"images/checked.png\" keyVal='"+this.rowKey+"'/></span>";
                    html+="</li>";
                    html+="<li>"+this.rowKey+"</li>";
                    html+="<li>"+this.description+"</li>";
                    html+="</ul>";
                });
                $("#roleList").html(html);
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
function ajaxUserList(){
    $.ajax({
        type: "POST",
        url: http_service + "/userRole/userList",
        data: {},
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var html="";
                $.each(data.data,function(i){
                    html+="<ul class=\"clearfix ul1 ul2\">";
                    html+="<li>";
                    html+="<span><img src=\"images/checked.png\" keyVal='"+this.rowKey+"'/></span>";
                    html+="</li>";
                    html+="<li onclick=\"userUpd('"+this.rowKey+"')\"  style=\"cursor: pointer;\">编辑</li>";
                    html+="<li>"+this.rowKey+"</li>";
                    html+="<li>"+this.roleList+"</li>";
                    html+="</ul>";
                });
                $("#userList").html(html);
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
function ajaxRoleDel(){
    var checkStr= "";
    $("#roleList").find(".checkedImg").each(function(i){
        if(i>0){
            checkStr+=",";
        }
        checkStr+=$(this).attr("keyVal");
    });
    var urlData=$.split(checkStr,",");
    var rows ={"rowKey":urlData};
    $.ajax({
        type: "POST",
        url: http_service + "/userRole/roleDel",
        data: rows,
        dataType: "json",
        traditional: true,
        success: function(data) {
            if (returnDataCheck(data)) {
                /*角色列表*/
                ajaxRoleList();
                /*用户列表*/
                ajaxUserList();
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*yh修改*/
function userUpd(user){
    var userRole=[];
    $.ajax({
        type: "POST",
        url: http_service + "/userRole/userRoleList",
        data: {"rowkey":user},
        async:false,
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                userRole=$.split(data.data,",");
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
    $.ajax({
        type: "POST",
        url: http_service + "/userRole/roleList",
        data: {},
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var html="";
                $.each(data.data,function(){
                    var roleFlag=true;
                    var rowKey=this.rowKey;
                    $.each(userRole,function(i){
                        if(rowKey==userRole[i]){
                            roleFlag=false;
                        }
                    });
                    html+="<li";
                    if(!roleFlag){
                        html+=" class=\"activeBg\" ";
                    }
                    html+=" title='"+this.rowKey+"'>"+this.rowKey+"<span></span></li>";
                });
                $("#addUserDivUl").html(html);
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
    $("#addUserType").val("upd");
    $("#userAddName").val(user);
    $("#userAddName").attr("disabled","disabled");
    $(".addUserDiv").show();
    $(".maskPosition").show();
}
function initOGGTrend() {
    /*同步增量行数趋势*/
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getOGGTrend",
        data: {
            "source_schema": schema,
            "source_instance": instance
        },
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                trendData = data.data;
                thermodynamicChartFun("tendency", trendData.days, trendData.months, trendData.data);
                instWSNumberTrendFun();
                setInterval(instWSNumberTrendFun, refreshtime);
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*日志*/
function instHistoryMagDate(data) {
    var html = "";
    $.each(data, function() {
        html += "<li class=\"dateNewLi\">";
        html += "<dl>";
        html += "<dt>";
        html += "<span>" + this.message + "</span>";
        html += "<div class=\"sanjiao\"></div>";
        html += "</dt>";
        html += "</dl>";
        var dateMd = $.split(this.createTime, "日")[0];
        var dateHm = $.split(this.createTime, "日")[1];
        html += "<h3>" + dateMd + "日<span>" + dateHm + "</span></h3>";
        html += "</li>";
    });
    $("#history-date-ul").html(html);
    html='';
}
/*同步增量行数趋势 建立ws*/
function instWSNumberTrendFun() {
    $.ajax({
        type: "POST",
        url: http_service + "/ogg",
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                trendData.data[data.data.indexOf] = data.data.value;
                echarts.dispose(echarts.getInstanceByDom(document.getElementById("tendency")));
                thermodynamicChartFun("tendency", trendData.days, trendData.months, trendData.data);
            }
            clearInterval(this);
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*趋势事件*/
function instWSHistoryMagFun() {
    $.ajax({
        type: "POST",
        url: http_service + "/event",
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                instHistoryMagDate(data.data);
            }
            clearInterval(this);
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}

