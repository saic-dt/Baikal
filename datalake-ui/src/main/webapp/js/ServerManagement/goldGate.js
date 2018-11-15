/*GoldenGate监控面板*/
var trendData=[];/*同步增量行数趋势 数据*/
var searchQueryData=[];/*检索查询原数据*/
var schema="",instance="",syncCount="",count="";/*url参数*/
var trendStatisticsWS=null;/*同步增量行数趋势统计图*/
var trafficListWs=null;/*流量列表*/
var page=1;
var refreshtime = 10000;
$(function(){
    //获取菜单实例
    thisMenuFlag="linkMonitoring";
    initMenu();
    /*获得url参数*/
    schema=$.QueryString("schema");
    instance=$.QueryString("instance");
    //syncCount=$.QueryString("syncCount");
    //count=$.QueryString("cot");
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width:$("#tendency").width(),
        height:$("#tendency").width()
    });
    /*Ajax 加载*/
    instAjaxFun();
    /*事件加载*/
    instFun();
    /*同步增量行数趋势统计图 */
    instWSHistoryMagStatisticsFun();
    setInterval(instWSHistoryMagStatisticsFun,refreshtime);
    /*流量列表*/
    instWSTrafficListFun();
    setInterval(instWSTrafficListFun,refreshtime);
    /*进程状态*/
    instWSprocess_stateFun();
    setInterval(instWSprocess_stateFun,refreshtime);
    /*进程延时趋势*/
    instWSProcessDelayFun();
    setInterval(instWSProcessDelayFun,refreshtime);
});
/*事件加载*/
function instFun(){
    /*检索查询输入框输入*/
    $("#searchQuery").on('keyup',function (event) {
        var val=$(this).val();
        searchQueryFun(val);
    });
    /*检索查询输入框获得光标*/
    $("#searchQuery").focus(function(){
        var val=$(this).val();
        searchQueryFun(val);
    });
    /*检索查询内容复选框点击事件*/
    $("#searchQuerySorting ul.clearfix").on('click',"li.searchQueryLi",function (event) {
        if($(this).find("em").hasClass("checkEm")){
            $(this).find("em").removeClass("checkEm");
        }else{
            var leg=$("#searchQuerySorting li.searchQueryLi em.checkEm").length;
            if(leg>=5){
                return false;
            }
            $(this).find("em").addClass("checkEm");
        }
    });
    /*空白处点击*/
    $("#searchMask").click(function(){
        $("#searchQuerySorting").hide();
        $("#searchMask").hide();
    });
    /**/
    $("#searchQueryGlyphicon").click(function(){
        var val=$("#searchQuery").val();
        searchQueryFun(val);
    });
    //hover
    $(".sorting ul.clearfix").on("mouseenter","li p",function(){
        var spanH=$(this).find("span").width();
        var pTwo= $(this).width();
        if(spanH>pTwo){
            var leg=$(".sorting ul.clearfix li  p").index($(this))+1;
            var num=leg%2;
            if(num==0){
                $(this).css("width","200%");
                $(this).css("z-index","1");
                $(this).animate({right:"0%"});
            }else{
                $(this).css("width","200%");
                $(this).css("z-index","1");
                $(this).animate({left:"0%"});
            }
        }
    });
    $(".sorting ul.clearfix").on("mouseleave","li p",function(){
        var li= $(this).parent().width()*2;
        var pTwo= $(this).width();
        if(pTwo==li){
            var leg=$(".sorting ul.clearfix li  p").index($(this))+1;
            var num=leg%2;
            if(num==0){
                $(this).css("width","");
                $(this).css("z-index","0");
                $(this).animate({right:""});
            }else{
                $(this).css("width","");
                $(this).css("z-index","0");
                $(this).animate({left:""});
            }
        }
    });
}
/*Ajax 加载*/
function instAjaxFun(){
    /*同步增量行数趋势*/
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getTableLSchemaInstance",
        data:{"source_schema":schema,"source_instance":instance},
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                searchQueryData=data.data;
                var html="";
                $.each(searchQueryData,function(i){
                    html+="<li class='searchQueryLi'><p><span>"+searchQueryData[i]+"</span><em></em></p></li>";
                });
                $("#searchQuerySorting ul.clearfix .searchQueryLi").remove();
                $("#searchQuerySorting ul.clearfix").prepend(html);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*检索查询输入检索*/
function searchQueryFun(val){
    var selectedData=[];
    $.each($("#searchQuerySorting li.searchQueryLi em"),function(){
        if($(this).hasClass("checkEm")){
            selectedData.push($(this).parent().find("span").text());
        }
    });
    var searchData=[];
    if(val=="" || val==null || val==undefined){
        searchData=searchQueryData;
    }else{
        $.each(searchQueryData,function(i){
            var thisVal=searchQueryData[i].toLocaleLowerCase();
            if(thisVal.indexOf(val.toLocaleLowerCase())>=0){
                searchData.push(searchQueryData[i]);
            }
        });
    }
    var searchCopyData=[];
    $.each(searchData,function(i){
        var flag=true;
        $.each(selectedData,function(j){
            if(searchData[i]==selectedData[j]){
                flag=false;
                return false;
            }
        });
        if(flag){
            searchCopyData.push(searchData[i]);
        }
    });
    var html="";
    $.each(selectedData,function(i){
        html+="<li class='searchQueryLi'><p><span>"+selectedData[i]+"</span><em class='checkEm'></em></p></li>";
    });
    $.each(searchCopyData,function(i){
        html+="<li class='searchQueryLi'><p><span>"+searchCopyData[i]+"</span><em></em></p></li>";
    });
    $("#searchQuerySorting ul .searchQueryLi").remove();
    $("#searchQuerySorting ul").prepend(html);
    $("#searchQuerySorting").show();
    $("#searchMask").show();
}
function PreviousNextPage(flag){
    if(flag==0){
        page++;
    }else if(flag==1){
        page--;
    }
    if(page<1){
        page=1;
    }
    var tables="";
    $.each($("#searchQuerySorting li.searchQueryLi"),function(){
        if( $(this).find("em").is(".checkEm")){
            if(tables=="" || tables==null || tables==undefined){
                tables+=$(this).find("span").text();
            }else{
                tables+=","+$(this).find("span").text();
            }
        }
    });
    var data= {"source_tables":tables,"source_schema":schema,"source_instance":instance,keyword:"",pageIndex:page};
    var dataUrl=data;
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getMonitorTableListByPage",
        data:dataUrl,
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                var html="";
                $.each(data.data,function(){
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.createTime+"</td>";
                    html+="<td>"+this.source_instance+"</td>";
                    html+="<td>"+this.source_schema+"</td>";
                    html+="<td>"+this.source_table+"</td>";
                    html+="<td>"+this.insert_rows+"</td>";
                    html+="<td>"+this.update_rows+"</td>";
                    html+="<td>"+this.delete_rows+"</td>";
                    html+="<td>"+this.total_rows+"</td>";
                    html+="</tr>";
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
/*查询*/
function queryAjaxFun(){
    var tables="";
    $.each($("#searchQuerySorting li.searchQueryLi"),function(){
        if( $(this).find("em").is(".checkEm")){
            if(tables=="" || tables==null || tables==undefined){
                tables+=$(this).find("span").text();
            }else{
                tables+=","+$(this).find("span").text();
            }
        }
    });
    //var data= {"source_tables":tables,"source_schema":schema,"source_instance":instance,keyword:""};
    //var dataUrl=JSON.stringify(data);
    instWSHistoryMagStatisticsFun();
    instWSTrafficListFun();
    //trendStatisticsWS.send(dataUrl);/*同步增量行数趋势统计图*/
    //trafficListWs.send(dataUrl);/*流量列表*/

    $("#searchQuerySorting").hide();
    $("#searchMask").hide();
}
/*全部取消*/
function cancelAll(){
    $("#searchQuerySorting li.searchQueryLi em").removeClass("checkEm");
}
/*跳转上级页面*/
function skipSuperior(){
    window.location.href =  web_http+"/dashboard.html?schema="+schema+"&instance="+instance+"&timestamp="+$.now();
}


/*同步增量行数趋势统计图 建立ws*/
function instWSHistoryMagStatisticsFun(){
    var tables="";
    $.each($("#searchQuerySorting li.searchQueryLi"),function(){
        if( $(this).find("em").is(".checkEm")){
            if(tables=="" || tables==null || tables==undefined){
                tables+=$(this).find("span").text();
            }else{
                tables+=","+$(this).find("span").text();
            }
        }
    });
    $.ajax({
        type:"POST",
        url:http_service+"/getGoldGateDashboard",
        dataType:"json",
        data:{"source_tables":tables,"source_schema":schema,"source_instance":instance,keyword:""},
        success:function(data){
            if(returnDataCheck(data)){
                var dataLine=data.data[0];
                var dataPie=data.data[1];
                var dataPieTop=data.data[2];
                /*折线图*/
                echarts.dispose(echarts.getInstanceByDom(document.getElementById("OGGtend")));
                lineChartFun("OGGtend",dataLine.xArr,dataLine.legendArr,dataLine.series,false);
                /*饼图*/
                echarts.dispose(echarts.getInstanceByDom(document.getElementById("OGGcount")));
                pieChartFun("OGGcount",dataPie.series[0].name,dataPie.legendArr,dataPie.series[0].data,false);
                /*比例图*/
                echarts.dispose(echarts.getInstanceByDom(document.getElementById("OGGcount1")));
                proportionChartFun("OGGcount1",dataPieTop.legendArr,dataPieTop.series,false);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*流量列表 建立ws*/
function instWSTrafficListFun(){
    var tables="";
    $.each($("#searchQuerySorting li.searchQueryLi"),function(){
        if( $(this).find("em").is(".checkEm")){
            if(tables=="" || tables==null || tables==undefined){
                tables+=$(this).find("span").text();
            }else{
                tables+=","+$(this).find("span").text();
            }
        }
    });
    $.ajax({
        type:"POST",
        url:http_service+"/getMonitorTableRowsList",
        dataType:"json",
        data:{"source_tables":tables,"source_schema":schema,"source_instance":instance,keyword:""},
        success:function(data){
            if(returnDataCheck(data)){
                var html="";
                $.each(data.data,function(){
                    html+="<tr class='trafficTr'>";
                    html+="<td>"+this.createTime+"</td>";
                    html+="<td>"+this.source_instance+"</td>";
                    html+="<td>"+this.source_schema+"</td>";
                    html+="<td>"+this.source_table+"</td>";
                    html+="<td>"+this.insert_rows+"</td>";
                    html+="<td>"+this.update_rows+"</td>";
                    html+="<td>"+this.delete_rows+"</td>";
                    html+="<td>"+this.total_rows+"</td>";
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
/*进程状态*/
function instWSprocess_stateFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/goldGateProcState",
        dataType:"json",
        data:{"source_schema":schema,"source_instance":instance,"keyword":""},
        success:function(data){
            if(returnDataCheck(data)){
            	var dt = data.data;
                if(dt.eprocState=="STOPPED"){
                    $("#process_state .e-process span").html(dt.eprocName);
                    $("#e_process_rows").html(dt.eprocRows);
                    $("#process_state .e-process img").attr("src","images/lianlu1.png");
                }else if(dt.eprocState=="RUNNING"){
                    $("#process_state .e-process span").html(dt.eprocName);
                    $("#e_process_rows").html(dt.eprocRows);
                    $("#process_state .e-process img").attr("src","images/lianlu.png");
                }
                if(dt.dprocState=="STOPPED"){
                    $("#process_state .d-process span").html(dt.dprocName);
                    $("#d_process_rows").html(dt.dprocRows);
                    $("#process_state .d-process img").attr("src","images/lianlu1.png");
                }else if(dt.dprocState=="RUNNING"){
                    $("#process_state .d-process span").html(dt.dprocName);
                    $("#d_process_rows").html(dt.dprocRows);
                    $("#process_state .d-process img").attr("src","images/lianlu.png");
                }
                if(dt.rprocState=="STOPPED"){
                    $("#process_state .r-process span").html(dt.rprocName);
                    $("#r_process_rows").html(dt.rprocRows);
                    $("#process_state .r-process img").attr("src","images/lianlu1.png");
                }else if(dt.rprocState=="RUNNING"){
                    $("#process_state .r-process span").html(dt.rprocName);
                    $("#r_process_rows").html(dt.rprocRows);
                    $("#process_state .r-process img").attr("src","images/lianlu.png");
                }
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*进程延时趋势 建立ws*/
function instWSProcessDelayFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/goldGateProcTrend",
        dataType:"json",
        data:{"source_schema":schema,"source_instance":instance,"keyword":""},
        success:function(data){
            if(returnDataCheck(data)){
                dataLine=data.data;
                echarts.dispose(echarts.getInstanceByDom(document.getElementById("timeDelay")));
                doubleLineChartFun("timeDelay",dataLine.xArr,dataLine.legendArr,dataLine.series,false);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}

