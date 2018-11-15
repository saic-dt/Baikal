/**
 * Created by wangwenhu on 17/10/26.
 */
var labelTwoInit=false;
var dbName="";
var dbList=[];
$(function(){
    thisMenuFlag="dataAssets";/*设置菜单选中*/
    initMenu();
    dbName=$.decodeBase64($.QueryString("name"),3);
    /*事件加载*/
    clickFun();
    /*ajax加载*/
    initAjaxFun();
});
/*事件加载*/
function clickFun(){
    /*标签点击事件 */
    $("#dbLabel li").click(function(){
        var index=$("#dbLabel li").index($(this));
        $("#dbLabel li").removeClass("activeLi");
        $("#dbLabel li:eq("+index+")").addClass("activeLi");
        $(".dbLabelDiv").hide();
        $(".dbLabelDiv:eq("+index+")").show();
        if(!labelTwoInit && index==1){
            initDbTwoTitle();
            initDbTwoChart();
        }
    });
    /*时间空间初始化*/
    $.datetimepicker.setLocale('ch');
    /*标签1中 统计时间*/
    $('#dbOneBeginDate1').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            dbOneTableAjax();
        }
    });
    /*标签1中 数据统计 时间 开始*/
    $('#dbOneBeginDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initDbOneChart();
        }
    });
    $('#dbOneEndDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initDbOneChart();
        }
    });
    /*标签1中 数据统计 时间 结束*/

    /*标签2中  入库时间*/
    $('#dbDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date()
    });
    /*标签2中 数据统计 时间 开始*/
    $('#dbTwoBeginDate').val(GetDateStr(-7));
    $('#dbTwoBeginDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initDbTwoChart();
        }
    });
    $('#dbTwoEndDate').val(GetDateStr(0));
    $('#dbTwoEndDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            initDbTwoChart();
        }
    });
    /*标签2中 数据统计 时间 结束*/

    $("#dbTwoTitleDate").on("click",".glyphicon-edit",function(){
        $("#dbTwoTitleDate .dbDate").hide();
        $("#dbTwoTitleDate #dbDate").show();
        $("#dbTwoTitleDate .glyphicon").removeClass("glyphicon-edit");
        $("#dbTwoTitleDate .glyphicon").addClass("glyphicon-ok");
    });
    $("#dbTwoTitleDate").on("click",".glyphicon-ok",function(){
        dbTwoTitleDateUpd();
    });
    $("#dbTwoTitleOthers").on("click",".glyphicon-edit",function(){
        $("#dbTwoTitleOthers .dbOthers").hide();
        $("#dbTwoTitleOthers #dbOthers").show();
        $("#dbTwoTitleOthers .glyphicon").removeClass("glyphicon-edit");
        $("#dbTwoTitleOthers .glyphicon").addClass("glyphicon-ok");

    });
    $("#dbTwoTitleOthers").on("click",".glyphicon-ok",function(){
        dbTwoTitleOthersUpd();
    });
    /*数据库表检索*/
    $("#dbSearch").keyup(function(){
        var val=$(this).val();
        var html="";
        $.each(dbList,function(i){
            if(dbList[i].indexOf(val)>=0){
                html += '<li title="' + dbList[i] + '" style="cursor: pointer;">' + dbList[i] + '</li>';
            }
        });
        $('#dbNameList').html('');
        $('#dbNameList').append(html);
    });
    /*返回上一级*/
    $("#returnDB").click(function(){
        window.location.href = web_http+"/takeNoteIndex.html";
    });
    /**/
    $("#dbNameList").on("click","li",function(){
        var parentName=$("#dbNameSpan").text();
        var name=$(this).text();
        window.location.href = web_http+"/takeNoteTable.html?parentName="+$.encodeBase64(parentName,3)+"&tableName="+$.encodeBase64(name,3);
    });
}
/*ajax加载*/
function initAjaxFun(){
    $('#dbOneBeginDate1').val(GetDateStr(0));
    $('#dbOneBeginDate').val(GetDateStr(-7));
    $('#dbOneEndDate').val(GetDateStr(0));
    /*标签1列表数据*/
    dbOneTableAjax();
    /*标签1 图表*/
    initDbOneChart();
    /*数据库表信息*/
    initDbList();
}
/*标签1列表数据*/
function dbOneTableAjax(){
    var dbOneBeginDate1=$("#dbOneBeginDate1").val();
    var company="";
    $.ajax({
        url: http_service_bases + '/Tab2/tableCount',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: dbOneBeginDate1,
            endDate: dbOneBeginDate1,
            top: 5,
            dbName: dbName,
            company: company
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                var html = '';
                var list = data.data;
                for (var i = 0; i < list.length; i++) {
                    html += '<tr><td>' + list[i][0] + '</td><td>' + list[i][1] + '</td><td>' + list[i][2] + '</td><td>' + list[i][3] + '</td><td>' + list[i][4] + '</td><td>' + list[i][5] + '</td><td>' + list[i][6] + '</td><td>' + list[i][7] + '</td></tr>'
                }
                $('#dbOneTable tbody').html('');
                $('#dbOneTable tbody').append(html);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    });
}
/*标签1 图表*/
function initDbOneChart(){
    var dbOneBeginDate=$("#dbOneBeginDate").val();
    var dbOneEndDate=$("#dbOneEndDate").val();
    var company="";
    $.ajax({
        url: http_service_bases + '/Tab2/tableCount',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: dbOneBeginDate,
            endDate: dbOneEndDate,
            top: 5,
            dbName: dbName,
            company: company
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                var tab2_ChartData = {};
                var list = data.data;
                var keyName = list[0][4];
                //重构图表数据结构
                for (var i = 0; i < list.length; i++) {
                    var seriesArr = list[i][7];
                    var key = list[i][4];
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
                        name: x,
                        type: "line",
                        data: [],
                        itemStyle: {
                            normal: {
                                color: ""
                            }
                        },
                        smooth: true
                    };
                    obj.data = tab2_ChartData[x][1];
                    seriesMap.push(obj);
                    legendArr.push(x);
                }
                labelLineTwoChartFun("clmData1",xData,legendArr,seriesMap);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    });
}
/*数据库表信息*/
function initDbList(){
    $.ajax({
        url: http_service_bases + '/Tab2/tableName',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: dbName
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                $('#dbNameSpan').text(dbName);
                var domUl = $('#dbNameList');
                var list = data.tableName;
                dbList=list;
                var html = '';
                for (var i = 0; i < list.length; i++) {
                    html += '<li title="' + list[i] + '" style="cursor: pointer;">' + list[i] + '</li>';
                }
                domUl.html('');
                domUl.append(html);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    })
}
/*标签2 标题信息*/
function initDbTwoTitle(){
    $.ajax({
        url: http_service_bases + '/Tab2/databaseSum',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: dbName
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                $('#dbName').html(data.dbName);
                $('#dbCompany').html(data.company);
                $('#dbNote').html(data.dbComment);
                $('.dbDate').html(data.date);
                $('.dbOthers').html(data.others);
                $('#dbDate').val(data.date);
                $('#dbOthers').val(data.others);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    })
}
/*标签2 图表*/
function initDbTwoChart(){
    labelTwoInit=true;
    var dbTwoBeginDate=$("#dbTwoBeginDate").val();
    var dbTwoEndDate=$("#dbTwoEndDate").val();
    $.ajax({
        url: http_service_bases + '/Tab1/compData',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: dbTwoBeginDate,
            endDate: dbTwoEndDate,
            dbName: dbName
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
            labelLineTwoChartFun("clmData",xData,legendArr,seriesMap);
        },
        error: function(e) {}
    });
}
/*标签2 标题入库时间修改*/
function dbTwoTitleDateUpd(){
    var newDbInDate=$("#dbDate").val();
    $.ajax({
        url: http_service_bases + '/Tab2/update',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: dbName,
            newDbInDate: newDbInDate
        },
        success: function(data) {
            if(data.success || data.success == "true"){
                $("#dbTwoTitleDate .dbDate").show();
                $("#dbTwoTitleDate #dbDate").hide();
                $("#dbTwoTitleDate .glyphicon").removeClass("glyphicon-ok");
                $("#dbTwoTitleDate .glyphicon").addClass("glyphicon-edit");
                $("#dbTwoTitleDate .dbDate").html(newDbInDate);
            }else if (data.success == false || data.success == "false") {
                alert('更新失败');
            }
        },
        error: function(e) {}
    });
}
/*标签2 标题备注修改*/
function dbTwoTitleOthersUpd(){
    var newDbComment=$("#dbOthers").val();
    $.ajax({
        url: http_service_bases + '/Tab2/dbCommentUpdate',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: dbName,
            newDbComment: newDbComment
        },
        success: function(data) {
            if(data.success || data.success == "true"){
                $("#dbTwoTitleOthers .dbOthers").show();
                $("#dbTwoTitleOthers #dbOthers").hide();
                $("#dbTwoTitleOthers .glyphicon").removeClass("glyphicon-ok");
                $("#dbTwoTitleOthers .glyphicon").addClass("glyphicon-edit");
                $("#dbTwoTitleOthers .dbOthers").html(newDbComment);
            }else if (data.success == false || data.success == "false") {
                alert('更新失败');
            }
        },
        error: function(e) {}
    });
}

