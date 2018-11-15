/**
 * Created by wangwenhu on 16/10/18.
 */
/*url定义*/
var http_suffix =  "/datalake-ui";
var http_host = window.location.host;
var http_service="http://" + http_host + http_suffix;
var http_service_bases="http://"+http_host+"/elseach";
var web_http="http://" + http_host+http_suffix;
/*当前菜单值*/
var thisMenuFlag="";
$(function(){
    //账户下拉框
    $("li.personName").click(function () {
        $(this).children(".accountReplace").show();
        $(this).addClass("personName1");
        setTimeout(function() {
            $(".accountReplace").hide();
            $("li.personName").removeClass("personName1")
        }, 3000);
    });
    /*菜单绑定事件*/
    $("#menu").on("click","ul li",function(){
        $(this).addClass("active").siblings("li").removeClass("active");
        $(this).children("dl").stop().fadeIn();
        $(this).siblings("li").find("dl").stop().hide()
    });
    /*菜单绑定事件*/
    $("#menu").on("click"," ul li.active dl dd",function(){
        $(this).addClass("currentDd").siblings("dd").removeClass("currentDd")
    });
});
/*菜单加载*/
function initMenu(){
    $.ajax({
        type: "POST",
        url: http_service + "/menu/get",
        data: {},
        dataType: "json",
        success: function (data) {
            if (data.success == 'true' || data.success == true) {
                if (returnDataCheck(data)) {
                    var sync = "";
                    var authority = "";
                    var monitor = "";
                    $.each(data.data.elements,function(i){
                        sync+="<dd><a href='"+web_http+"/dashboard.html?schema="+this.schema+"&instance="+this.instance+"&menuFlag=dataSync'>"+this.name+"</a></dd>";
                        authority+="<dd><a href='"+web_http+"/dashboard.html?schema="+this.schema+"&instance="+this.instance+"&menuFlag=dataAuthority'>"+this.name+"</a></dd>";
                        monitor+="<dd><a href='"+web_http+"/goldGate.html?schema="+this.schema+"&instance="+this.instance+"'>"+this.name+"</a></dd>";
                    });

                    var html = "<ul>";
                    html += "<li";
                    if(thisMenuFlag=="" || thisMenuFlag==null || thisMenuFlag==undefined){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"./index.html\">Dashboard</a>";
                    html += "</li>";
                    html += "<li";/*dataSync*/
                    if(thisMenuFlag=="dataSync"){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"#\">数据同步管理</a>";
                    html += "<dl class=\"list-dl\"";
                    if(thisMenuFlag=="dataSync"){
                        html += " style=\"display: block;\" ";
                    }
                    html += ">"+sync+"</dl>";
                    html += "</li>";
                    html += "<li";/*linkMonitoring*/
                    if(thisMenuFlag=="linkMonitoring"){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"#\">数据链接监控</a>";
                    html += "<dl class=\"list-dl\"";
                    if(thisMenuFlag=="linkMonitoring"){
                        html += " style=\"display: block;\" ";
                    }
                    html += ">"+monitor+"</dl>";
                    html += "</li>";
                    /*html +=  "<li";/!*userRoles*!/
                    if(thisMenuFlag=="userRoles"){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"./roleAdd.html\">用户角色管理</a>";
                    html += "</li>";*/
                    /*html += "<li";/!*dataAuthority*!/
                    if(thisMenuFlag=="dataAuthority"){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"#\">数据权限管理</a>";
                    html += "<dl class=\"list-dl\"";
                    if(thisMenuFlag=="dataAuthority"){
                        html += " style=\"display: block;\" ";
                    }
                    html += ">"+authority+"</dl>";
                    html += "</li>";*/
                    /*html += "<li";/!*dataAssets*!/
                    if(thisMenuFlag=="dataAssets"){
                        html += " class=\"active\" ";
                    }
                    html += ">";
                    html += "<a href=\"./takeNoteIndex.html\">数据资产管理</a></li>";*/

                    if(data.data.isAdmin== "true" ||data.data.isAdmin==true){
                        // if(1==1){
                        /*html += "<li";/!*dataManage*!/
                        if(thisMenuFlag=="dataManage"){
                            html += " class=\"active\" ";
                        }
                        html += ">";
                        html += "<a href=\"./dataManage.html\">数据源管理</a></li>";*/
                        /*html += "<li";/!*schemaManage*!/
                        if(thisMenuFlag=="schemaManage"){
                            html += " class=\"active\" ";
                        }
                        html += ">";
                        html += "<a href=\"./schemaManage.html\">SCHEMA管理</a></li>";

                        html += "</li>";*/
                        html += "<li";/*dataAssets*/
                        if(thisMenuFlag=="tenantManage"){
                            html += " class=\"active\" ";
                        }
                        html += ">";
                        html += "<a href=\"./tenant.html\">租户管理</a></li>";

                        html += "</li>";
                        html += "<li";/*operationLog*/
                        if(thisMenuFlag=="operationLog"){
                            html += " class=\"active\" ";
                        }
                        html += ">";
                        html += "<a href=\"./operationLog.html\">操作日志管理</a></li>";
                    }
                    html += "</ul>";
                    $("#menu").append(html);
                }
            }else {
                alertWarn('warnning', data.message);
            }
        },
        error: function (e) {
        }
    });
}
/*退出*/
function logout(){
    location.href=http_service + "/logout";
}
/*提示弹出框 警告框*/
function alertWarn(title,message){
    var AlertHtml='';
    AlertHtml+='<div class="alert_div warn-box">';
    AlertHtml+='<div class="show_8080">';
    AlertHtml+='<h4>'+title +'</h4>';
    AlertHtml+='<p>'+message+'</p>';
    AlertHtml+='<button class="insureBtn">确认</button>';
    AlertHtml+='</div>';
    AlertHtml+='</div>';
    $("body").append(AlertHtml);
    $(".warn-box").show();
    $(".insureBtn").click(function(){
        $("body .warn-box").remove();
    })
}
/*确认框*/
function alertFn(title,message,messageBr,brFlag,insureFun) {
    var relyHtml = '';
    relyHtml += '<h3 class="Title_info">title <img class="closeInsure" src="images/angin_close.png"/></h3>';
    relyHtml += '<div class="angin_info">';
    relyHtml += '<img class="img60" src="images/cancle_icon.png" alt=""/>';
    relyHtml += '<div class="tooltipInfo">';
    relyHtml += '<p class="insureAngin">' + message + '</p>';
    relyHtml += '<p '+(brFlag?"class=\"insureAngin\"":"")+' >' + messageBr + '</p>';
    relyHtml += '</div>';
    relyHtml += '</div>';
    relyHtml += '<div class="insure_cancel">';
    relyHtml += '<button class="insure backgroundT">确认</button>';
    relyHtml += '<button class="cancel">取消</button>';
    relyHtml += '</div>';
    var showRely = $(".angin_div.angin_rely").html(relyHtml);
    showRely.show();
    $(".maskPosition").show();
    //弹框关闭
    $(".closeInsure,.cancel").click(function() {
        $(".angin_div").hide();
        $(".maskPosition").hide();
    });
    $(".insure").click(function() {
        $(".angin_div").hide();
        $(".maskPosition").hide();
        insureFun();
    });
}
/*接口返回状态处理*/
function returnDataCheck(data){
    var retFlag=true;
    if(!data.success && data.resultCode=="904"){
        window.location.href=web_http+"/login.html";
    }
    if(!data.success && data.resultCode!="904" && data.resultCode!="200"){
        alertWarn("错误提示",data.message);
        retFlag=false;
    }
    return retFlag;
}