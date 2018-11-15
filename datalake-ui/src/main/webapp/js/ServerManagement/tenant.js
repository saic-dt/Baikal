/**
 * Created by wangwenhu on 17/8/19.
 */
var trendData = []; /*同步增量行数趋势 数据*/
var schema = "",
	instance = "", /*,syncCount="",count=""*/ /*url参数*/
	tableName = "";
var srcdata = {};
var refreshtime = 60000;
$(function() {
	//获取菜单实例
	thisMenuFlag = "tenantManage"; /*设置菜单选中*/
	initMenu();
	/*获得url参数*/
	schema = $.QueryString("schema");
	instance = $.QueryString("instance");
	tableName = $.QueryString("tableName");
	$("#username").html($.GetCookie('username'));
	/*同步增量行数趋势图宽度高设置*/
	$("#tendency").css({
		width : $("#tendency").width(),
		height : $("#tendency").width()
	});
	/*事件加载*/
	clickFun();
	/*ajax加载*/
	ajaxFun();
});
function clickFun() {
	/*点击列表出现对勾*/
	$(".border-Div").on("click", ".ul1 li span", function() {
		if ($(this).children("img").hasClass("checkedImg")) {
			$(this).children("img").removeClass("checkedImg");
		} else {
			$(this).children("img").addClass("checkedImg");
		}
	});
	//用户弹出框复选
	$("#addUserDivUl").on("click", "li", function() {
		if ($(this).hasClass("activeBg")) {
			$(this).removeClass("activeBg");
		} else {
			$(this).addClass("activeBg");
		}
	});
	//角色反选
	$(".fanxuan1").click(function() {
		$.each($(".ul3 li span").children("img"), function() {
			if ($(this).hasClass("checkedImg")) {
				$(this).removeClass("checkedImg");
			} else {
				$(this).addClass("checkedImg");
			}
		});
	});
	//用户反选
	$(".fanxuan2").click(function() {
		$.each($(".ul2 li span").children("img"), function() {
			if ($(this).hasClass("checkedImg")) {
				$(this).removeClass("checkedImg");
			} else {
				$(this).addClass("checkedImg");
			}
		});
	});
	//新增用户
	$("#addUser").click(function() {
		$.ajax({
			type : "POST",
			url : http_service + "/tenant/tenantList",
			data : {},
			dataType : "json",
			success : function(data) {
				if (returnDataCheck(data)) {
					var html = "";
					$.each(data.data, function() {
						html += "<option value =\"" + this.tenantName + "\">" + this.tenantName + "</option>";
					});
					$("#select1").html(html);
				}
			},
			error : function(e) {},
			complete : function(XHR, TS) {
				XHR = null
			}
		});
		$("#addUserType").val("add");
        $(".addUserDiv .title_role").html("新增用户");
        $(".addUserDiv .title_error").html("");
		$(".addUserDiv").show();
		$(".maskPosition").show();
	});
	//新增角色
	$("#addRole").click(function() {
        $(".addRoleDiv .title_error").html("");
		$(".addRoleDiv").show();
		$(".maskPosition").show();
	});
	//用户添加保存按钮	
	$("button.userInsure").click(function() {
		var userNameVal = $("#userAddName").val();
		var password = $("#userPassword").val();
		var addUserType = $("#addUserType").val();
		var tenant = $("#select1").val();
		//验证
		if (userNameVal == null || userNameVal == "") {
			$(".addUserDiv .title_error").html("用户名不能为空!");
			return false;
		}
		if (!(/^([a-zA-Z\u4e00-\u9fa5\s]){0,20}$/.test(userNameVal)) && addUserType == "add") {
			$(".addUserDiv .title_error").html("用户名错误!");
			return false;
		}
		$(".addUserDiv .title_error").html("");
		if (addUserType == "add") {
			if (password == "") {
				$(".addUserDiv .title_error").html("添加用户时密码不能为空");
				return false;
			}
            if (password != "" && password.length < 10) {
                $(".addUserDiv .title_error").html("密码长度必须大于10");
                return false;
            }
			var flg = false;
			$.ajax({
				type : "POST",
				url : http_service + "/user/get",
				data : {"rowkey":userNameVal},
				async : false,
				dataType : "json",
				success : function(data) {
					if (returnDataCheck(data)) {
						if(data.data.username != ""){
							flg = true;
						}
					}
				},
				error : function(e) {},
				complete : function(XHR, TS) {
					XHR = null
				}
			});
			if(flg){
				$(".addUserDiv .title_error").html("用户已存在");
				return false;
			}
			password = $.encodeBase64(password);
		}else if(password != "" && password != null){
            if (password.length < 10) {
                $(".addUserDiv .title_error").html("密码长度必须大于10");
                return false;
            }
			password = $.encodeBase64(password);
		}
		
		//验证结束
		var url = "/user/add";
		$.ajax({
			type : "POST",
			url : http_service + url,
			data : {
				"username" : userNameVal,
				 "password": password,
				 "tenantName" : tenant
			},
			dataType : "json",
			success : function(data) {
				if (returnDataCheck(data)) {
					$("#userAddName").removeAttr("disabled");
					$(".addUserDiv").hide();
					$(".maskPosition").hide();
					$("#userAddName").val("");
					$("#userPassword").val("");
					$("#select1").html("");
					/*用户列表*/
					ajaxUserList();
				}
			},
			error : function(e) {},
			complete : function(XHR, TS) {
				XHR = null
			}
		});

	});
	$("button.userCancel").click(function() {
		$("#userAddName").removeAttr("disabled");
		$(".addUserDiv").hide();
		$(".maskPosition").hide();
		$("#userAddName").val("");
		$("#userPassword").val("");
		$("#select1").html("");
	});
	//角色添加保存按钮
	$("button.roleInsure").click(function() {
		var roleName = $("#roleName").val();
		var description = $("#description").val();
		$("button.roleInsure").attr("disabled", true);
		var index = roleName.indexOf("_");
		if (index == 0 || index == (roleName.length - 1)) {
			$(".addRoleDiv .title_error").html("下划线不能为开头或结尾！");
			return false;
		}
		var regNumber = new RegExp(regExType.number);
		var indexBer = roleName.substring(0, 1);
		if (regNumber.test(indexBer)) {
			$(".addRoleDiv .title_error").html("数字不能为开头！");
			return false;
		}
		if (regNumber.test(roleName)) {
			$(".addRoleDiv .title_error").html("不能只包含数字！");
			return false;
		}
		if (roleName.length > 12 || roleName.length < 2) {
			$(".addRoleDiv .title_error").html("只能2～12个字！");
			return false;
		}
		var reg = new RegExp(regExType.userName);
		if (!reg.test(roleName)) {
			$(".addRoleDiv .title_error").html("只能为字母、数字和下划线！");
			return false;
		}
		if (description.length > 25) {
			$(".addRoleDiv .title_error").html("说明不能超过25个字！");
			return false;
		}
		$(".addRoleDiv .title_error").html("");
		$.ajax({
			type : "POST",
			url : http_service + "/tenant/tenantCreate",
			async : false,
			data : {
				"tenantName" : roleName,
				"desCription" : description
			},
			dataType : "json",
			success : function(data) {
				if (returnDataCheck(data)) {
					$(".addRoleDiv").hide();
					$(".maskPosition").hide();
					$("#roleName").val("");
					$("#description").val("");
					/*租户列表*/
					ajaxTenantList();
				}
			},
			error : function(e) {},
			complete : function(XHR, TS) {
				XHR = null
			}
		});
		$("button.roleInsure").attr("disabled", false);
	});
	$("button.roleCancel").click(function() {
		$(".addRoleDiv").hide();
		$(".maskPosition").hide();
		$("#roleName").val("");
		$("#description").val("");
	});



	/*用户删除*/
	$("#userDelAll").click(function() {
		var checkStr = "";
		$("#userList").find(".checkedImg").each(function(i) {
			if (i > 0) {
				checkStr += ",";
			}
			checkStr += $(this).attr("keyVal");
		});
		var urlData = $.split(checkStr, ",");
		var rows = {
			"rowKey" : urlData
		};
		$.ajax({
			type : "POST",
			url : http_service + "/user/delete",
			data : rows,
			dataType : "json",
			traditional : true,
			success : function(data) {
				if (returnDataCheck(data)) {
					/*用户列表*/
					ajaxUserList();
				}
			},
			error : function(e) {},
			complete : function(XHR, TS) {
				XHR = null
			}
		});
	});
	//弹框关闭
	$("#promptBox").on("click", ".closeInsure,.cancel", function() {
		$("#promptBox").hide();
		$(".maskPosition").hide();
	})
}
//多选
function checkArr(clsName) {
	var checkStr = "";
	$(clsName).children(".activeBg").each(function() {
		var t = $(this).text();
		checkStr += t;
		checkStr += ",";
	});
	var Str = checkStr.slice(0, (checkStr.length - 1));
	return Str;

}
function ajaxFun() {
	ajaxTenantList();
	ajaxUserList();
}
function ajaxTenantList() {
	$.ajax({
		type : "POST",
		url : http_service + "/tenant/tenantList",
		data : {},
		dataType : "json",
		success : function(data) {
			if (returnDataCheck(data)) {
				var html = "";
				$.each(data.data, function(i) {
					html += "<ul class=\"clearfix ul1 ul3\">";
					html += "<li>";
					html += "<span><img src=\"images/checked.png\" keyVal='" + this.tenantName + "'/></span>";
					html += "</li>";
					html += "<li>" + this.tenantName + "</li>";
					html += "<li>" + this.desCription + "</li>";
					html += "</ul>";
				});
				$("#roleList").html(html);
			}
		},
		error : function(e) {},
		complete : function(XHR, TS) {
			XHR = null
		}
	});
}
function ajaxUserList() {
	$.ajax({
		type : "POST",
		url : http_service + "/user/list",
		data : {},
		dataType : "json",
		success : function(data) {
			if (returnDataCheck(data)) {
				var html = "";
				$.each(data.data, function(i) {
					html += "<ul class=\"clearfix ul1 ul2\">";
					html += "<li>";
					html += "<span><img src=\"images/checked.png\" keyVal='" + this.rowKey.replace(/\n/g, ":") + "'/></span>";
					html += "</li>";
					html += "<li onclick=\"userUpd('" + this.rowKey.replace(/\n/g, ":") + "')\"  style=\"cursor: pointer;\">编辑</li>";
					html += "<li>" + this.username + "</li>";
					html += "<li>" + this.tenantName + "</li>";
					html += "</ul>";
				});
				$("#userList").html(html);
			}
		},
		error : function(e) {},
		complete : function(XHR, TS) {
			XHR = null
		}
	});
}
/*yh修改*/
function userUpd(user) {
	var userRole = "";
	$.ajax({
		type : "POST",
		url : http_service + "/user/get",
		data : {
			"rowkey" : user.replace(":", "\n")
		},
		async : false,
		dataType : "json",
		success : function(data) {
			if (returnDataCheck(data)) {
				userRole = data.data.tenantName;
			}
		},
		error : function(e) {},
		complete : function(XHR, TS) {
			XHR = null
		}
	});
	$.ajax({
		type : "POST",
		url : http_service + "/tenant/tenantList",
		data : {},
		dataType : "json",
		success : function(data) {
			if (returnDataCheck(data)) {
				var html = "";
				$.each(data.data, function() {
					html += "<option value =\"" + this.tenantName + "\" " + selects(this.tenantName, userRole) + ">" + this.tenantName + "</option>";
				});
				$("#select1").html(html);
			}
		},
		error : function(e) {},
		complete : function(XHR, TS) {
			XHR = null
		}
	});
	$("#addUserType").val("upd");
	$("#userAddName").val(user);
	$("#userAddName").attr("disabled", "disabled");
    $(".addUserDiv .title_role").html("修改用户");
    $(".addUserDiv .title_error").html("");
	$(".addUserDiv").show();
	$(".maskPosition").show();
}
function selects(vlu, vlu2) {
	if (vlu == vlu2) {
		return "selected";
	} else {
		return "";
	}
}