var parameterData={};
$(function () {
    /*参数初始化*/
    // console.log("schema:"+schema);
    typeof schema != "undefined"?parameterData["source_schema"]=schema:"";
    typeof instance != "undefined"?parameterData["source_instance"]=instance:"";
    /*同步增量行数趋势 初始化*/
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getOGGTrend",
        data:parameterData,
        dataType: "json",
        success: function (data) {
            if (returnDataCheck(data)) {
                trendData = data.data;
                thermodynamicChartFun("tendency",trendData.days,trendData.months,trendData.data);
                instWSNumberTrendFun();
                setInterval(instWSNumberTrendFun,refreshtime);
            }
        },
        error: function (e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
    /*趋势事件 初始化*/
    trendEventFun();
    setInterval(trendEventFun,refreshtime);
});
/*同步增量行数趋势*/
function instWSNumberTrendFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/ogg",
        dataType:"json",
        data:parameterData,
        success:function(data){
            if(returnDataCheck(data)){
                trendData.data[data.data.indexOf]=data.data.value;
                thermodynamicChartFun("tendency",trendData.days,trendData.months,trendData.data);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*趋势事件*/
function trendEventFun(){
    $.ajax({
        type:"POST",
        url:http_service+"/eventDetail",
        dataType:"json",
        data:parameterData,
        success:function(data){
            if(returnDataCheck(data)){
                trendEventDate(data.data);
            }
        },
        error: function(e) {
        },
        complete: function (XHR, TS) { XHR = null }
    });
}
/*趋势事件 填充html*/
function trendEventDate(data){
    var html="";
    $.each(data,function(){
        html+="<li class=\"dateNewLi\">";
        html+="<dl>";
        html+="<dt>";
        html+="<span>"+this.message+"</span>";
        html+="<div class=\"sanjiao\"></div>";
        html+="</dt>";
        html+="</dl>";
        var dateMd=$.split(this.createTime,"日")[0];
        var dateHm=$.split(this.createTime,"日")[1];
        html+="<h3>"+dateMd+"日<span>"+dateHm+"</span></h3>";
        html+="</li>";
    });
    $("#history-date-ul").html(html);
}