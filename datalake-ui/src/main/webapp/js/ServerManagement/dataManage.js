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
    thisMenuFlag="dataManage";
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
    $("#addData").click(function(){
    	getSelectList();
        $("#addType").val("save");
        $(".osUser").val("");//OS用户
        $(".ipAddress").val("");//IP地址(必须字段)
        $(".basePath").val("");//安装地址
        $(".dbHome").val("");//DB_HOME
        $(".dbName").val("");//数据库名(必须字段)
        $(".dbPort").val("");//(数据库端口，当dbType类型是Mysql时显示)
        $(".dbLogPath").val("");//(数据库日志路径，当类型是Mysql时显示)
        $(".rootUser").val("");//(root用户，当类型是Mysql时显示)
        $(".rootPasswd").val("");//(root用户密码，当类型是Mysql时显示)
        $(".password").val("");
        $(".linkUser").val("");//链路用户
        $(".mainProcPort").val("");//主进程端口
        $(".remoteCluster").val("");//目标集群
        $(".rowKey").val("");
        $("#addUpdBokDiv .addTitleCount .addTitleLi").show();
        $("#addUpdBokDiv .tabsCountDiv").hide();
        $("#addUpdBokDiv .tabsCountDiv:eq(0)").show();
        $("#addUpdBokDiv .addTitleCount .addTitleLi").removeClass("on");
        $("#addUpdBokDiv .addTitleCount .addTitleLi:eq(0)").addClass("on");

        $(".addBokDiv").show();
        $(".maskPosition").show();
    });
    $("button.roleCancel").click(function(){
        $(".addBokDiv").hide();
        $(".maskPosition").hide();
    });
    /*orade 保存*/
    $("#oradeTabsCount button.roleInsure").click(function(){
        editSaveFun("oradeTabsCount");
    });
    /*mysql 保存*/
    $("#mysqlTabsCount button.roleInsure").click(function(){
        editSaveFun("mysqlTabsCount");
    });
    /*DB2 保存*/
    $("#DBTabsCount button.roleInsure").click(function(){
        editSaveFun("DBTabsCount");
    });
    /*选项卡点击事件*/
    $(".addBokDiv .addTitleLi").click(function(){
        var index=$(".addBokDiv .addTitleLi").index($(this));
        $(".addBokDiv .addTitleLi").removeClass("on");
        $(this).addClass("on");
        $(".addBokDiv .tabsCountDiv").hide();
        $(".addBokDiv .tabsCountDiv:eq("+index+")").show();
    });
}
/*保存修改*/
function editSaveFun(flg){
    var ipAddress=$("#"+flg+" .ipAddress").val();//IP地址(必须字段)
    var dbName=$("#"+flg+" .dbName").val();//数据库名(必须字段)
    if(ipAddress=="" || ipAddress==null || ipAddress==undefined){
        $("#"+flg+" .title_error").html("IP地址不能为空");
        return false;
    }else{
        $("#"+flg+" .title_error").html("");
    }
    if(dbName=="" || dbName==null || dbName==undefined){
        $("#"+flg+" .title_error").html("数据库名不能为空");
        return false;
    }else{
        $("#"+flg+" .title_error").html("");
    }
    var dataParameter=instEditSaveParameter(flg);
    var addType=$("#addType").val();
    $.ajax({
        type: "POST",
        url: http_service + "/sourceDb/save",
        data:dataParameter,
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                if(data.success){
                    $(".addBokDiv").hide();
                    $(".maskPosition").hide();
                    instWSTrafficListFun();
                }else{
                    $("#"+flg+" .title_error").html(data.message);
                }
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*参数封装*/
function instEditSaveParameter(flg){
    var dbPort=$("#"+flg+" .dbPort").val();
    var dbLogPath=$("#"+flg+" .dbLogPath").val();
    var rootUser=$("#"+flg+" .rootUser").val();
    var rootPasswd=$("#"+flg+" .rootPasswd").val();
    if(dbPort=="" || dbPort==null || dbPort==undefined)dbPort="";
    if(dbLogPath=="" || dbLogPath==null || dbLogPath==undefined)dbLogPath="";
    if(rootUser=="" || rootUser==null || rootUser==undefined)rootUser="";
    if(rootPasswd=="" || rootPasswd==null || rootPasswd==undefined)rootPasswd="";
    var data={};
    data.rowKey=$("#"+flg+" .rowKey").val();
    data.dbType=$("#"+flg+" .dbType").val();
    data.osUser=$("#"+flg+" .osUser").val();//OS用户
    data.ipAddress=$("#"+flg+" .ipAddress").val();//IP地址(必须字段)
    data.basePath=$("#"+flg+" .basePath").val();//安装地址
    data.dbHome=$("#"+flg+" .dbHome").val();//DB_HOME
    data.dbName=$("#"+flg+" .dbName").val();//数据库名(必须字段)
    data.dbPort=dbPort;//(数据库端口，当dbType类型是Mysql时显示)
    data.dbLogPath=dbLogPath;//(数据库日志路径，当类型是Mysql时显示)
    data.rootUser=rootUser;//(root用户，当类型是Mysql时显示)
    data.rootPasswd=rootPasswd;//(root用户密码，当类型是Mysql时显示)
    data.linkUser=$("#"+flg+" .linkUser").val();//链路用户
    data.mainProcPort=$("#"+flg+" .mainProcPort").val();//主进程端口
    data.remoteCluster=$("#"+flg+" .remoteCluster").val();
    data.tenantName=$("#tenantSelect").val();
    return data;
}
/*Ajax 加载*/
function instAjaxFun(){
    /*租户*/
    $.ajax({
        type: "POST",
        url: http_service + "/SchemaManage/getTenants",
        async:false,
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="";
                $.each(data.data,function (i) {
                    html+="<option value =\""+data.data[i]+"\">"+data.data[i]+"</option>"
                });
                $("#tenantSelect").html(html);
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
    $.ajax({
        type:"POST",
        url:http_service+"/sourceDb/list",
        data:{pageFlg:"99",tenantName:$("#tenantSelect").val()},
        dataType:"json",
        success:function(data){
            if(returnDataCheck(data)){
            	var pageRem = data.data.count%pageSize==0?0:1;
                pageCunt=parseInt(data.data.count/pageSize)+pageRem;
                listData=data.data.list;
                var listRem=data.data.list.length%pageSize==0?0:1;
                listPageCunt=parseInt(data.data.list.length/pageSize)+listRem;
                var html="";
                $.each(data.data.list,function(i){
                    if(i==10){
                        return false;
                    }
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.dbType+"</td>";
                    html+="<td>"+this.dbName+"</td>";
                    html+="<td>"+this.ipAddress+"</td>";
                    html+="<td>"+this.mainProcPort+"</td>";
                    html+="<td>"+this.basePath+"</td>";
                    html+="<td><span title='"+this.remoteCluster+"'>"+this.remoteCluster+"</span></td>";
                    html+="<td>";
                    if(this.dbType=="ORACLE" || this.dbType=="MYSQL" || this.dbType=="DB2") {
                        html += "<span class='but' onclick=\"dataUpdFun('" +this.rowKey.replace("\n",":")+ "','" + this.dbType + "')\">编辑</span>";
                    }
                    html+="</td>";
                    html+="</tr>";
                    if(i==0){
                        oneRowKey=this.rowKey;
                    }else{
                        startRowKey=this.rowKey;
                    }
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
    if(page<1){
        page=1;
        return false;
    }else if(page>pageCunt){
        page=pageCunt;
        return false;
    }

    if(page<=listPageCunt){
        var html="";
        for(var i=(page-1)*10;i<page*10;i++){
            var objData=listData[i];
            if(!objData){
                break;
            }
            html+="<tr class='trafficTr'>";
            html+="<td>"+objData.dbType+"</td>";
            html+="<td>"+objData.dbName+"</td>";
            html+="<td>"+objData.ipAddress+"</td>";
            html+="<td>"+objData.mainProcPort+"</td>";
            html+="<td>"+objData.basePath+"</td>";
            html+="<td><span title='"+objData.remoteCluster+"'>"+objData.remoteCluster+"</span></td>";
            html+="<td>";
            if(objData.dbType=="ORACLE" || objData.dbType=="MYSQL" || objData.dbType=="DB2") {
                html += "<span class='but' onclick=\"dataUpdFun('" +objData.rowKey.replace(":","\n")+ "','" + objData.dbType + "')\">编辑</span>";
            }
            html+="</td>";
            html+="</tr>";
        }
        $("#trafficList tr.trafficTr").remove();
        $("#trafficList").append(html);
    }else{
        var pageFlg=(parseInt(page/10)+(page%10==0?0:1))*100-1;
        $.ajax({
            type: "POST",
            url: http_service + "/sourceDb/list",
            data:{pageFlg:pageFlg,tenantName:$("#tenantSelect").val()},
            dataType: "json",
            success: function (data) {
                if (returnDataCheck(data)) {
                    var pageRem = data.data.count%pageSize==0?0:1;
                    pageCunt=parseInt(data.data.count/pageSize)+pageRem;
                    listData=data.data.list;
                    var listRem=data.data.list.length%pageSize==0?0:1;
                    listPageCunt=parseInt(data.data.list.length/pageSize)+listRem;
                    var html="";
                    for(var i=(page-1)*10;i<page*10;i++){
                        var objData=listData[i];
                        if(!objData){
                            break;
                        }
                        html+="<tr class='trafficTr'>";
                        html+="<td>"+objData.dbType+"</td>";
                        html+="<td>"+objData.dbName+"</td>";
                        html+="<td>"+objData.ipAddress+"</td>";
                        html+="<td>"+objData.mainProcPort+"</td>";
                        html+="<td>"+objData.basePath+"</td>";
                        html+="<td><span title='"+objData.remoteCluster+"'>"+objData.remoteCluster+"</span></td>";
                        html+="<td>";
                        if(objData.dbType=="ORACLE" || objData.dbType=="MYSQL" || objData.dbType=="DB2") {
                            html += "<span class='but' onclick=\"dataUpdFun('" +objData.rowKey.replace(":","\n")+ "','" + objData.dbType + "')\">编辑</span>";
                        }
                        html+="</td>";
                        html+="</tr>";
                    }
                    $("#trafficList tr.trafficTr").remove();
                    $("#trafficList").append(html);
                }
            },
            error: function (e) {
            },
            complete: function (XHR, TS) { XHR = null }
        });
    }


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