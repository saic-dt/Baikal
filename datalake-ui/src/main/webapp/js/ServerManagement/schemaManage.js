/**
 * Created by wangwenhu on 17/12/12.
 */
var trendData=[];/*同步增量行数趋势 数据*/
var schema="",instance="",count="";/*url参数*/
var page=1;
var refreshtime = 10000;
var startRowKey="",oneRowKey="",pageSize=10,pageCunt=0;
$(function(){
    //获取菜单实例
    thisMenuFlag="schemaManage";
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
    //添加按钮
    $("#addSchema").click(function(){
        $("#titleError").html("");
    	$("#jdbcUrl").val("");
        $("#user").val("");
        $("#password").val("");
        $("#schema").val("");
        $("#dbType").val("");
        $("#linkSelect").html("");
        $("#serviceVal").html("");
        $(".addBokDiv").show();
        $(".maskPosition").show();
    });
    /*取消弹出框*/
    $("button.roleCancel").click(function(){
        $(".addBokDiv").hide();
        $(".maskPosition").hide();
    });
    /*确认弹出框*/
    $("button.roleInsure").click(function(){
        saveFun();
    });
    /*链路选择事件*/
    $("#linkSelect").change(function(){
        var serviceVal=$(this).find("option:selected").attr("serviceVal");
        $("#serviceVal").html(serviceVal);
    });
    $("#dbType").change(function(){
        var dbType=$(this).val();
        getSelectFun(dbType);
        $("#serviceVal").html("");
    });
}
/*保存*/
function saveFun(){
    var jdbcUrl=$("#jdbcUrl").val();
    var user=$("#user").val();
    var password=$("#password").val();
    var schema=$("#schema").val();
    var dbType=$("#dbType").val();
    var dbName=$("#linkSelect").val();
    var rmtIp=$("#linkSelect").find("option:selected").attr("rmtIpVal");

    if(jdbcUrl=="" || jdbcUrl==null || jdbcUrl==undefined){
        $("#titleError").html("JDBC-url不能为空!");
        return false;
    }
    if(dbType!="MONGO"){
        if(user=="" || user==null || user==undefined){
            $("#titleError").html("连接用户不能为空!");
            return false;
        }
        if(password=="" || password==null || password==undefined){
            $("#titleError").html("用户密码不能为空!");
            return false;
        }
    }
    if(schema=="" || schema==null || schema==undefined){
        $("#titleError").html("SCHEMA不能为空!");
        return false;
    }
    if(dbType=="" || dbType==null || dbType==undefined){
        $("#titleError").html("类型不能为空!");
        return false;
    }
    if(dbType!="MONGO"){
        if(dbName=="" || dbName==null || dbName==undefined){
            $("#titleError").html("链路不能为空!");
            return false;
        }
    }
    $.ajax({
        type: "POST",
        url: http_service + "/SchemaManage/save",
        data:{jdbcUrl:jdbcUrl,user:user,password:password,schema:schema,dbType:dbType,dbName:dbName,rmtIp:rmtIp,tenantName:$("#tenantSelect").val()},
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                if(data.success){
                    $(".addBokDiv").hide();
                    $(".maskPosition").hide();
                    instWSTrafficListFun();
                }else{
                    $("#titleError").html(data.message);
                }
            }
        },
        error: function (e) {
        }
    });
}

/*Ajax 加载*/
function instAjaxFun(){
    /*类型下拉框加载*/
    $.ajax({
        type: "POST",
        url: http_service + "/SchemaManage/dbTypeList",
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="<option value=\"\">请选择</option>";
                $.each(data.data,function(k,v){
                    html+="<option value=\""+v+"\">"+k+"</option>"
                });
                $("#dbType").append(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
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
function getSelectFun(dbType){
    /*链路下拉框加载*/
    $.ajax({
        type: "POST",
        url: http_service + "/SchemaManage/getSelectList",
        data:{dbType:dbType,tenantName:$("#tenantSelect").val()},
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="<option value=\"\">请选择</option>";
                $.each(data.data,function(key,value){
                    var val=$.split(value.rowKey,"\n")[1];
                    var serviceVal=JSON.stringify(value);
                    var rmtIpVal= value.rmtIp;
                    html+="<option value=\""+val+"\" serviceVal='"+serviceVal+"' rmtIpVal='"+rmtIpVal+"'>"+key+"</option>"
                });
                $("#linkSelect").html("");
                $("#linkSelect").html(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*跳转上级页面*/
function skipSuperior(){
    window.location.href =  web_http+"/dashboard.html?schema="+schema+"&instance="+instance+"&timestamp="+$.now();
}
/*列表 建立ws*/
function instWSTrafficListFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/SchemaManage/getList",
        data:{pageSize:pageSize,tenantName:$("#tenantSelect").val()},
        dataType:"json",
        success:function(data){
            if(returnDataCheck(data)){
            	var pageRem = data.data.count%pageSize==0?0:1;
                pageCunt=parseInt(data.data.count/pageSize)+pageRem;
                var html="";
                $.each(data.data.list,function(i){
                    html+="<tr class='trafficTr'>";
                    html+="<td><span>"+this.dbType+"</span></td>";
                    html+="<td><span>"+this.dbName+"</span></td>";
                    html+="<td><span>"+this.schema+"</span></td>";
                    html+="<td><span>"+this.ip+"</span></td>";
                    html+="<td><span  title='"+this.targetCluster+"'>"+this.targetCluster+"</span></td>";
                    html+="<td><span class='but' onclick=\"dataDeleteFun('"+ this.rowKey.replace(/\n/g,":")+"')\">删除</span></td>";
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
    var rowKey="";
    if(flag==1){
        rowKey=startRowKey;
    }else if(flag==0 || flag==2){
        rowKey=oneRowKey;
    }
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
    $.ajax({
        type: "POST",
        url: http_service + "/SchemaManage/getList",
        data:{startRowKey:rowKey,pageFlg:flag,pageSize:pageSize,tenantName:$("#tenantSelect").val()},
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
            	var pageRem = data.data.count%pageSize==0?0:1;
                pageCunt=parseInt(data.data.count/pageSize)+pageRem;
                var html="";
                $.each(data.data.list,function(i){
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.dbType+"</td>";
                    html+="<td>"+this.dbName+"</td>";
                    html+="<td>"+this.schema+"</td>";
                    html+="<td>"+this.ip+"</td>";
                    html+="<td>"+this.targetCluster+"</td>";
                    html+="<td><span class='but' onclick=\"dataDeleteFun('"+ this.rowKey.replace(/\n/g,":")+"')\">删除</span></td>";
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
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}

function dataDeleteFun(rowKey){
    alertFn("删除确认","确定要删除信息?","如果是请点确认，否则取消",false,insureDeleteFun);
    function insureDeleteFun() {
        $.ajax({
            type: "POST",
            url: http_service + "/SchemaManage/delete",
            data:{rowKey:rowKey.replace(":","\n"),tenantName:$("#tenantSelect").val()},
            dataType: "json",
            success: function (data) {
                if (returnDataCheck(data)) {
                    PreviousNextPage(2);
                }
            },
            error: function (e) {
            },
            complete: function (XHR, TS) { XHR = null }
        });
    }

}
