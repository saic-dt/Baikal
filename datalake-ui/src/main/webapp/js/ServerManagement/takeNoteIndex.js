/**
 * Created by wangwenhu on 17/10/20.
 */
var labelTwoInit=false;
var dataBaseList=[];
$(function(){//获取菜单实例
    thisMenuFlag="dataAssets";/*设置菜单选中*/
    $(".main-contentH").css("height","1022px");
    initMenu();
    /*事件加载*/
    clickFun();
    /*ajax加载*/
    initAjaxFun();
});
/*事件方法*/
function clickFun(){
    /*标签点击事件*/
    $("#labelOl li").click(function(){
        var index=$("#labelOl li").index($(this));
        $("#labelOl li").removeClass("activeLi");
        $("#labelOl li:eq("+index+")").addClass("activeLi");
        $(".labelContDiv").hide();
        $(".labelContDiv:eq("+index+")").show();
        if(!labelTwoInit && index==1){
            labelTwoTableAjax();
            labelTwoCharts();
        }
        if(index==0){
            $(".main-contentH").css("height","1022px");
        }else if(index==1){
            $(".main-contentH").css("height","750px");
        }
    });
    /*时间空间初始化 tab2*/
    $.datetimepicker.setLocale('ch');
    /*标签1中 时间 开始*/
    $('#labelOneBeginDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initLabelOneChart();
        }
    });
    $('#labelOneBeginDate').val(GetDateStr(-7));
    $('#labelOneEndDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initLabelOneChart();
        }
    });
    $('#labelOneEndDate').val(GetDateStr(0));
    /*标签1中 时间 结束*/
    /*标签2中 统计日期*/
    $('#statisticsDate').val(GetDateStr(0));
    $('#statisticsDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            labelTwoTableAjax();
        }
    });
    /*归属企业选择*/
    $("#enterpriseCont").on("click",".check",function(){
        var leg=$("#enterpriseCont .item .check:checkbox:checked").length;
        if(leg==0){
            $("#selectDept").html("");
        }else{
            $("#selectDept").html("已选择"+leg+"个部门");
        }
        labelTwoTableAjax();
        labelTwoCharts();

    });
    /* 归属企业打开*/
    $('#selectDept').click(function(event) {
        var sibling = $(this).siblings('.pop-dialog');
        if (sibling.hasClass('show')) {
            sibling.removeClass('show');
        } else {
            sibling.addClass('show');
        }
    });
    /* 归属企业隐藏*/
    $('.pop-dialog .settings .close-icon').click(function(event) {
        $('.pop-dialog').removeClass('show');
    });
    /*标签2中 时间 开始*/
    $('#labelTwoBeginDate').val(GetDateStr(-7));
    $('#labelTwoBeginDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            labelTwoCharts();
        }
    });
    $('#labelTwoEndDate').val(GetDateStr(0));
    $('#labelTwoEndDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            labelTwoCharts();
        }
    });
    /*标签2中 时间 结束*/
    /*数据库检索*/
    $("#dataBaseSearch").keyup(function(){
        var val=$(this).val();
        var html="";
        $.each(dataBaseList,function(i){
            if(dataBaseList[i].indexOf(val)>=0){
                html += '<li title="' + dataBaseList[i] + '" style="cursor: pointer;">' + dataBaseList[i] + '</li>';
            }
        });
        $('#dataBaseList').html('');
        $('#dataBaseList').append(html);
    });
    /**/
    $("#dataBaseList").on("click","li",function(){
        var name=$(this).text();
        window.location.href = web_http+"/takeNoteDB.html?name="+$.encodeBase64(name,3);
    });

}
/*ajax加载*/
function initAjaxFun(){
    /*查询右边 所有数据库信息*/
    $.ajax({
        url: http_service_bases + '/dbTree/DatabaseView',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                dataBaseList = data.dbName;
                var html = '';
                for (var i = 0; i < dataBaseList.length; i++) {
                    html += '<li title="' + dataBaseList[i] + '"  style="cursor: pointer;">' + dataBaseList[i] + '</li>';
                }
                $('#dataBaseList').html('');
                $('#dataBaseList').append(html);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    });
    /*加载12个图表*/
    initLabelOneChart();
    /*归属企业加载 开始*/
    var enterpriseHtml="";
    $.each(enterpriseDtat,function(){
        enterpriseHtml+="<div class=\"item\">";
        enterpriseHtml+="<span class=\"glyphicon glyphicon-align-justify\"></span>";
        enterpriseHtml+="<em valText='"+this.value+"'>"+this.name+"</em>";
        enterpriseHtml+="<input type=\"checkbox\"  class=\"check\">";
        enterpriseHtml+="</div>";
    });
    $("#enterpriseCont").html(enterpriseHtml);
    /*归属企业加载 结束*/
}
/*标签1中 12个图表*/
function initLabelOneChart() {
    var startInput = $('#labelOneBeginDate').val();
    var endInput = $('#labelOneEndDate').val();
    if (startInput == '' || endInput == '') {
        console.log('error:开始和结束日期不能为空');
        return;
    }
    $.ajax({
        url: http_service_bases + '/Tab1/newEnergyData',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: startInput,
            endDate: endInput
        },
        success: function(data) {
            labelLineChartFun('labelOneE1', data, 'ip24Count');
            labelLineChartFun('labelOneE2', data, 'ep11Count');
            labelLineChartFun('labelOneE3', data, 'rx5Count');
            labelLineChartFun('labelOneE4', data, 'ip24VINCount');
            labelLineChartFun('labelOneE5', data, 'ep11VINCount');
            labelLineChartFun('labelOneE6', data, 'rx5VINCount');
            dateChartFun('labelOneD1', data, 'ip24Count',"2017");
            dateChartFun('labelOneD2', data, 'ep11Count',"2017");
            dateChartFun('labelOneD3', data, 'rx5Count',"2017");
            dateChartFun('labelOneD4', data, 'ip24VINCount',"2017");
            dateChartFun('labelOneD5', data, 'ep11VINCount',"2017");
            dateChartFun('labelOneD6', data, 'rx5VINCount',"2017");
        },
        error: function(e) {}
    });
}
/*标签2 列表数据*/
function labelTwoTableAjax(){
    var top="";
    if(!labelTwoInit){
        top=3;
    }
    var endDate = $('#statisticsDate').val();
    if (endDate == '') {
        console.log('error:开始和结束日期不能为空');
        return;
    }
    var dbName=[];/*归属企业*/
    $('#enterpriseCont .item .check').each(function(index, el) {
        if ($(this)[0].checked == true) {
            var name = $(this).siblings('em').attr("valText");
            dbName.push(name)
        }
    });
    $.ajax({
        url: http_service_bases + '/Tab1/compData',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: endDate,
            endDate: endDate,
            top: top,
            compaNames: JSON.stringify(dbName)
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                var html = '';
                var list = data.data;
                for (var i = 0; i < list.length; i++) {
                    html += '<tr>';
                        html += '<td>' + list[i][0] + '</td>';
                        html += '<td>' + list[i][1] + '</td>';
                        html += '<td>' + list[i][2] + '</td>';
                        html += '<td>' + list[i][3] + '</td>';
                        html += '<td>' + list[i][4] + '</td>';
                    html += '</tr>'
                }
                $('#tableCase tbody').html('');
                $('#tableCase tbody').append(html);
            } else if (data.success == 'false' || data.success == false) {

            }
        },
        error: function(e) {}
    });
}
/*标签2 图表数据*/
function labelTwoCharts() {
    var top = '',dbName=[];
    $('#enterpriseCont .item .check').each(function(index, el) {
        if ($(this)[0].checked == true) {
            var name = $(this).siblings('em').attr("valText");
            dbName.push(name)
        }
    });
    if(!labelTwoInit){
        top=3;
    }else{
        labelTwoInit=true;
    }
    var startDate =$('#labelTwoBeginDate').val();
    var endDate =$('#labelTwoEndDate').val();
    $.ajax({
        url: http_service_bases + '/Tab1/compData',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: startDate,
            endDate: endDate,
            top: top,
            compaNames: JSON.stringify(dbName)
        },
        success: function(data) {
            var list = data.data;
            var tab2_ChartData = {};
            var keyName = list[0][2];
            //重构图表数据结构
            for (var i = 0; i < list.length; i++) {
                var seriesArr = list[i][4];
                var key = list[i][2];
                var xArr = list[i][0];
                if (tab2_ChartData[key]) {
                    tab2_ChartData[key][0].push(xArr);
                    tab2_ChartData[key][1].push(seriesArr);
                } else {
                    tab2_ChartData[key] = [];
                    tab2_ChartData[key][0] = [];
                    tab2_ChartData[key][1] = [];
                    tab2_ChartData[key][0].push(xArr);
                    tab2_ChartData[key][1].push(seriesArr);
                }
            }
            var xData = tab2_ChartData[keyName][0];
            var seriesMap=[],legendArr=[];
            for (x in tab2_ChartData) {
                var obj = {
                        name:x,
                        type:'line',
                        stack: '总量',
                        data:[]
                    };
                obj.data = tab2_ChartData[x][1];
                seriesMap.push(obj);
                legendArr.push(x);
            }
            labelLineTwoChartFun("clmData1",xData,legendArr,seriesMap);
        },
        error: function(e) {}
    });

}
