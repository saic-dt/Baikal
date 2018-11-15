/**
 * Created by wangwenhu on 17/12/12.
 */
var trendData=[];/*同步增量行数趋势 数据*/
var schema="",instance="",count="";/*url参数*/
var page=1,listData=[],listPageCunt=0;/*当前页码*/
var refreshtime = 10000;
var startRowKey="",oneRowKey="",pageSize=10,pageCunt=0;
$(function(){
    //获取菜单实例
    thisMenuFlag="operationLog";
    initMenu();
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width:$("#tendency").width(),
        height:$("#tendency").width()
    });
    /*事件加载*/
    instFun();
    /*Ajax 加载*/
    instAjaxFun();
    /*列表*/
    instWSTrafficListFun();
});
/*事件加载*/
function instFun(){
    $("#queryLog").click(function () {
        instWSTrafficListFun();
    });
    /*时间空间初始化 tab2*/
    $.datetimepicker.setLocale('ch');
    /* 时间 开始*/
    $('#startTimeLog').datetimepicker({
        format: 'Y-m-d H:i:s',
        onShow: function(ct, e) {
            this.setOptions({
                maxDate: $('#endTimeLog').val() ? $('#endTimeLog').val() : false
            })
        }
    });
    /* 时间 结束*/
    $('#endTimeLog').datetimepicker({
        format: 'Y-m-d H:i:s',
        onShow: function(ct, e) {
            this.setOptions({
                minDate: $('#startTimeLog').val() ? $('#startTimeLog').val() : false
            })
        }
    });
}
/*Ajax 加载*/
function instAjaxFun(){
    /*类型下拉框加载*/
    $.ajax({
        type: "POST",
        url: http_service + "/operationLog/getDbType",
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="<option value=\"\">全部</option>";
                $.each(data.data,function(){
                    html+="<option value=\""+this+"\">"+this+"</option>"
                });
                $("#dbTypeLog").append(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
    /*操作类型下拉框加载*/
    $.ajax({
        type: "POST",
        url: http_service + "/operationLog/getOperationType",
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="<option value=\"\">全部</option>";
                $.each(data.data,function(){
                    html+="<option value=\""+this+"\">"+this+"</option>"
                });
                $("#opterateTypeLog").append(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });

}

function getSelectList(){
    /*目标集群*/
    $.ajax({
        type: "POST",
        url: http_service + "/sourceDb/clusterList",
        data:{tenantName:$("#tenantSelect").val()},
        dataType: "json",
        async : false,
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="<option value=\"\">请选择</option>";
                $.each(data.data,function(){
                    html+="<option value=\""+this+"\">"+this+"</option>";
                });
                $(".clusterSelect").html(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*列表*/
function instWSTrafficListFun(){
    page=1;
    $.ajax({
        type:"POST",
        url:http_service+"/operationLog/getList",
        data:{
            "user_name":$("#userNameLog").val(),
            "source_schema":$("#sourceSchemaLog").val(),
            "pageIndex":page,
            "pageSize":10,
            "source_table":$("#sourceTableLog").val(),
            "db_type":$("#dbTypeLog").val(),
            "opterate_type":$("#opterateTypeLog").val(),
            "startTime":$("#startTimeLog").val(),
            "endTime":$("#endTimeLog").val()
        },
        dataType:"json",
        success:function(data){
            if(returnDataCheck(data)){
                var html="";
                $.each(data.data,function(i){
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.user_name+"</td>";
                    html+="<td>"+this.opterate_type+"</td>";
                    html+="<td>"+this.db_type+"</td>";
                    html+="<td>"+this.source_schema+"</td>";
                    html+="<td><span title='"+this.source_table+"'>"+this.source_table+"</span></td>";
                    html+="<td><span>"+this.operation_time+"</span></td>";
                    html+="<td style='padding-right: 10px;'><span title='"+this.message+"'>"+this.message+"</span></td>";
                    html+="</tr>";
                });
                $("#trafficList tr.trafficTr").remove();
                $("#trafficList").append(html);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
function PreviousNextPage(flag){
    if(flag==1){
        page++;
    }else if(flag==0){
        page--;
    }
    $.ajax({
        type:"POST",
        url:http_service+"/operationLog/getList",
        data:{
            "user_name":$("#userNameLog").val(),
            "source_schema":$("#sourceSchemaLog").val(),
            "pageIndex":page,
            "pageSize":10,
            "source_table":$("#sourceTableLog").val(),
            "db_type":$("#dbTypeLog").val(),
            "opterate_type":$("#opterateTypeLog").val(),
            "startTime":$("#startTimeLog").val(),
            "endTime":$("#endTimeLog").val()
        },
        dataType:"json",
        success:function(data){
            if(returnDataCheck(data)){
                var html="";
                $.each(data.data,function(i){
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.user_name+"</td>";
                    html+="<td>"+this.opterate_type+"</td>";
                    html+="<td>"+this.db_type+"</td>";
                    html+="<td>"+this.source_schema+"</td>";
                    html+="<td><span title='"+this.source_table+"'>"+this.source_table+"</span></td>";
                    html+="<td><span>"+this.operation_time+"</span></td>";
                    html+="<td style='padding-right: 10px;'><span title='"+this.message+"'>"+this.message+"</span></td>";
                    html+="</tr>";
                });
                $("#trafficList tr.trafficTr").remove();
                $("#trafficList").append(html);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
function dataUpdFun(rowKey,type){
    getSelectList();
    $.ajax({
        type: "POST",
        url: http_service + "/sourceDb/edit",
        data:{rowKey:rowKey,tenantName:$("#tenantSelect").val()},
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                $("#addType").val("edit");
                var idDiv="";
                $("#addUpdBokDiv .addTitleCount .addTitleLi").hide();
                $("#addUpdBokDiv .tabsCountDiv").hide();
                if(type=="ORACLE"){
                    idDiv="oradeTabsCount";
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(0)").show();
                    $("#addUpdBokDiv .tabsCountDiv:eq(0)").show();
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(0)").addClass("on");
                }else if(type=="MYSQL"){
                    idDiv="mysqlTabsCount";
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(1)").show();
                    $("#addUpdBokDiv .tabsCountDiv:eq(1)").show();
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(1)").addClass("on");
                }else if(type=="DB2"){
                    idDiv="DBTabsCount";
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(2)").show();
                    $("#addUpdBokDiv .tabsCountDiv:eq(2)").show();
                    $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(2)").addClass("on");
                }
                $("#"+idDiv+" .rowKey").val(rowKey);//rowKey
                $("#"+idDiv+" .osUser").val(data.data.osUser);//OS用户
                $("#"+idDiv+" .ipAddress").val(data.data.ipAddress);//IP地址(必须字段)
                $("#"+idDiv+" .basePath").val(data.data.basePath);//安装地址
                $("#"+idDiv+" .dbHome").val(data.data.dbHome);//DB_HOME
                $("#"+idDiv+" .dbName").val(data.data.dbName);//数据库名(必须字段)
                $("#"+idDiv+" .dbPort").val(data.data.dbPort);//(数据库端口，当dbType类型是Mysql时显示)
                $("#"+idDiv+" .dbLogPath").val(data.data.dbLogPath);//(数据库日志路径，当类型是Mysql时显示)
                $("#"+idDiv+" .rootUser").val(data.data.rootUser);//(root用户，当类型是Mysql时显示)
                $("#"+idDiv+" .rootPasswd").val(data.data.rootPasswd);//(root用户密码，当类型是Mysql时显示)
                $("#"+idDiv+" .linkUser").val(data.data.linkUser);//链路用户
                $("#"+idDiv+" .mainProcPort").val(data.data.mainProcPort);//主进程端口
                $("#"+idDiv+" .remoteCluster").val(data.data.remoteCluster);//目标集群
                $(".addBokDiv").show();
                $(".maskPosition").show();
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}