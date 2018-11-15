/**
 *  wwh 表单校验
 * Created by Administrator on 2015/8/24.
 */

(function ($) {
    $.checkExpression ={
        errorInfo : function(map){/*初始提示html*/
            var info="";
            if(map.checkType==4){
                info=$.checkExpression.ajaxErrorInfo(map);
            }else if(map.checkType==5){
                info=$.checkExpression.ajaxSubmitInfo(map);
            }else{
                info=$.checkExpression.formErrorInfo(map);
            }
            return info;
        },
        formErrorInfo : function(map){/*form初始提示html*/
            var info="";
            if(map.classType){
                if(map.fromType=="app"){
                    info="<div class=\""+map.errorTrueClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\"><span class=\"ico\"></span><span class=\"errorSpan\">"+ map.info+"</span></div>";
                }else{
                    info="<span class=\""+map.errorTrueClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\">"+ map.info+"</span>";
                }
            }else{
                if(map.fromType=="app"){
                    info="<div class=\""+map.errorFalseClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\"><span class=\"ico\"></span><span class=\"errorSpan\">"+ map.info+"</span></div>";
                }else{
                    info="<span class=\""+map.errorFalseClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\">"+ map.info+"</span>";
                }
            }
            return info;
        },
        ajaxErrorInfo : function(map){/*ajax初始提示html*/
            var info="";
            if(map.ajaxClassFlag){
                if(map.fromType=="app"){

                }else{
                    info="<span class=\""+map.ajaxEorTrueClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\">"+ map.info+"</span>";
                }
            }else{
                if(map.fromType=="app"){

                }else{
                    info="<span class=\""+map.ajaxEorFalseClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\">"+ map.info+"</span>";
                }
            }
            return info;
        },
        ajaxSubmitInfo : function(map){/*ajax数据提示时*/
            var info="";
            if(map.fromType=="app"){

            }else{
                info="<span class=\""+map.ajaxSubmitClass+"_"+map.fromType+"\" id=\""+map.id+"Tip\">"+ map.info+"</span>";
            }
            return info;
        },
        checkValueContrast : function(checkMap){/*获取输入框校验组信息*/
            checkMap.value=  $.goBlank($("#"+checkMap.id).val());
            $.each(checkSetting,function(setKey,setValue){/*将传入信息与默认信息进行组合*/
                var flag=true;
                $.each(checkMap,function(boxKey,boxValue){
                    if(setKey==boxKey){
                        flag=false;
                    }
                });
                if(flag){
                    checkMap[setKey]=setValue;
                }
            });
        },
        mapExtend : function(rawMap,mergerMap){/*合并map*/
            rawMap.value=  $.goBlank($("#"+rawMap.id).val());
            $.each(mergerMap,function(setKey,setValue){/*将传入信息与默认信息进行组合*/
                var flag=true;
                $.each(rawMap,function(boxKey,boxValue){
                    if(setKey==boxKey){
                        flag=false;
                    }
                });
                if(flag){
                    rawMap[setKey]=setValue;
                }
            });
        },
        insetPageIsMap : function(map){/*保存校验数据*/
            this.mapExtend(map,checkSetting);
            var flag=false;
            var forPro={};
            var inputMap={};
            var valueMap={};
            valueMap=mapArray[map.fromType];
            if(valueMap!=null &&valueMap!='' && valueMap!=undefined){
                inputMap=valueMap;
                $.each(valueMap,function(key,values){
                    if(key==map.pageIsId){
                        flag=true;
                    }
                });
                if(flag==true){/*存在*/
                    forPro=valueMap[map.pageIsId];
                }
                forPro[map.id+"Tip"]=map;
                inputMap[map.pageIsId]=forPro;
            }else{
                forPro[map.id+"Tip"]=map;
                inputMap[map.pageIsId]=forPro;
            }
            mapArray[map.fromType]=inputMap;
        },
        updatePageIsMap : function(map){/*修改校验数据*/
            if(map.pageIsId=='' || map.pageIsId==undefined || map.pageIsId==null){
                map.pageIsId='form';
            }
            map.value=  $.goBlank($("#"+map.id).val());
            var val={};
            var forPro={};
            var inputMap={};
            var valueMap={};
            valueMap=mapArray[map.fromType];
            if(valueMap!=null &&valueMap!='' && valueMap!=undefined) {
                inputMap=valueMap;
                $.each(valueMap, function (key, values) {
                    if (key == map.pageIsId) {
                        forPro = valueMap[map.pageIsId];
                    }
                });
            }else{
                inputMap[map.pageIsId]="";
            }
            if(forPro!=null && forPro!=undefined && forPro!=''){
                $.each(forPro,function(key,value){
                    if(key==map.id+"Tip"){
                        val=value;
                    }
                });
                $.each(map,function(key,value){
                    val[key]=value;
                });
                forPro[map.id+"Tip"]=val;
                inputMap[map.pageIsId]=forPro;
                mapArray[map.fromType]=inputMap;
            }
        },
        selectPageIsMap : function(map){/*查询校验数据*/
            if(map.pageIsId=='' || map.pageIsId==undefined || map.pageIsId==null){
                map.pageIsId='form';
            }var valueMap={};
            valueMap=mapArray[map.fromType];
            if(valueMap!=null &&valueMap!='' && valueMap!=undefined) {
                var forPro=valueMap[map.pageIsId];
                if(forPro!=null && forPro!=undefined && forPro!=''){
                    $.each(forPro,function(key,values){
                        if(key==map.id+"Tip"){
                            map=values;
                        }
                    });
                }
            }
        },
        deletePageIsMap : function(map){/*向校验map中数组删除数据*/
            var forPros={};
            var forPro={};
            newMapArray={};
            var valueMap={};
            valueMap=mapArray[map.fromType];
            if(valueMap==null || valueMap=='' || valueMap==undefined) {
                return false;
            }
            var forPro=valueMap[map.pageIsId];
            if(forPro!=null && forPro!=undefined && forPro!=''){
                $.each(forPro,function(key,values){
                    if(key!=map.id+"Tip"){
                        forPros[key]=values;
                    }
                });
                if(forPros!=null && forPros!=undefined && forPros!=''){/*没有错误信息*/
                    $.each(valueMap,function(key,values){
                        if(key!=map.pageIsId){
                            newMapArray[key]=values;
                        }
                    });
                    valueMap=newMapArray;
                }else valueMap[map.pageIsId]=forPros;
            }
            mapArray[map.fromType]=valueMap;
        },
        insetFormMap : function(map){/*保存错误数据*/
            var flag=false;
            var forPro=[];
            var inputMap={};
            var valueMap=formArray[map.fromType];/*app*/
            if(valueMap==null || valueMap=='' || valueMap==undefined) {
                formArray[map.fromType]="";
            }else{
                inputMap=valueMap;
                $.each(valueMap,function(key,values){
                    if(key==map.pageIsId){
                        flag=true;
                    }
                });
            }
            if(flag==true){/*存在*/
                forPro=valueMap[map.pageIsId];
                if(jQuery.inArray(map.id+"Tip", forPro)<0){/*不存在*/
                    forPro.push(map.id+"Tip");
                }
            }else{/*不存在*/
                inputMap[map.pageIsId]="";
                forPro.push(map.id+"Tip");
            }
            inputMap[map.pageIsId]=forPro;
            formArray[map.fromType]=inputMap;
        },
        deleteFormMap : function(map){/*删除错误数据*/
            var flag=false;
            var forPro=[];
            var newformArray={};
            var valueMap=formArray[map.fromType];
            if(valueMap==null || valueMap=='' || valueMap==undefined) {
                return false;
            }
            forPro=valueMap[map.pageIsId];
            if(forPro!='' && forPro!=undefined && forPro!=null){
                if(jQuery.inArray(map.id+"Tip", forPro)>=0){/*存在*/
                    forPro.splice(jQuery.inArray(map.id+"Tip",forPro),1);
                }
            }
            if(forPro=='' || forPro==undefined || forPro==null){/*清除map中相应的表单key*/
                $.each(valueMap,function(key,values){
                    if(key!=map.pageIsId){
                        newformArray[key]=values
                    }
                });
                valueMap=newformArray;
            }else valueMap[map.pageIsId]=forPro;
            formArray[map.fromType]=valueMap;
        },
        selectFormMapInfo : function(errorMap,id,fromFlag){/*表单提交时查询错误信息*/
            var map={};
            var fromMap=mapArray[fromFlag];
            var forPro={};
            if(fromMap!=null && fromMap!=undefined && fromMap!=''){
                forPro=fromMap[id];
                if(forPro!=null && forPro!=undefined && forPro!=''){
                    $.each(forPro,function(key,values){
                        $.each(errorMap,function(errorKey,errorValue){
                            if(key==errorValue){
                                map[key]=values;
                            }
                        });
                    });
                }
            }
            return map;
        },
        checkFormIsMap :function(pageIsId,fromFlag){/*判断是否有错误信息*/
            var retFlag=false;
            if(pageIsId=='' || pageIsId==undefined){
                pageIsId="form";
            }
            if(fromFlag=='' || fromFlag==undefined || fromFlag==null){
                fromFlag="pc";
            }
            var valueMap=formArray[fromFlag];
            if(valueMap=='' || valueMap==null || valueMap==undefined){
                return retFlag;
            }
            $.each(valueMap,function(key,values){
                if(key==pageIsId){
                    retFlag=true;
                }
            });
            return retFlag;
        },
        checkPageIsShow :function(pageIsId,fromFlag){/*显示所有错误信息*/
            if(pageIsId=='' || pageIsId==undefined){
                pageIsId="form";
            }
            if(fromFlag=='' || fromFlag==undefined || fromFlag==null){
                fromFlag="pc";
            }
            var valueMap=formArray[fromFlag];
            if(valueMap=='' || valueMap==null || valueMap==undefined){
                return false;
            }
            $.each(valueMap,function(key,values){
                if(key==pageIsId){
                    $.checkExpression.checkFormErrorShow(values,key,fromFlag);
                }
            });
        },
        checkFormErrorShow :function(errorMap,pageIsId,fromFlag){/*查询所有错误信息*/
            if(errorMap!='' && errorMap!=undefined && errorMap!=null){
                var map=$.checkExpression.selectFormMapInfo(errorMap,pageIsId,fromFlag);
                if(errorMap!='' && errorMap!=undefined && errorMap!=null){
                    var flag=false;
                    $.each(map,function(key,value){/*检查所有校验数据中有没有设置 提示是否连续提示*/
                        if(value.continuousFlag==false){
                            flag= true;
                        }
                    });
                    $.each(map,function(key,value){/*显示所有错误信息*/
                        $.checkExpression.infoCueType(value);
                        if(flag==true && value.flag==false){
                            return false;
                        }
                    });
                }
            }
        },
        infoCueShow : function(map){/*是否显示*/
            map.flag=false;
            if(map.onCueShow) this.infoCueType(map);
            this.insetPageIsMap(map);
        },
        infoCueType : function(map){/*提示类型*/
            if(map.onCueType==false){
                $.browserBox({boxId:'checkId',contInfo:map.info});
                $.stop();
            }else{
                this.infoAutomatic(map);
            }
        },
        infoAutomatic : function(map){/*提示方法*/
            var html=this.errorInfo(map);
            if(map.errorDivId!='' && map.errorDivId!=null && map.errorDivId!=undefined){
                this.errorDivIdError(html,map);
            }else if(map.onAutomatic==true){/*自动追加*/
                this.cueTypeTrueError(html,map);
            }else{
                this.cueTypeFalseError(html,map);
            }
        },
        cueTypeTrueError : function(html,map){/*自动追加*/
            $("#"+map.id+"Tip").remove();
            $("#"+map.id).parent().append(html);
        },
        cueTypeFalseError : function(html,map){/*不自动追加*/
            $("#"+map.id+"Div").html("");
            $("#"+map.id+"Div").html(html);
        },
        errorDivIdError : function(html,map){
            $("#"+map.errorDivId).html("");
            $("#"+map.errorDivId).html(html);
        },
        nullAllow : function(map){
            if(map.value=='' || map.value==undefined){
                map.info=map.onNullError;
                $.checkExpression.insetFormMap(map);/*保存错误数据*/
                $.checkExpression.infoCueShow(map);/*显示错误信息*/
            }else if(map.value!='' || map.value!=undefined){ map.flag=true;this.deleteFormMap(map);}
        },
        inputNull : function(map){
            if(map.value=='' || map.value==undefined){
                map.flag=false;
            }
        },
        selectAllow : function(map){
            if(map.value=='' || map.value==undefined || map.value==-1){
                map.info=map.onNullError;
                $.checkExpression.insetFormMap(map);/*保存错误数据*/
                $.checkExpression.infoCueShow(map);/*显示错误信息*/
            }else if(map.value!='' || map.value!=undefined){ map.flag=true;this.deleteFormMap(map);}
        },
        deleteCheck : function(map){
            $("#"+map.id+"Tip").remove();
        },
        /*pc端from表单校验*/
        formCheck : function(checkMap){
            $.checkExpression.selectPageIsMap(checkMap);/*获得数据*/
            switch(checkMap.inputType)
            {
                case "input":
                    $.checkExpression.inputProofread(checkMap);
                    break;
                case "textarea":
                    $.checkExpression.inputProofread(checkMap);
                    break;
                case "select":
                    $.checkExpression.selectProofread(checkMap);
                    break;
                default :
                    break;
            }
        },
        formCheckBasic : function(map){
            var exp="";
            if(map.regular!=null && map.regular!=undefined && map.regular!=''){
                exp=map.regular;
            }else{
                exp=eval("regExType."+map.objectType);
            }
            var reg = new RegExp(exp);
            if (!reg.test(map.value)) {
                $.checkExpression.insetFormMap(map);
                map.info=map.onRegularError;
                $.checkExpression.infoCueShow(map);
            }
            else if(reg.test(map.value)){this.deleteFormMap(map);map.flag=true;}
        },
        formCheckMax : function(map){
            var reg = map.value;
            if (reg.length>map.max) {
                $.checkExpression.insetFormMap(map);
                if(map.onMaxError==null || map.onMaxError=='' || map.onMaxError==undefined){
                    map.info=map.onMinOrMaxError;
                }else{
                    map.info=map.onMaxError;
                }
                $.checkExpression.infoCueShow(map);
            }else if(reg.length<map.max){ map.flag=true;this.deleteFormMap(map);}
        },
        formCheckMin : function(map){
            var reg = map.value;
            if (reg.length<map.min) {
                $.checkExpression.insetFormMap(map);
                if(map.onMinError==null || map.onMinError=='' || map.onMinError==undefined)
                    map.info=map.onMinOrMaxError;
                else
                    map.info=map.onMinError;
                $.checkExpression.infoCueShow(map);
            }else if(reg.length>map.min){ map.flag=true;this.deleteFormMap(map);}
        },
        formCheckRange: function(map){
            var reg = map.value;
            if (reg.length<map.min || reg.length >map.max) {
                $.checkExpression.insetFormMap(map);
                map.info=map.onMinOrMaxError;
                $.checkExpression.infoCueShow(map);
            }else if(reg.length>map.min && reg.length <map.max){ map.flag=true;this.deleteFormMap(map);}
        },
        inputProofread :function(checkMap){
            if(checkMap.allowNull) this.nullAllow(checkMap);
            if (checkMap.flag==true  && checkMap.objectType !='') this.formCheckBasic(checkMap);/*基本校验*/
            if(checkMap.min!=''&& checkMap.max!='' && checkMap.flag==true)this.formCheckRange(checkMap);/*校验最大和最小位数*/
            else if(checkMap.max!='' && checkMap.flag==true) this.formCheckMax(checkMap);/*校验最大位数*/
            else if(checkMap.min!='' && checkMap.flag==true) this.formCheckMin(checkMap);/*校验最小位数*/
            if (checkMap.flag) this.deleteCheck(checkMap);
            this.updatePageIsMap(checkMap);
        },
        selectProofread :function(checkMap){
            if(checkMap.allowNull) this.selectAllow(checkMap);
            if (checkMap.flag==true  && checkMap.objectType !='') this.formCheckBasic(checkMap);/*基本校验*/
            if (checkMap.flag) this.deleteCheck(checkMap);
            this.updatePageIsMap(checkMap);
        },
        /*ajax校验*/
        ajaxForm : function(checkMap)
        {
            $.checkExpression.selectPageIsMap(checkMap);/*获得数据*/
            checkMap.checkType=5;
            checkMap.info=checkMap.onWait;
            $.checkExpression.infoCueShow(checkMap);/*显示数据提交时提示*/
            var ls_url = checkMap.url;
            $.ajax(
                {
                    type : checkMap.type,
                    url : ls_url,
                    cache : checkMap.cache,
                    data : checkMap.data,
                    async : checkMap.async,
                    timeout : checkMap.timeout,
                    dataType : checkMap.dataType,
                    success : function(data, textStatus, jqXHR){
                        var lb_ret,lb_isValid = false;
                        /*根据业务判断设置显示信息*/
                        lb_ret = checkMap.success(data, textStatus, jqXHR);
                        if((typeof lb_ret)=="string")
                        {
                            checkMap.info=checkMap.onError;
                        }
                        else if(lb_ret){
                            checkMap.info=checkMap.onCorrect;
                            lb_isValid=true;
                        }else{
                            checkMap.info=checkMap.onError;
                        }
                        checkMap.checkType=4;
                        /*显示错误信息*/
                        if(lb_isValid){
                            $.checkExpression.deleteFormMap(checkMap);
                            if(checkMap.onCorrect!=null && checkMap.onCorrect!=undefined && checkMap.onCorrect!=""){
                                $.checkExpression.infoCueShow(checkMap);
                            }else{
                                $.checkExpression.deleteCheck(checkMap);
                            }
                        }else{
                            checkMap.ajaxClassFlag=false;
                            $.checkExpression.insetFormMap(checkMap);
                            $.checkExpression.infoCueShow(checkMap);
                        }
                    },
                    complete : function(jqXHR, textStatus){
                        checkMap.complete(jqXHR, textStatus);
                    },
                    error : function(jqXHR, textStatus, errorThrown){
                        checkMap.error(jqXHR, textStatus, errorThrown);
                    }
                });
        },
        /*校验输入内容是否正确*/
        inputCheck : function(checkMap){
            if(checkMap.allowNull) this.inputNullAllow(checkMap);
            if (checkMap.flag  && checkMap.objectType !='') this.inputCheckBasic(checkMap);/*基本校验*/
            if(checkMap.min!=''&& checkMap.max!='' && checkMap.flag==true)this.inputCheckRange(checkMap);/*校验最大和最小位数*/
            else if(checkMap.max!='' && checkMap.flag==true) this.inputCheckMax(checkMap);/*校验最大位数*/
            else if(checkMap.min!='' && checkMap.flag==true) this.inputCheckMin(checkMap);/*校验最小位数*/
            return checkMap.flag;
        },
        inputNullAllow : function(map){
            if(map.value=='' || map.value==undefined){
                map.flag=false;
            }else if(map.value!='' || map.value!=undefined){map.flag=true;}
        },
        inputCheckBasic : function(map){
            var exp="";
            if(map.regular!=null && map.regular!=undefined && map.regular!=''){
                exp=map.regular;
            }else{
                exp=eval("regExType."+map.objectType);
            }
            var reg = new RegExp(exp);
            if (!reg.test(map.value)) {
                map.flag=false;
            }else if(reg.test(map.value)){ map.flag=true;}
        },
        inputCheckMax : function(map){
            var reg = map.value;
            if (reg.length>map.max) {
                map.flag=false;
            }else{ map.flag=true;}
        },
        inputCheckMin : function(map){
            var reg = map.value;
            if (reg.length<map.min) {
                map.flag=false;
            }else{ map.flag=true;}
        },
        inputCheckRange: function(map){
            var reg = map.value;
            if (reg.length<map.min || reg.length >map.max) {
                map.flag=false;
            }else { map.flag=true;}
        }

    };

    $.fn.formProofread = function(map){/*判断并进行提示*/
        return this.each(function() {
            var jqobj = $(this);
            map.id = $(this).attr('id');
            if(map.id==null || map.id=="" || map.id==undefined){
                var timestamp = (new Date()).valueOf();
                $(this).attr('id',timestamp);
                map.id =timestamp;
            }
            var srcTag=this.tagName.toLowerCase();
            map.inputType=srcTag;
            $.checkExpression.insetPageIsMap(map);/*保存校验数据*/
            this.fromType=map.fromType;
            this.pageIsId=map.pageIsId;
            $.checkExpression.formCheck(map);
            if(srcTag == "input" || srcTag=="textarea"){
                jqobj.bind("blur", function () {/*失去焦点*/
                    map.onCueShow = true;
                    map.classType = true;
                    map.checkType = 1;
                    $.checkExpression.updatePageIsMap(map);
                    if (map.blurFlag != false) $.checkExpression.formCheck(map);
                    if (map.flag == true && map.ajax == 1) {
                        $.checkExpression.ajaxForm(map);
                    }
                });
                jqobj.focus(function () {/*获得焦点*/
                    map.onCueShow = true;
                    map.classType = false;
                    map.checkType = 2;
                    $.checkExpression.updatePageIsMap(map);
                    if (map.focusFlag != false) $.checkExpression.formCheck(map);
                });
                jqobj.bind('keyup', function () {/*输入时*/
                    if ($("#" + map.id + "Tip").length <= 0) map.onCueShow = false;
                    else map.onCueShow = true;
                    map.classType = false;
                    map.checkType = 3;
                    $.checkExpression.updatePageIsMap(map);
                    if (map.inputFlag != false)  $.checkExpression.formCheck(map);
                });
            }else if (srcTag == "select")
            {
                jqobj.bind({
                    /*获得焦点*/
                    focus: function(){
                        map.onCueShow = true;
                        map.classType = false;
                        map.checkType = 2;
                        $.checkExpression.updatePageIsMap(map);
                        if (map.focusFlag != false) $.checkExpression.formCheck(map);
                    },
                    /*失去焦点*/
                    blur: function(){
                        map.onCueShow = true;
                        map.classType = true;
                        map.checkType = 1;
                        $.checkExpression.updatePageIsMap(map);
                        if (map.blurFlag != false) $.checkExpression.formCheck(map);
                    },
                    /*选择项目后触发*/
                    change: function(){
                        if ($("#" + map.id + "Tip").length <= 0) map.onCueShow = false;
                        else map.onCueShow = true;
                        map.classType = false;
                        map.checkType = 3;
                        $.checkExpression.updatePageIsMap(map);
                        if (map.inputFlag != false)  $.checkExpression.formCheck(map);
                    }
                });
            }

        });
    };
    $.fn.ajaxProofread = function(map){/*动态Ajax校验*/
        map.id = $(this).attr('id');
        if(map.id==null || map.id=="" || map.id==undefined){
            var timestamp = (new Date()).valueOf();
            $(this).attr('id',timestamp);
            map.id =timestamp;
        }
        map.ajax = 1;
        return this.each(function() {
            map.fromType=this.fromType;
            map.pageIsId=this.pageIsId;
            $.checkExpression.mapExtend(map, ajaxSetting);
            $.checkExpression.updatePageIsMap(map);
        })
    };

    $.fn.inputProofread = function(map){/*只判断不提示*/
        map.id=$(this).attr('id');
        if(map.id==null || map.id=="" || map.id==undefined){
            var timestamp = (new Date()).valueOf();
            $(this).attr('id',timestamp);
            map.id =timestamp;
        }
        var retFlag=false;
        $.checkExpression.checkValueContrast(map);
        var flag= $.checkExpression.inputCheck(map);
        if(flag==true){
            retFlag= false;
        }else{
            retFlag= true;
        }
        return retFlag;
    };

    $.formPageIsPc =function(pageIsId){/*表单提交时检查是否存在错误信息*/
        if(pageIsId=='' || pageIsId==undefined){
            pageIsId="form";
        }
        var fromFlag="pc";
        var flag=$.checkExpression.checkFormIsMap(pageIsId,fromFlag);
        if(flag==true) $.checkExpression.checkPageIsShow(pageIsId,fromFlag);
        return flag;
    };

    $.formPageIsApp =function(pageIsId){/*表单提交时检查是否存在错误信息*/
        if(pageIsId=='' || pageIsId==undefined){
            pageIsId="form";
        }
        var fromFlag="app";
        var flag=$.checkExpression.checkFormIsMap(pageIsId,fromFlag);
        if(flag==true) $.checkExpression.checkPageIsShow(pageIsId,fromFlag);
        return flag;
    };
    $.goBlank=function(str){/*去掉前后空格*/
        return str.replace(/(^\s*)|(\s*$)/g, "");
    };
    /*
     * 密码强度校验
     * 3.高：含大写字母、小写字母、数字、特殊字符中的任意3项及以上，且位数超过11（含）位；
     * 2.中：其他；
     * 1.低：仅包含大写字母、小写字母、数字中的1项，且在10位（含）以下
     * 0.不符合基本校验
     * */
    $.fn.pwdGrade =function (){
        var cat, num;
        var str=$(this).val();
        var minMax=/.{1,20}/;
        if (!minMax.test(str)) {
            return 0;
        }
        var specialZz = new RegExp(eval("regExType.digtOrEglsOrspecial"));
        if (!specialZz.test(str)) {
            return 0;
        }
        var len = str.length;
        var maths,mathsDigit,smalls,smallsDigit,bigs,bigsDigit,special,specialDigit;
        var specialCat = new RegExp(eval("regExType.specials"));
        special=specialCat.test(str);
        specialDigit=str.split(specialCat).length-1;
        var mathsCat = /\d/;
        maths = mathsCat.test(str);
        var mathsMap=str.split(mathsCat);
        mathsDigit=mathsMap.length-1;
        var smallsCat = /[a-z]/;
        smalls = smallsCat.test(str);
        smallsDigit=str.split(smallsCat).length-1;
        var bigsCat = /[A-Z]/;
        bigs = bigsCat.test(str);
        bigsDigit=str.split(bigsCat).length-1;
        /*alert("数字 ： "+maths+" 数字位数 "+mathsDigit+" 特殊字符： "+special+" 特殊字符位数： "+specialDigit +" 小字母： "+smalls+" 小字母位数： "+smallsDigit+" 大字母： "+bigs+" 大字母位数： "+bigsDigit);*/
        num=maths + smalls + bigs+special;
        cat=maths + smalls + bigs;
        if(num>=3&&len>=11){
            return 3;
        }else if(cat=1 && len<=10){
            return 1;
        }else{
            return 2;
        }
    };

})(jQuery);
var checkSetting =
{
    id:'',/*输入框id*/
    allowNull:true,/*是否为空*/
    min:'',/*最小位数*/
    max:'',/*最大位数*/
    value:'',/*校验值*/
    info:'',/*提示信息*/
    objectType:'',/*校验对象类型*/
    onCueShow:false,/*是否显示提示信息 true:显示   false: 不显示*/
    onCueType:true,/*提示方法类型 true:追加  false:弹出框*/
    onAutomatic:true,/*追加类型是否是自动追加 true:自动  false:不自动*/
    flag:true,/*校验值是否通过 true通过*/
    pageIsId:"form",/*本次校验id*/
    regular:'',/*正则表达式*/
    onNullError:'',/*正则判断空值提示信息*/
    onRegularError:'',/*正则校验错误时信息*/
    onMaxError:'',/*最大位数校验错误信息*/
    onMinError:'',/*最小位数校验错误信息*/
    onMinOrMaxError:'',/*最小位数校验错误信息*/
    blurFlag:true,/*是否启动失去焦点校验*/
    focusFlag:true,/*是否启动得到焦点校验*/
    inputFlag:true, /*是否启动输入时校验*/
    checkType:'0',/*数据校验类型 0：提交时 1：失去焦点 2：得到焦点 3：输入时 4:ajax提交  5：ajax提交时*/
    classType:true,/*提示信息样式  true : 默认样式   false:改变样式*/
    errorTrueClass:"spanOnError", /*提示信息样式为true时class名*/
    errorFalseClass:"spanOnFocus", /*提示信息样式为false时class名*/
    continuousFlag:true, /*提示是否连续提示   false:不连续 永远只提示一个  true：连续  将所以的提示信息提示出来*/
    fromType:"pc" ,/*校验表单类型  分Pc端 和 App端*/
    errorDivId: '' /*自定义提示信息显示divid*/
};
var ajaxSetting =
{
    type : "GET",
    url : window.location.href,
    dataType : "html",
    timeout : 100000,
    data : null,
    async : true,
    cache : false,
    buttons : null,
    beforeSend : function(){return true;},
    success : function(){return true;},
    complete : $.noop,
    processData : true,
    error : $.noop,
    onError:"服务器校验没有通过",
    onWait:"正在等待服务器返回数据",
    onCorrect:'',/*校验通过后提示信息*/
    ajaxClassFlag:true ,/*提示信息样式  true : 默认样式   false:改变样式*/
    ajaxEorTrueClass:"ajaxErrorTrue", /*提示信息样式为true时class名*/
    ajaxEorFalseClass:"ajaxErrorFalse", /*提示信息样式为false时class名*/
    ajaxSubmitClass:"spanOnLoad" /*ajaxSubmit提交时样式*/
};
var mapArray={};
var newMapArray={};
var formArray={};

