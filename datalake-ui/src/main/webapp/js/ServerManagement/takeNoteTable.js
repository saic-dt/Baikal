/**
 * Created by wangwenhu on 17/10/30.
 */
var parentName="",tableName="";
var tableList=[];
$(function(){
    thisMenuFlag="dataAssets";/*设置菜单选中*/
    initMenu();
    parentName=$.decodeBase64($.QueryString("parentName"),3);
    tableName=$.decodeBase64($.QueryString("tableName"),3);
    /*事件加载*/
    clickFun();
    /*ajax加载*/
    initAjaxFun();
});
/*事件加载*/
function clickFun(){
    /*时间空间初始化*/
    $.datetimepicker.setLocale('ch');
    /*标签1中 数据统计 开始时间*/
    $('#tableOneBeginDate').val(GetDateStr(-7));
    $('#tableOneBeginDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            oneEchartTable();
        }
    });
    /*标签1中 数据统计 结束时间*/
    $('#tableOneEndDate').val(GetDateStr(0));
    $('#tableOneEndDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date(),
        onSelectDate: function() {
            oneEchartTable();
        }
    });

    $('#tableInDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date()
    });
    $('#tableCountDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false,
        maxDate: new Date()
    });

    /*标签1中 编辑*/
    $("#tableLabelTitle").on("click",".glyphicon-edit",function(){
        $(this).parent().find("em.tableEm").hide();
        $(this).parent().find("input.tableInput").show();
        $(this).removeClass("glyphicon-edit");
        $(this).addClass("glyphicon-ok");
    });
    /*标签1中 入库时间保存*/
    $("#tableInDateLi").on("click",".glyphicon-ok",function(){
        var tableInDate=$("#tableInDate").val();
        $.ajax({
            url: http_service_bases + '/Tab3/inDateUpdate',
            type: 'GET',
            dataType: 'json',
            data: {
                dbName: parentName,
                tableName: tableName,
                newInDate: tableInDate
            },
            success: function(data) {
                if(data.success || data.success == "true"){
                    $("#tableInDateLi em.tableEm").show();
                    $("#tableInDateLi input.tableInput").hide();
                    $("#tableInDateLi .glyphicon").removeClass("glyphicon-ok");
                    $("#tableInDateLi .glyphicon").addClass("glyphicon-edit");
                    $("#tableInDateLi em.tableEm").html(tableInDate);
                }else if (data.success == false || data.success == "false") {
                    alert('更新失败');
                }
            },
            error: function(e) {}
        });
    });
    /*标签1中 开始审计时间保存*/
    $("#tableCountDateLi").on("click",".glyphicon-ok",function(){
        var tableCountDate=$("#tableCountDate").val();
        $.ajax({
            url: http_service_bases + '/Tab3/inDateUpdate',
            type: 'GET',
            dataType: 'json',
            data: {
                dbName: parentName,
                tableName: tableName,
                newInDate: tableCountDate
            },
            success: function(data) {
                if(data.success || data.success == "true"){
                    $("#tableCountDateLi em.tableEm").show();
                    $("#tableCountDateLi input.tableInput").hide();
                    $("#tableCountDateLi .glyphicon").removeClass("glyphicon-ok");
                    $("#tableCountDateLi .glyphicon").addClass("glyphicon-edit");
                    $("#tableCountDateLi em.tableEm").html(tableCountDate);
                }else if (data.success == false || data.success == "false") {
                    alert('更新失败');
                }
            },
            error: function(e) {}
        });
    });
    /*标签1中 备注保存*/
    $("#tableOthersLi").on("click",".glyphicon-ok",function(){
        var tableOthers=$("#tableOthers").val();
        $.ajax({
            url: http_service_bases + '/Tab3/inDateUpdate',
            type: 'GET',
            dataType: 'json',
            data: {
                dbName: parentName,
                tableName: tableName,
                newInDate: tableOthers
            },
            success: function(data) {
                if(data.success || data.success == "true"){
                    $("#tableOthersLi em.tableEm").show();
                    $("#tableOthersLi input.tableInput").hide();
                    $("#tableOthersLi .glyphicon").removeClass("glyphicon-ok");
                    $("#tableOthersLi .glyphicon").addClass("glyphicon-edit");
                    $("#tableOthersLi em.tableEm").html(tableOthers);
                }else if (data.success == false || data.success == "false") {
                    alert('更新失败');
                }
            },
            error: function(e) {}
        });
    });
    /*数据库表检索*/
    $("#tableSearch").keyup(function(){
        var val=$(this).val();
        var html="";
        $.each(tableList,function(i){
            if(tableList[i].indexOf(val)>=0){
                html += '<li title="' + tableList[i] + '" ';
                if(tableName==tableList[i]){
                    html += ' class="total_result_opn" '
                }
                html+=' style="cursor: pointer;">' + tableList[i] + '</li>';
            }
        });
        $('#tableNameList').html('');
        $('#tableNameList').append(html);
    });
    /**/
    $("#parentNameP").click(function(){
        window.location.href = web_http+"/takeNoteDB.html?parentName="+$.encodeBase64(parentName,3);
    });

    /**/
    $("#tableNameList").on("click","li",function(){
        var parentName=$("#parentNameSpan").text();
        var name=$(this).text();
        window.location.href = web_http+"/takeNoteTable.html?parentName="+$.encodeBase64(parentName,3)+"&tableName="+$.encodeBase64(name,3);
    });
}
/*ajax加载*/
function initAjaxFun(){
    /*数据库表信息*/
    initTableList();
    /* 数据库摘要 */
    initDatabaseSum();
    /*标签1中 数据统计图表*/
    oneEchartTable();
}
/*数据库表信息*/
function initTableList(){
    $.ajax({
        url: http_service_bases + '/Tab2/tableName',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: parentName
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                $('#parentNameSpan').text(parentName);
                var domUl = $('#tableNameList');
                var list = data.tableName;
                tableList=list;
                var html = '';
                for (var i = 0; i < list.length; i++) {
                    html += '<li title="' + list[i] + '" ';
                    if(tableName==list[i]){
                        html += ' class="total_result_opn" '
                    }
                    html+=' style="cursor: pointer;">' + list[i] + '</li>';
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
/* 数据库摘要 */
function initDatabaseSum() {
    $.ajax({
        url: http_service_bases + '/Tab3/tableSum',
        type: 'GET',
        dataType: 'json',
        data: {
            dbName: parentName,
            tableName: tableName
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                $('#tableName').html(data.tableName);
                $('#tableSection').html(data.section);
                $('#tableComment').html(data.tableComment);
                $('.tableInDate').html(data.inDate);
                $('.tableCountDate').html(data.countDate);
                $('.tableOthers').html(data.others);
                $('#tableInDate').val(data.inDate);
                $('#tableCountDate').val(data.countDate);
                $('#tableOthers').val(data.others);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    });

}
/*标签1中 数据统计图表*/
function oneEchartTable(){
    var startDate=$("#tableOneBeginDate").val();
    var endDate=$("#tableOneEndDate").val();
    $.ajax({
        url: http_service_bases + '/Tab3/tableRowCount',
        type: 'GET',
        dataType: 'json',
        data: {
            startDate: startDate,
            endDate: endDate,
            dbName: parentName,
            tableName: tableName
        },
        success: function(data) {
            if (data.success == 'true' || data.success == true) {
                var tab2_data = {};
                var list = data.data;
                var keyName = list[0][1];
                //重构图表数据结构
                for (var i = 0; i < list.length; i++) {
                    var seriesArr = list[i][2];
                    var key = list[i][1];
                    var xArr = list[i][0];
                    if (tab2_data[key]) {
                        tab2_data[key][0].push(xArr);
                        tab2_data[key][1].push(seriesArr);
                    } else {
                        tab2_data[key] = [];
                        tab2_data[key][0] = [];
                        tab2_data[key][1] = [];
                        tab2_data[key][0].push(xArr);
                        tab2_data[key][1].push(seriesArr);
                    }
                }
                var xArrMap = tab2_data[keyName][0];
                var seriesMap=[],legendArr=[];
                for (x in tab2_data) {
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
                    obj.data = tab2_data[x][1];
                    seriesMap.push(obj);
                    legendArr.push(x);
                }
                labelLineTwoChartFun("clmData1",xArrMap,legendArr,seriesMap);
            } else if (data.success == 'false' || data.success == false) {
                alert(data.msg);
            }
        },
        error: function(e) {}
    });
}