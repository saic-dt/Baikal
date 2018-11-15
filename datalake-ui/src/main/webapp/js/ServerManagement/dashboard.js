/**
 * Created by wangwenhu on 17/8/19.
 */
var trendData = []; /*同步增量行数趋势 数据*/
var schema = "",
    instance = ""; /*url参数*/
var  refreshtime  =  60000;
$(function() {
    //获取菜单实例
    thisMenuFlag=$.QueryString("menuFlag");
    initMenu();
    instClick();
    /*获得url参数*/
    schema = $.QueryString("schema");
    instance = $.QueryString("instance");
    $(".left_span").html(schema);
    $(".right_span").html(instance);
    $("#username").html($.GetCookie('username'));
    /*同步增量行数趋势图宽度高设置*/
    $("#tendency").css({
        width: $("#tendency").width(),
        height: $("#tendency").width()
    });
    /*查询按钮*/
    $("#searchBut").click(function() {
        instWSSyncDataFun();
    });
    /*输入框回车*/
    $("#search").keydown(function() { //给输入框绑定按键事件
        if (event.keyCode == "13") { //判断如果按下的是回车键则执行下面的代码
            instWSSyncDataFun();
        }
    });
    /*已同步数据 加载*/
    $("#searchSync").keyup(function(){
        instSyncDataFun();
    });
    /*未同步数据 加载*/
    $("#search").keyup(function(){
        instAjaxFun();
    });
    /*已同步数据*/
    instSyncDataFun();
    instAjaxFun();
    setInterval(instAjaxFun, 10000);
});
var syncScrollFlag=false,unsyncScrollFlag=false;
var syncScrollTopFlag=false,unsyncScrollTopFlag=false;
var syncStart=0,unsyncStart=0;
/**/
function instClick() {
    /*已同步 滚动条监听*/
    $("#syncScroll").scroll(function() {
        if(syncScrollTopFlag){
            return false;
        }
        var h=$(this).height();
        var scrollHeight=$(this)[0].scrollHeight;
        var scrollTop=$(this)[0].scrollTop;
        if(scrollTop+h>=scrollHeight-20){
            syncScrollTopFlag=true;
            syncStart++;
            syncScrollFun();
        }
    });
    /*未同步 滚动条监听*/
    $("#unsyncScroll").scroll(function() {
        if(unsyncScrollTopFlag){
            return false;
        }
        var h=$(this).height();
        var scrollHeight=$(this)[0].scrollHeight;
        var scrollTop=$(this)[0].scrollTop;
        if(scrollTop+h>=scrollHeight-20){
            unsyncStart++;
            unsyncScrollTopFlag=true;
            unsyncScrollFun();
        }
    });
    /*沙盒同步 取消按钮*/
    $("button.sandboxCancel").click(function(){
        $(".sandboxDiv").hide();
        $(".maskPosition").hide();
        $("#sandboxTable").val("");
    });
    /*沙盒同步 确认按钮*/
    $("button.sandboxInsure").click(function(){
        $("button.sandboxInsure").attr("disabled","disabled");
        $("button.sandboxCancel").attr("disabled","disabled");
        var table = $("#sandboxTable").val();
        var liArr = $("#sandboxDivUl .activeBg");
        var sandboxArr =[];
        for(var i = 0; i < liArr.length; i++){
            sandboxArr.push($("#sandboxDivUl .activeBg")[i].innerText);
        }
        $.ajax({
            type: "POST",
            url: http_service + "/sandbox/addAll",
            data: JSON.stringify({"instance":instance,"schema":schema,"table":table,"sandBox":sandboxArr}),
            contentType:"application/json",
            dataType: "json",
            success: function(data) {
                if(data.message == 'success'){
                    $("button.sandboxInsure").removeAttr("disabled");
                    $("button.sandboxCancel").removeAttr("disabled");
                    $(".sandboxDiv").hide();
                    $(".maskPosition").hide();
                    $("#sandboxTable").val("");
                }else{
                    $(".sandboxDiv .title_error").text(e.message);
                    $("button.sandboxInsure").removeAttr("disabled");
                    $("button.sandboxCancel").removeAttr("disabled");
                }
            },
            error: function(e) {
                $(".sandboxDiv .title_error").text(e.message);
                $("button.sandboxInsure").removeAttr("disabled");
                $("button.sandboxCancel").removeAttr("disabled");
            },
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    });
    /*沙盒列表*/
    $("#sandboxDivUl").on("click","li",function(){
        if($(this).hasClass("activeBg")){
            $(this).removeClass("activeBg");
        }else{
            $(this).addClass("activeBg");
        }
    });
}
/*Ajax 加载*/
function instAjaxFun() {
    unsyncScrollFlag=false;
    unsyncStart=0;
    /*已同步 未同步*/
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getSchemaTableList",
        data: {
            "source_schema": schema,
            "source_instance": instance,
            "keyword": $("#search").val(),
            start:unsyncStart
        },
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var valData = data.data;
                if(valData.length==0){
                    $("#text-warning-unsync").html("");
                    return false;
                }
                $.each(valData, function() {
                    if (this.flag == "unsync") { //未同步
                        var html = "";
                        $.each(this.elements, function() {
                            if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_unsync_tr'>";
                                html += "<td class=\"one-list\">" + this.source_table + "<input type='hidden' class='key' value='" + this.row_key + "'/></td>";
                                html += "<td class=\"list-progess one-list\">";
                                html += "<div class=\"pro_item\">";
                                html += "<em id=\"progress_main\" class=\"bar\" >";
                                html += "<i class='percentI' style=\"width:" + this.percent + "\">";
                                html += "<b class=\"borderSum\">";
                                html += "<span class='percent'>" + this.percent + "</span>  【剩余<span class='seconds'>" + this.residue_seconds + "</span>】";
                                html += "</b>";
                                html += "</i>";
                                html += "</em>";
                                html += "</div>";
                                html += "</td>";
                                html += "<td class=\"two-list td-but\"><span class='status'>";
                                html += this.status;
                                html += "</span></td>";
                                html += "<td class=\"two-list\"></td>";
                                html += "<td class=\"two-list\"></td>";
                                html += "<td class=\"four-list glyphicon_td td-but\">";
                                if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                    html += "<span class=\"glyphicon glyphicon-refresh\"></span>";
                                } else {
                                    html += "<span class=\"glyphicon glyphicon-arrow-up\" onclick=\"syncFun('" + this.source_table + "')\"></span>";
                                }
                                html += "</td>";
                                html += "</tr>";
                            } else {
                                html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_unsync_tr'>";
                                html += "<td class=\"one-list\">" + this.source_table + "<input type='hidden' class='key' value='" + this.row_key + "'/></td>";
                                html += "<td class=\"list-progess one-list\">";
                                html += "<div class=\"pro_item\" style='display: none;'>";
                                html += "<em id=\"progress_main\" class=\"bar\" >";
                                html += "<i class='percentI' style=\"width:0%\">";
                                html += "<b class=\"borderSum\">";
                                html += "<span class='percent'>0</span>  【剩余<span class='seconds'></span>】";
                                html += "</b>";
                                html += "</i>";
                                html += "</em>";
                                html += "</div>";
                                html += "</td>";
                                html += "<td class=\"two-list td-but\"><span class='status'>";
                                if (this.row_key == "" || this.row_key == null || this.row_key == undefined) {
                                    html += "未同步";
                                }
                                html += "</span></td>";
                                html += "<td class=\"two-list\" style='display: none;'><img src=\"images/bianji.png\" onclick=\"updateCell('" + this.source_table + "');\"/></td>";
                                html += "<td class=\"two-list glyphicon_td td-but\">";
                                if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                    html += "<span class=\"glyphicon glyphicon-refresh\"></span>";
                                } else {
                                    html += "<span class=\"glyphicon glyphicon-arrow-up\" onclick=\"syncFun('" + this.source_table + "')\"></span>";
                                }
                                html += "</td>";
                                html += "</tr>";
                            }
                        });
                        $("#unsyncScroll").scrollTop(0);
                        unsyncScrollTopFlag=false;
                        $("#text-warning-unsync").html(html);
                        html="";
                        //instSyncDataFun();
                        instWSSyncDataFun();

                        clearInterval(this);
                    }
                });
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*已同步 html加载*/
function syncHtmlFun(data, chartsFlag) {
    if(data.length==0){
        $("#text-warning-sync").html("");
        return false;
    }
    if (chartsFlag) {//是更新
        var trLength = $("#text-warning-sync tr").length;
        var flagIndex;
        var copyHtml;
        if(trLength<data.length){/*新增条数*/
            flagIndex=data.length - trLength;
            //copyHtml = $("#text-warning-sync tr").eq(0).prop("outerHTML");
            for (var i =0; i<flagIndex; i++) {
                var html="";
                html += "<tr id='" + i + "_sync_tr'>";
                html += "<td class=\"one-list\">" + i + "</td>";
                html += "<td class=\"one-list one-list-charts\" style='padding: 2px 6px; width: 50%;'><div id=\"" + i + "_sync_echart\" style='width:100%;height:25px;'></div></td>";
                html += "<td class=\"two-list\" style=\"position: relative;\"><input type='hidden' class='key' value='" + i + "'/><img src=\"images/time-out.png\" class='promptImg' style='position: absolute; top: 7px; width: 15px; display: none;'><img src=\"images/tongbu.png\" onclick=\"resynchronizationFun('" + i + "');\"/></td>";
                html += "<td class=\"two-list\" style=\"position: relative;display: none;\"><img src=\"images/tongbu.png\" onclick=\"synchSanbox('" + i + "');\" /></td>";
                html += "<td class=\"two-list\"  style=\"display: none;\"><img src=\"images/bianji.png\" onclick=\"updateCell('" + data[i].source_table + "');\"/></td>";
                html += "<td class=\"two-list\"><img src=\"images/delte.png\" onclick=\"delFun('" + i + "');\"/></td>";
                html += "</tr>";
                $("#text-warning-sync").append(html);
            }
        }else if(trLength>data.length){
            $("#text-warning-sync tr:gt("+(data.length-1)+")").remove();
        }
        $("#text-warning-sync tr").each(function (index) {
            if(index==data.length){
                return ;
            }
            $("#text-warning-sync tr").show();
            $(this).attr('id',data[index].source_table.replace(/\$/g, "") + '_sync_tr');
            $(this).find('td').eq(0).text(data[index].source_table);
            $(this).find('.key').val(data[index].row_key);
            //$(this).find('td').eq(1).html("<div id=\"" + data[index].source_table + "_sync_echart\" style='width:100%;height:25px;'></div>");
            $(this).find('td').eq(2).find('img').attr('onclick',"resynchronizationFun('" + data[index].source_table + "')");
            if(data[index].meta_flag == "5"){
                $(this).find('td').eq(2).find('img.promptImg').attr("src","images/time-out1.png");
                $(this).find('td').eq(2).find('img.promptImg').show();
            }else if(data[index].meta_flag == "6"){
                $(this).find('td').eq(2).find('img.promptImg').attr("src","images/time-out1.png");
                $(this).find('td').eq(2).find('img.promptImg').show();
            }else if(data[index].meta_flag == "7"){
                $(this).find('td').eq(2).find('img.promptImg').attr("src","images/time-out1.png");
                $(this).find('td').eq(2).find('img.promptImg').show();
            }else if(data[index].meta_flag == "8"){
                $(this).find('td').eq(2).find('img.promptImg').attr("src","images/time-out1.png");
                $(this).find('td').eq(2).find('img.promptImg').show();
            }else if(data[index].ddl_changed == "1"){
                $(this).find('td').eq(2).find('img.promptImg').attr("src","images/time-out.png");
                $(this).find('td').eq(2).find('img.promptImg').show();
            }else{
                $(this).find('td').eq(2).find('img.promptImg').hide();
            }
            $(this).find('td').eq(3).find('img').attr('onclick',"synchSanbox('" + data[index].source_table + "')");
            $(this).find('td').eq(4).find('img').attr('onclick',"updateCell('" + data[index].source_table + "')");
            $(this).find('td').eq(5).find('img').attr('onclick',"delFun('" + data[index].source_table + "')");
            var oldId = document.getElementById(data[index].source_table + '_sync_echart');
            if(oldId!=null&&oldId!=undefined&&echarts.getInstanceByDom(oldId)) {
                lineChartNoShaftFun(data[index].source_table + "_sync_echart", data[index].xArr, data[index].series,null,null,false);
            }else{
                $(this).find('td').eq(1).html("<div id=\"" + data[index].source_table + "_sync_echart\" style='width:100%;height:25px;'></div>");
                lineChartNoShaftFun(data[index].source_table + "_sync_echart", data[index].xArr, data[index].series,null,null,true);
            }
        });
    }else{
        var html = "";
        $.each(data, function() {
            html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_sync_tr'>";
            html += "<td class=\"one-list\">" + this.source_table + "</td>";
            html += "<td class=\"one-list one-list-charts\" style='padding: 2px 6px; width: 50%;'><div id=\"" + this.source_table + "_sync_echart\" style='width:100%;height:25px;'></div></td>";
            html += "<td class=\"two-list\" style='position: relative;'><input type='hidden' class='key' value='" + this.row_key + "'/><img src=\"images/time-out.png\" class='promptImg' style='position: absolute; top: 7px; width: 15px; display: none;'><img src=\"images/tongbu.png\" onclick=\"resynchronizationFun('" + this.source_table + "');\"/></td>";
            html += "<td class=\"two-list\" style='position: relative;display: none;'><img src=\"images/tongbu.png\" onclick=\"synchSanbox('" + this.source_table + "');\"/></td>";
            html += "<td class=\"two-list\"  style=\"display: none;\"><img src=\"images/bianji.png\" onclick=\"updateCell('" + this.source_table + "');\"/></td>";
            html += "<td class=\"two-list\"><img src=\"images/delte.png\" onclick=\"delFun('" + this.source_table + "');\"/></td>";
            html += "</tr>";
        });
        $("#text-warning-sync").html('');
        $("#text-warning-sync").html(html);
        html='';
    }
}
/*已同步 重新同步*/
function resynchronizationFun(id) {
    alertFn("同步确认","确定要重新同步信息?", "如果是请点确认，否则取消",false,insureClickFun);
    function insureClickFun(){
        $.ajax({
            type: "POST",
            url: http_service + "/dashboard/repStartSync",
            data: {
                "source_schema": schema,
                "source_instance": instance,
                source_table: id
            },
            dataType: "json",
            success: function(data) {
                if (data.success == 'true' || data.success == true) {
                    if (returnDataCheck(data)) {
                        alertWarn('success', data.message);
                    }
                } else {
                    alertWarn('warnning', data.message);
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    }
}
//通用编辑
function updateCell(id){
    location.href=http_service+"/authorization.html?schema="+schema+"&instance="+instance+"&tableName="+id+"&menuFlag="+thisMenuFlag;
}
/*已同步 删除*/
function delFun(id) {
    if ($("#" + id.replace(/\$/g, "") + "_unsync_tr").length > 0) {
        alertWarn('warnning', "未同步中已经存在该记录，不能点击删除");
        return false;
    }
    alertFn("删除确认","确定要删除信息?","如果是请点确认，否则取消",false,insureDelFun);
    function insureDelFun() {
        var rowkey = $("#" + id.replace(/\$/g, "") + "_sync_tr").find(".key").val();
        $.ajax({
            type: "POST",
            url: http_service + "/dashboard/delete",
            data: {
                rowkey: rowkey
            },
            dataType: "json",
            success: function(data) {
                if (data.success == 'true' || data.success == true) {
                    if (returnDataCheck(data)) {
                        $("#" + id.replace(/\$/g, "") + "_sync_tr").hide();
                        if ($("#" + id.replace(/\$/g, "") + "_unsync_tr").length > 0) {
                            return false;
                        }
                        var html = "";
                        html += "<tr id='" + id.replace(/\$/g, "") + "_unsync_tr'>";
                        html += "<td class=\"one-list\">" + id + "<input type='hidden' class='key' value=''/></td>";
                        html += "<td class=\"list-progess one-list\">";
                        html += "<div class=\"pro_item\" style='display: none;'>";
                        html += "<em id=\"progress_main\" class=\"bar\" >";
                        html += "<i class='percentI' style=\"width:0%\">";
                        html += "<b class=\"borderSum\">";
                        html += "<span class='percent'>0</span>  【剩余<span class='seconds'></span>】";
                        html += "</b>";
                        html += "</i>";
                        html += "</em>";
                        html += "</div>";
                        html += "</td>";
                        html += "<td class=\"two-list td-but\"><span class='status'>未同步</span></td>";
                        html += "<td class=\"two-list\"  style=\"display: none;\"><img src=\"images/bianji.png\" onclick=\"updateCell('" + this.source_table + "');\"/></td>";
                        html += "<td class=\"two-list glyphicon_td td-but\"><span class=\"glyphicon glyphicon-arrow-up\" onclick=\"syncFun('" + id + "')\"></span></td>";
                        html += "</tr>";
                        $("#text-warning-unsync").prepend(html);
                    }
                } else {
                    alertWarn('warnning', data.message);
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    }
}
/*未同步 同步*/
function syncFun(id) {
    alertFn("同步确认","确定要同步信息?","如果是请点确认，否则取消",false,insureSyncFun);
    function insureSyncFun() {
        $.ajax({
            type: "POST",
            url: http_service + "/dashboard/startSync",
            data: {
                "source_schema": schema,
                "source_instance": instance,
                "source_table": id
            },
            dataType: "json",
            success: function(data) {
                if (returnDataCheck(data)) {
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".glyphicon_td").html("<span class=\"glyphicon glyphicon-refresh\"></span>");
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".pro_item").show();
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".key").val(id);
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".status").html("正在构建表");
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".seconds").html("[--:--]");
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".percent").html("0.00%");
                    $("#" + id.replace(/\$/g, "") + "_unsync_tr").find(".percentI").css("0.00%");
                }
            },
            error: function(e) {},
            complete: function(XHR, TS) {
                XHR = null
            }
        });
    }
}
/*跳转下级页面*/
function skipSubordinate() {
    window.location.href = web_http + "/goldGate.html?schema=" + schema + "&instance=" + instance + "&timestamp=" + $.now();
}
/*已同步数据 建立ws*/
function instSyncDataFun() {
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getSchemaSyncTableList",
        dataType: "json",
        data: {
            "source_schema": schema,
            "source_instance": instance,
            "keyword": $("#searchSync").val(),
            start:0
        },
        success: function(data) {
            syncScrollFlag=false;
            if (returnDataCheck(data)) {
                syncHtmlFun(data.data, false);
            }
            clearInterval(this);
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*已同步数据 建立ws*/
function instWSSyncDataFun() {
    syncScrollFlag=false;
    syncStart=0;
    $.ajax({
        type: "POST",
        url: http_service + "/syncDataTransfer",
        dataType: "json",
        data: {
            "source_schema": schema,
            "source_instance": instance,
            "keyword": $("#searchSync").val(),
            start:syncStart
        },
        success: function(data) {
            if (returnDataCheck(data)) {
                $("#syncScroll").scrollTop(0);
                syncScrollTopFlag=false;
                syncHtmlFun(data.data, true);
            }
            clearInterval(this);
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*沙盒同步*/
function synchSanbox(table){
    $(".sandboxDiv .title_error").text("");
    $.ajax({
        type: "POST",
        url: http_service + "/sandbox/selectList",
        data: {"instance":instance,"schema":schema,"table":table},
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var html="";
                var dataObj = data.data;
                for(var key in dataObj){
                    html+="<li title='"+key+"'" + (dataObj[key]? "class='activeBg'" : "") + ">"+key+"<span></span></li>";
                }
                $("#syncScroll").scrollTop(0);
                syncScrollTopFlag=false;
                $("#sandboxDivUl").html(html);
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
    $("#sandboxTable").val(table);
    $(".sandboxDiv").show();
    $(".maskPosition").show();
}
/*已同步滚动条更新*/
function syncScrollFun() {
    $.ajax({
        type: "POST",
        url: http_service + "/syncDataTransfer",
        dataType: "json",
        data: {
            "source_schema": schema,
            "source_instance": instance,
            "keyword": $("#searchSync").val(),
            start:syncStart
        },
        success: function(data) {
            // syncScrollFlag=false;
            if (returnDataCheck(data)) {
                var html = "";
                $.each(data.data, function() {
                    html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_sync_tr'>";
                    html += "<td class=\"one-list\">" + this.source_table + "</td>";
                    html += "<td class=\"one-list one-list-charts\" style='padding: 2px 6px; width: 50%;'><div id=\"" + this.source_table + "_sync_echart\" style='width:100%;height:25px;'></div></td>";
                    html += "<td class=\"two-list\" style='position: relative;'><input type='hidden' class='key' value='" + this.row_key + "'/><img src=\"images/time-out.png\" class='promptImg' style='position: absolute; top: 7px; width: 15px; display: none;'><img src=\"images/tongbu.png\" onclick=\"resynchronizationFun('" + this.source_table + "');\"/></td>";
                    html += "<td class=\"two-list\" style='position: relative;display: none;'><img src=\"images/tongbu.png\" onclick=\"synchSanbox('" + this.source_table + "');\" /></td>";
                    html += "<td class=\"two-list\" style=\"display: none;\"><img src=\"images/bianji.png\" onclick=\"updateCell('" + this.source_table + "');\"/></td>";
                    html += "<td class=\"two-list\"><img src=\"images/delte.png\" onclick=\"delFun('" + this.source_table + "');\"/></td>";
                    html += "</tr>";
                });
                $("#text-warning-sync").append(html);
                syncScrollTopFlag=false;
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}
/*未同步滚动条更新*/
function unsyncScrollFun() {
    /* 未同步*/
    $.ajax({
        type: "POST",
        url: http_service + "/dashboard/getSchemaTableList",
        data: {
            "source_schema": schema,
            "source_instance": instance,
            "keyword": $("#search").val(),
            start:unsyncStart
        },
        dataType: "json",
        success: function(data) {
            if (returnDataCheck(data)) {
                var valData = data.data;
                if(valData.length==0){
                    return false;
                }
                $.each(valData, function() {
                    if (this.flag == "unsync") { //未同步
                        var html = "";
                        $.each(this.elements, function() {
                            if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_unsync_tr'>";
                                html += "<td class=\"one-list\">" + this.source_table + "<input type='hidden' class='key' value='" + this.row_key + "'/></td>";
                                html += "<td class=\"list-progess one-list\">";
                                html += "<div class=\"pro_item\">";
                                html += "<em id=\"progress_main\" class=\"bar\" >";
                                html += "<i class='percentI' style=\"width:" + this.percent + "\">";
                                html += "<b class=\"borderSum\">";
                                html += "<span class='percent'>" + this.percent + "</span>  【剩余<span class='seconds'>" + this.residue_seconds + "</span>】";
                                html += "</b>";
                                html += "</i>";
                                html += "</em>";
                                html += "</div>";
                                html += "</td>";
                                html += "<td class=\"two-list td-but\"><span class='status'>";
                                html += this.status;
                                html += "</span></td>";
                                html += "<td class=\"two-list\"></td>";
                                html += "<td class=\"two-list\"></td>";
                                html += "<td class=\"four-list glyphicon_td td-but\">";
                                if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                    html += "<span class=\"glyphicon glyphicon-refresh\"></span>";
                                } else {
                                    html += "<span class=\"glyphicon glyphicon-arrow-up\" onclick=\"syncFun('" + this.source_table + "')\"></span>";
                                }
                                html += "</td>";
                                html += "</tr>";
                            } else {
                                html += "<tr id='" + this.source_table.replace(/\$/g, "") + "_unsync_tr'>";
                                html += "<td class=\"one-list\">" + this.source_table + "<input type='hidden' class='key' value='" + this.row_key + "'/></td>";
                                html += "<td class=\"list-progess one-list\">";
                                html += "<div class=\"pro_item\" style='display: none;'>";
                                html += "<em id=\"progress_main\" class=\"bar\" >";
                                html += "<i class='percentI' style=\"width:0%\">";
                                html += "<b class=\"borderSum\">";
                                html += "<span class='percent'>0</span>  【剩余<span class='seconds'></span>】";
                                html += "</b>";
                                html += "</i>";
                                html += "</em>";
                                html += "</div>";
                                html += "</td>";
                                html += "<td class=\"two-list td-but\"><span class='status'>";
                                if (this.row_key == "" || this.row_key == null || this.row_key == undefined) {
                                    html += "未同步";
                                }
                                html += "</span></td>";
                                html += "<td class=\"two-list\"  style=\"display: none;\"><img src=\"images/bianji.png\" onclick=\"updateCell('" + this.source_table + "');\"/></td>";
                                html += "<td class=\"two-list glyphicon_td td-but\">";
                                if (this.row_key != "" && this.row_key != null && this.row_key != undefined) {
                                    html += "<span class=\"glyphicon glyphicon-refresh\"></span>";
                                } else {
                                    html += "<span class=\"glyphicon glyphicon-arrow-up\" onclick=\"syncFun('" + this.source_table + "')\"></span>";
                                }
                                html += "</td>";
                                html += "</tr>";
                            }
                        });
                        $("#text-warning-unsync").append(html);
                        unsyncScrollTopFlag=false;
                    }
                });
            }
        },
        error: function(e) {},
        complete: function(XHR, TS) {
            XHR = null
        }
    });
}