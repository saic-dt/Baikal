$(function(){
    getSelect(); //取得节点列表
    loginCallback(); //判断登陆
    $(".logoInput").keydown(function (e) {
        if(e.keyCode==13) {
            keyLogin()
        }
    });
    $(".login-info input").focus(function(){
        $(".login-info img").hide();
        $(this).next("img").show();
    });
    $(".login-info img.delteIcon").click(function () {
        $(this).parent().find("input").val("")
    });
    $(".login_btn").on("click",function(){
        var name = $("#insureName").val();
        var password = $("#password").val();
        if (!checkName(name)) {
            return false;
        }
        if (!checkPassword(password)) {
            return false;
        }
        var login_host = $("#select1").val();
        if(login_host!=http_host){
            location.href="http://" +login_host+http_suffix+"/login.html?callback=true&loginParam="+$.encodeBase64(name+","+password);
            return;
        }
        $(this).text("正在登录...");
        $.ajax({
            type: "POST",
            url: http_service + "/loginByUser",
            dataType: "json",
            data: {"username":name,"password":$.md5(name+password)},
            success: function (msg) {
                if(msg.resultCode==201 || msg.resultCode=="201"){
                    $(".login_btn").text("登录");
                    valiDataCallBack("密码已过期，请重新设置！", "select1");
                }else if(msg.resultCode==200 || msg.resultCode==203 || msg.resultCode=="200" || msg.resultCode=="203") {
                    $.SetCookie('esurl', msg.data.esurl,'d1');
                    $.SetCookie('username', msg.data.username, 'd1');
                    $.SetCookie('tenantName', msg.data.tenantName, 'd1');
                    if(msg.resultCode==203 || msg.resultCode=="203") {
                        $.SetCookie('pwdMessage', msg.message, 'd1');
                    }
                    location.href=http_service+"/index.html";
                }else{
                    valiDataCallBack(msg.message, "insureName");
                    $(".login_btn").text("登录");
                }
            },
            error: function (ll) {
                valiDataCallBack("加载失败!", JSON.stringify(ll));
                $(".login_btn").text("登录");
            }
        });
    });
    $(".userCancel").click(function () {
        $(".addRoleDiv").hide();
        $(".maskPosition").hide();
    });
    $(".userInsure").click(function () {
        clickUpdatePwd();
    });
    $(".boxPwdInput").keydown(function (e) {
        if(e.keyCode==13) {
            clickUpdatePwd();
        }
    });
});
function  updatePwd() {
    $("#oldPassword").val("");
    $("#newPassword").val("");
    $("#confirmPassword").val("");
    $("#updatePwdError").html("");
    $(".addRoleDiv").show();
    $(".maskPosition").show();
}
function clickUpdatePwd() {
    var name = $("#userName").val();
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var confirmPassword = $("#confirmPassword").val();
    if(name=="" || name==null || name==undefined){
        alertWarn("错误提示","用户名不能为空!");
        return false;
    }
    if(oldPassword=="" || oldPassword==null || oldPassword==undefined){
        alertWarn("错误提示","原密码不能为空!");
        return false;
    }
    if(newPassword=="" || newPassword==null || newPassword==undefined){
        alertWarn("错误提示","新密码不能为空!");
        return false;
    }else if (newPassword.length < 10) {
        alertWarn("错误提示","新密码长度必须大于10!");
        return false;
    }
    if(confirmPassword=="" || confirmPassword==null || confirmPassword==undefined){
        alertWarn("错误提示","确认密码不能为空!");
        return false;
    }else if (confirmPassword.length < 10) {
        alertWarn("错误提示","确认密码长度必须大于10!");
        return false;
    }
    if(confirmPassword!=newPassword){
        alertWarn("错误提示","新密码与确认密码不一致!");
        return false;
    }
    $.ajax({
        type: "POST",
        url: http_service + "/updatePassword",
        dataType: "json",
        data: {"username":name,"oldPassword":$.encodeBase64(oldPassword),"newPassword":$.encodeBase64(newPassword)},
        success: function (data) {
            if(!returnDataCheck(data)){
                return false;
            }
            $(".addRoleDiv").hide();
            $(".maskPosition").hide();
            alertWarn("提示","密码修改成功，请使用新密码重新登录!");
        },
        error: function () {
            alertWarn("提示","密码修改失败!");
        }
    });
}
//回车登陆
function keyLogin(){
    $(".login_btn").click()  //调用登录按钮的登录事件
}
function valiDataCallBack(message, id){
    /*$(".form-error").remove();
    $("#"+id).after('<span class="form-error">'+message+'</span>');
    $(window).scrollTop(($("#"+id).offset().top - 20));*/
    $("#personError").html(message);
}
//姓名校验.
function checkName(name) {
    if (!name) {
        valiDataCallBack("用户名不能为空!", "insureName");
        return false;
    } else if (!(/^([a-zA-Z\u4e00-\u9fa5\s]){0,20}$/.test(name))) {

        valiDataCallBack("用户名错误!", "insureName");

        return false;
    }
    return true;
}
function checkPassword(password) {
    if (!password) {
        valiDataCallBack("密码不能为空!", "password");
        return false;
    } else if (password.length < 6) {
        valiDataCallBack("密码长度必须大于6!", "password");
        return false;
    }
    return true;
}
//跨域登陆
function loginCall(loginParam){
    var unicode = $.decodeBase64(loginParam);
    var str = '';
    for(var i = 0 , len =  unicode.length ; i < len ;++i){
        str += String.fromCharCode(unicode[i]);
    }
    var vlus = str.split(",");
    var name = vlus[0];
    var password = vlus[1];
    $(".login_btn").text("正在登录...");
    $.ajax({
        type: "POST",
        url: http_service + "/loginByUser",
        dataType: "json",
        data: {"username":name,"password":$.md5(name+password)},
        success: function (msg) {
            if(msg.resultCode==201 || msg.resultCode=="201"){
                $(".login_btn").text("登录");
                valiDataCallBack("密码已过期，请重新设置！", "select1");
            }else if(msg.resultCode==200 || msg.resultCode==203 || msg.resultCode=="200" || msg.resultCode=="203") {
                $.SetCookie('esurl', msg.data.esurl,'d1');
                $.SetCookie('username', msg.data.username, 'd1');
                $.SetCookie('tenantName', msg.data.tenantName, 'd1');
                if(msg.resultCode==203 || msg.resultCode=="203") {
                    $.SetCookie('pwdMessage', msg.message, 'd1');
                }
                location.href=http_service+"/index.html";
            }else{
                $(".login_btn").text("登录");
                valiDataCallBack(msg.message, "insureName");
            }
        }
    });
}
function loginCallback(){
    if("true"==$.QueryString("callback")){
        var loginParam = $.QueryString("loginParam");
        loginCall(loginParam);
    }
}
//10.10前台紧急添加
function getSelect(){
    $.ajax({
        type: "POST",
        url: http_service + "/hostList",
        dataType: "json",
        async:false,
        data:{},
        success: function (data) {
            var html = "<option value =\""+window.location.host +"\">当前节点</option>";
            $.each(data.hostList, function() {
                if (this.host != "" && this.host != null && this.host != undefined) {
                    html += "<option value =\""+this.host+"\">"+this.name+"</option>"
                }
            });
            $("#select1").html(html);
        }
    });
}