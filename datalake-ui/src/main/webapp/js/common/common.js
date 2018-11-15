
var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}

/**
 *  昨天： = GetDateStr(1)
 * 今天： = GetDateStr(0)
 *  明天： = GetDateStr(1)
 */
function GetDateStr(AddDayCount) {
  var date = new Date();
  date.setDate(date.getDate() + AddDayCount);
  return date.Format("yyyy-MM-dd");
  debugger;
}
/*
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * 例子：
 * (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 * (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
 * */
Date.prototype.Format = function (fmt) { //author: meizz
  var o = {
    "M+": this.getMonth() + 1, //月份
    "d+": this.getDate(), //日
    "h+": this.getHours(), //小时
    "m+": this.getMinutes(), //分
    "s+": this.getSeconds(), //秒
    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
    "S": this.getMilliseconds() //毫秒
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
  return fmt;
};

(function ($) {

  /*------------------------------------ 操作时间 ----------------------------------------------*/
  /*
   * fmt: 日期格式 yyyy-MM-dd hh:mm:ss(2006-07-02 08:09:04) 或者 yyyy-M-d h:m:s(2006-7-2 8:9:4)
   * algorithmType: 算法类型 add：加 subtract:减
   * algorithmBase: 算法基数 y:年  M:月   d:日   h:时(0~23) m:分(0~59)
   * baseValue: 基数
   * 例子：
   * var date=new Date('2015-06-06 12:11:11');
   * $.dateAlgorithm(date,'yyyy-MM-dd hh:mm:ss','add','y','1') ==> 2016-06-06 12:11:11
   * $.dateAlgorithm(date,'yyyy-MM-dd hh:mm:ss','add','M','1') ==> 2015-07-06 12:11:11
   * $.dateAlgorithm(date,'yyyy-MM-dd hh:mm:ss','add','d','1') ==> 2015-06-07 12:11:11
   * $.dateAlgorithm(date,'yyyy-MM-dd hh:mm:ss','add','h','1') ==> 2016-06-06 13:11:11
   * $.dateAlgorithm(date,'yyyy-MM-dd hh:mm:ss','add','m','1') ==> 2016-06-06 12:12:11
   * */
  $.dateAlgorithm=function(date,fmt,algorithmType,algorithmBase,baseValue){
    var o = {
      "M+": date.getMonth() + 1, //月份
      "d+": date.getDate(), //日
      "h+": date.getHours(), //小时
      "m+": date.getMinutes(), //分
      "s+": date.getSeconds(), //秒
      "q+": Math.floor((date.getMonth() + 3) / 3), //季度
      "S": date.getMilliseconds() //毫秒
    };
    var val=0;
    if(algorithmType=='add'){
      val=baseValue;
    }else if(algorithmType=='subtract'){
      val=0-baseValue;
    }
    var year=date.getFullYear();
    if(algorithmBase=='y'){
      year=year*1+val*1;
      if((year%4==0 && year%100!=0) || year%400==0){

      }else{
        if(o['M+']*1==2 && o['d+']*1>28){
          o['d+']=28;
        }
      }
    }else if(algorithmBase=='M'){
      var yy=Math.floor(baseValue*1/12);/*获得要跨年个数*/
      var baseM=baseValue-yy*12;/*跨年剩下月数*/

      if(algorithmType=='add'){
        o['M+']=o['M+']*1+baseM;
      }else if(algorithmType=='subtract'){
        o['M+']=o['M+']*1-baseM;
      }
      if(algorithmType=='add' && o['M+']*1>12){
        year=year*1+yy+1;
        o['M+']=o['M+']*1-12;
      }else if(algorithmType=='add' && yy*1>0){
        year=year*1+yy;
      }
      if(algorithmType=='subtract' && o['M+']*1<1){
        year=year*1-yy-1;
        o['M+']=12+o['M+']*1;
      }else if(algorithmType=='subtract' && yy*1>0){
        year=year*1-yy;
      }
      var dArray={};
      dArray[1]=31;
      if((year%4==0 && year%100!=0) || year%400==0){
        dArray[2]=29;
      }else{
        dArray[2]=28;
      }
      dArray[3]=31;
      dArray[4]=30;
      dArray[5]=31;
      dArray[6]=30;
      dArray[7]=31;
      dArray[8]=31;
      dArray[9]=30;
      dArray[10]=31;
      dArray[11]=30;
      dArray[12]=31;
      if(o['d+']*1>dArray[o['M+']*1]){
        o['d+']=dArray[o['M+']*1];
      }
    }else if(algorithmBase=='d'){
      var yy=Math.floor(baseValue*1/365);/*获得要跨年个数*/
      var yr=0;//闰年个数
      var dym=1;
      var iMax=0;
      if(algorithmType=='add' && (o['M+']*1<2 || (o['M+']*1==2 && o['d+']*1<=28))){
        dym=0;
      }else if(algorithmType=='subtract' && o['M+']*1>3){
        dym=0;
      }
      if(algorithmType=='add' && o['M+']*1>=3){
        iMax=yy;
      }else if(algorithmType=='add' && o['M+']*1<3){
        iMax=yy-1;
      }
      if(algorithmType=='subtract' && o['M+']*1>2){
        iMax=yy-1;
      }else if(algorithmType=='subtract' && o['M+']*1<=2){
        iMax=yy;
      }
      for(var i=dym;i<=iMax;i++){
        var years=0;
        if(algorithmType=='add'){
          years=year+i;
        }else if(algorithmType=='subtract'){
          years=year-i;
        }
        if((years%4==0 && years%100!=0) || years%400==0){
          yr++;
        }
      }
      var yd=365*yy+yr;//
      var baseDd=baseValue-yd;/*跨年天数剩下天数*/
      var dd=0;/*累计天数*/
      if(algorithmType=='add'){
        year=year*1+yy;
        dd=o['d+']*1+baseDd;
        var dArray={};
        dArray[1]=31;
        if((year%4==0 && year%100!=0) || year%400==0){
          dArray[2]=29;
        }else{
          dArray[2]=28;
        }
        dArray[3]=31;
        dArray[4]=30;
        dArray[5]=31;
        dArray[6]=30;
        dArray[7]=31;
        dArray[8]=31;
        dArray[9]=30;
        dArray[10]=31;
        dArray[11]=30;
        dArray[12]=31;
        dArray[13]=31;
        if(((year+1)%4==0 && (year+1)%100!=0) || (year+1)%400==0){
          dArray[14]=29;
        }else{
          dArray[14]=28;
        }
        dArray[15]=31;
        dArray[16]=30;
        dArray[17]=31;
        dArray[18]=30;
        dArray[19]=31;
        dArray[20]=31;
        dArray[21]=30;
        dArray[22]=31;
        dArray[23]=30;
        dArray[24]=31;
        var dqM=o['M+']*1;
        var dh=0;
        var dBase=0;
        for (var i=dqM;i<=24;i++){
          dh=dh+dArray[i];
          if(dh>=dd){
            o['d+']=dd-dBase;
            if(i>12){
              o['M+']=i-12;
              year++;
            }else{
              o['M+']=i;
            }
            if(o['d+']<1){
              o['d+']=dArray[i-1];
              if(i==1){
                o['M+']=12;
                year--;
              }else{
                o['M+']=o['M+']*1-1;
              }
            }
            break;
          }
          dBase=dh;
        }
      }else if(algorithmType=='subtract'){
        year=year*1-yy;
        dd=o['d+']*1-baseDd;
        var dArray={};
        dArray[1]=31;
        if(((year-1)%4==0 && (year-1)%100!=0) || (year-1)%400==0){
          dArray[2]=29;
        }else{
          dArray[2]=28;
        }
        dArray[3]=31;
        dArray[4]=30;
        dArray[5]=31;
        dArray[6]=30;
        dArray[7]=31;
        dArray[8]=31;
        dArray[9]=30;
        dArray[10]=31;
        dArray[11]=30;
        dArray[12]=31;
        dArray[13]=31;
        if((year%4==0 && year%100!=0) || year%400==0){
          dArray[14]=29;
        }else{
          dArray[14]=28;
        }
        dArray[15]=31;
        dArray[16]=30;
        dArray[17]=31;
        dArray[18]=30;
        dArray[19]=31;
        dArray[20]=31;
        dArray[21]=30;
        dArray[22]=31;
        dArray[23]=30;
        dArray[24]=31;
        var dqM=o['M+']*1+12;
        var dh=0;
        for (var i=dqM;i<=24;i--){
          if(i==dqM){
            dh=0;
          }else{
            dh=dh-dArray[i];
          }
          if(dh<dd){
            o['d+']=dd-dh;
            if(i<=12){
              o['M+']=i;
              year--;
            }else{
              o['M+']=i-12;
            }
            if(o['d+']*1>dArray[i]){
              o['d+']=o['d+']*1-dArray[i];
              if(i==12){
                o['M+']=1;
                year++;
              }else{
                o['M+']=o['M+']*1+1;
              }
            }
            break;
          }
        }
      }
    }else if(algorithmBase=='h'){
      if(val*1<24 && val*1>-24){
        var dArray={};
        dArray[1]=31;
        if((year%4==0 && year%100!=0) || year%400==0){
          dArray[2]=29;
        }else{
          dArray[2]=28;
        }
        dArray[3]=31;
        dArray[4]=30;
        dArray[5]=31;
        dArray[6]=30;
        dArray[7]=31;
        dArray[8]=31;
        dArray[9]=30;
        dArray[10]=31;
        dArray[11]=30;
        dArray[12]=31;
        if(algorithmType=='add'){
          var dh=o["h+"]*1+val*1;
          if(dh>=24){
            o["h+"]=dh-24;
            o['d+']=o['d+']*1+1;
          }else{
            o["h+"]=dh;
          }
          if(o['d+']*1>dArray[o['M+']*1]){
            o['d+']=o['d+']*1-dArray[o['M+']*1];
            o['M+']=o['M+']*1+1;
          }
          if(o['M+']*1>12){
            year++;
            o['M+']=o['M+']*1-12;
          }
        }else if(algorithmType=='subtract'){
          var dh=o["h+"]*1+val*1;
          if(dh<0){
            o["h+"]=24+dh;
            o['d+']=o['d+']*1-1;
          }else{
            o["h+"]=dh;
          }
          if(o['d+']*1<=0){
            o['M+']=o['M+']*1-1;
            var key=o['M+']*1;
            if(key*1==0){
              key=12;
            }
            o['d+']=dArray[key]+o['d+']*1
          }
          if(o['M+']*1<=0){
            year--;
            o['M+']=12+o['M+']*1;
          }
        }
      }
    }else if(algorithmBase=='m'){
      if(val<59 && val>-59) {
        var dArray = {};
        dArray[1] = 31;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
          dArray[2] = 29;
        } else {
          dArray[2] = 28;
        }
        dArray[3] = 31;
        dArray[4] = 30;
        dArray[5] = 31;
        dArray[6] = 30;
        dArray[7] = 31;
        dArray[8] = 31;
        dArray[9] = 30;
        dArray[10] = 31;
        dArray[11] = 30;
        dArray[12] = 31;
        if(algorithmType=='add'){
          var mh=o["m+"]*1+val*1;
          if(mh>=60){
            o["m+"]=mh-60;
            o['h+']=o['h+']*1+1;
          }else{
            o["m+"]=mh;
          }
          if(o["h+"]*1>=24){
            o["h+"]=o["h+"]*1-24;
            o['d+']=o['d+']*1+1;
          }
          if(o['d+']*1>dArray[o['M+']*1]){
            o['d+']=o['d+']*1-dArray[o['M+']*1];
            o['M+']=o['M+']*1+1;
          }
          if(o['M+']*1>12){
            year++;
            o['M+']=o['M+']*1-12;
          }
        }else if(algorithmType=='subtract'){
          var mh=o["m+"]*1+val*1;
          if(mh<0){
            o["m+"]=60+mh;
            o['h+']=o['h+']*1-1;
          }else{
            o["m+"]=mh;
          }
          if(o["h+"]*1<0){
            o["h+"]=24+o["h+"]*1;
            o['d+']=o['d+']*1-1;
          }
          if(o['d+']*1<=0){
            o['M+']=o['M+']*1-1;
            var key=o['M+']*1;
            if(key*1==0){
              key=12;
            }
            o['d+']=dArray[key]+o['d+']*1
          }
          if(o['M+']*1<=0){
            year--;
            o['M+']=12+o['M+']*1;
          }
        }
      }
    }
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (year + "").substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
  };
  /*
   * 将天、时、秒转成毫秒
   * @param str 默认秒：20是代表20秒  h是指小时：如12小时则是：h12  d是天数：30天则：d30
   * @return 毫秒
   * */
  $.getMsec=function (str){
    var str1=str.substring(1,str.length)*1;
    var str2=str.substring(0,1);
    if (str2=="h")
    {
      return str1*60*60*1000;
    }
    else if (str2=="d")
    {
      return str1*24*60*60*1000;
    }else{
      return str1*1000;
    }
  };
  /**
   * 日期 转换为 Unix时间戳
   * @param <string> 2014-01-01 20:20:20 日期格式
   * @return <int>    unix时间戳(秒)
   * 例子：
   * $.dateToUnix("2016-04-12 10:49:59") ==> '1460429399'
   */
  $.dateToUnix= function(string) {
    var f = string.split(' ', 2);
    var d = (f[0] ? f[0] : '').split('-', 3);
    var t = (f[1] ? f[1] : '').split(':', 3);
    return (new Date(
            parseInt(d[0], 10) || null,
            (parseInt(d[1], 10) || 1) - 1,
            parseInt(d[2], 10) || null,
            parseInt(t[0], 10) || null,
            parseInt(t[1], 10) || null,
            parseInt(t[2], 10) || null
        )).getTime() / 1000;
  };
  /**
   * 时间戳转换日期
   * @param <int> unixTime  待时间戳(秒)
   * @param <bool> isFull  返回完整时间(yyyy-MM-dd 或者 yyyy-MM-dd hh:mm:ss)
   * 例子：
   * $.unixToDate("1460429399") ==> '2016-04-12 10:49:59'
   */
  $.unixToDate= function(unixTime, isFull) {
    var date=new Date(parseInt(unixTime) * 1000);
    return date.Format(isFull);
  };

  /*------------------------------------ 操作url ----------------------------------------------*/
  /*
   * 获得url中参数指定参数
   * val参数名称
   * */
  $.QueryString = function(val) {
    var uri = window.location.search;
    var re = new RegExp("" +val+ "=([^&?]*)", "ig");
    return ((uri.match(re)) ? decodeURI(uri.match(re)[0].substr(val.length+1)) : null);
  };
  /*
   * 获得url中参数列表
   * */
  $.QueryStringArray = function() {
    var uri = window.location.search.replace(/\?/g,'');
    var re = new RegExp("=([^&?]*)", "ig");
    var parameter=uri.split(/&/g);
    var array={};
    $.each(parameter,function(){
      array[this.split(/=/g)[0]]=decodeURI(this.split(/=/g)[1]);
    });
    return array;
  };
  /*
   * url特殊字符编码转译
   * */
  $.encodeContent = function(data){
    return encodeURI(data).replace(/&/g,'%26').replace(/\+/g,'%2B').replace(/\s/g,'%20').replace(/#/g,'%23').replace(/\n/g,'%0a');
  };
  /*
   * url特殊字符编码解析
   * */
  $.encodeAnalysis = function(data){
    return decodeURI(data);
  };
  $.JQEncodeAnalysis= function(data){
    return decodeURI(data).replace(/%BD/g,'').replace(/%BF/g,'').replace(/%EF/g,'');
  };
  /*
   * url参数加密
   */
  $.urlParameterEncryption=function(parameter){
    return $.encodeBase64(parameter,"1");
  };
  /*
   * url参数解密
   */
  $.urlParameterDecryption=function(parameter){
    var val= $.decodeBase64(parameter,"1");
    return val;
  };

  /*------------------------------------ Base64加密解密 ----------------------------------------------*/
  //加密方法。没有过滤首尾空格，即没有trim.
  //加密可以加密N次，对应解密N次就可以获取明文
  $.encodeBase64 = function(val,times){
    val=$.goBlank(val);
    var code="";
    var num=1;
    if(typeof times=='undefined'||times==null||times==""){
      num=1;
    }else{
      var vt=times+"";
      num=parseInt(vt);
    }
    if(typeof val=='undefined'||val==null||val==""){

    }else{
      code=val;
      for(var i=0;i<num;i++){
        code=doEncode(code);
      }
    }
    return code;
  };

  //解密方法。没有过滤首尾空格，即没有trim
  //加密可以加密N次，对应解密N次就可以获取明文
  $.decodeBase64 = function (val,times){
    val=$.goBlank(val);
    var mingwen="";
    var num=1;
    if(typeof times=='undefined'||times==null||times==""){
      num=1;
    }else{
      var vt=times+"";
      num=parseInt(vt);
    }
    if(typeof val=='undefined'||val==null||val==""){

    }else{
      mingwen=val;
      for(var i=0;i<num;i++){
        mingwen=doDecode(mingwen);
      }
    }
    return mingwen;
  };


  /*------------------------------------ 操作证件 ----------------------------------------------*/
  /*
   * 根据身份证获得地区,出生日期,性别
   * cardId:身份证
   * 例子：
   * $.cardIdSplit('340826199906066632') ==> {region:'安徽',birthday:'1999-06-06',sex:'男'}
   *  */
  $.cardIdSplit = function (cardId){
    var iSum=0 ;
    var object={region:'',birthday:'',sex:''};
    if(!/^\d{17}(\d|x)$/i.test(cardId)) return object;
    cardId=cardId.replace(/x$/i,"a");
    var sBirthday=cardId.substr(6,4)+"-"+Number(cardId.substr(10,2))+"-"+Number(cardId.substr(12,2));
    var d=new Date(sBirthday.replace(/-/g,"/"));
    if(sBirthday==(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))
      object.birthday=sBirthday;
    for(var i = 17;i>=0;i --) iSum += (Math.pow(2,i) % 11) * parseInt(cardId.charAt(17 - i),11) ;
    if(iSum%11!=1) return {region:'',birthday:'',sex:''};
    object.sex=cardId.substr(16,1)%2?"男":"女";
    object.region=aCity[parseInt(cardId.substr(0,2))];
    return object;
  };

  /*------------------------------------ 操作字符串 ----------------------------------------------*/
  /*
   * 字符串替换
   * data: 字符串
   * str: 需要替换的字符串
   * repStr: 替换后的字符串
   * 例子：
   * $.replace('ccc*ddd','c*d',"c,d") ==> ccc,ddd
   *  */
  $.replace=function(data,str,repStr){
    if(data.indexOf(str) > 0 ){
      str=$.stringContent(str);
      return data.replace(new RegExp(str,"g"),repStr);
    }else{
      return data;
    }
  };
  /*
   * 字符串分割
   * data: 字符串
   * str: 需要分割的字符串
   * 例子：
   * $.split('ccc*ddd',"*")  ==> ['ccc','ddd']
   *  */
  $.split=function(data,str){
    str=$.stringContent(str);
    return data.split(new RegExp(str,"g"));
  };
  /*
   * 字符串设置
   * str: 需要设置的字符串
   * 例子：
   * $.stringContent("sss*ddd") ==> 'sss\*ddd'
   *  */
  $.stringContent=function(str){
    if(str.indexOf("\\")>=0){
      str=str.replace(new RegExp("\\\\","g"),"\\\\");
    }
    if(str.indexOf("?")>=0){
      str=str.replace(new RegExp("\\?","g"),"\\?");
    }
    if(str.indexOf("$")>=0){
      str=str.replace(new RegExp("\\$","g"),"\\$");
    }
    if(str.indexOf("^")>=0){
      str=str.replace(new RegExp("\\^","g"),"\\^");
    }
    if(str.indexOf("*")>=0){
      str=str.replace(new RegExp("\\*","g"),"\\*");
    }
    if(str.indexOf("(")>=0){
      str=str.replace(new RegExp("\\(","g"),"\\(");
    }
    if(str.indexOf(")")>=0){
      str=str.replace(new RegExp("\\)","g"),"\\)");
    }
    if(str.indexOf("+")>=0){
      str=str.replace(new RegExp("\\+","g"),"\\+");
    }
    if(str.indexOf("|")>=0){
      str=str.replace(new RegExp("\\|","g"),"\\|");
    }
    if(str.indexOf(".")>=0){
      str=str.replace(new RegExp("\\.","g"),"\\.");
    }
    return str;
  };
  /*
   * 去除前后空格
   * 例子：
   * $.goBlank(" dddd ") ==> "dddd"
   * */
  $.goBlank = function(str){
    return str.replace(/(^\s*)|(\s*$)/g, "");
  };
  /*
   * 数字转换成中文数字
   * 例子：
   * $.convertToChinese("192") ==> "一九二"
   * */
  $.convertToChinese=function(num){
    var N = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九"];
    var str = num.toString();
    var len = num.toString().length;
    var C_Num = [];
    for(var i = 0; i < len; i++){
      C_Num.push(N[str.charAt(i)]);
    }
    return C_Num.join('');
  };
  /*
   * 字符串空数据处理
   * 例子：
   * $.stringBlank("null") ==> ""
   * */
  $.stringBlank=function(str){
    var ret="";
    if(str!==null && str!=undefined && str!=""){
      ret=str;
    }
    return ret;
  };
  /*------------------------------------ 操作Cookie ----------------------------------------------*/
  /*
   * 操作Cookie  添加
   * @param name
   * @param value
   * @param time 默认秒：20是代表20秒  h是指小时：如12小时则是：h12  d是天数：30天则：d30
   * @return
   * */
  $.SetCookie = function (name,value,time){
    var strMsec = $.getMsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strMsec*1);
    value=$.encodeBase64(value);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString()+";path=/";

  };
  /*
   * 操作Cookie  提取   后台必须是escape编码
   * @param name
   * @return
   * */
  $.GetCookie=function (name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) return $.decodeBase64(unescape(arr[2])); return null;
  };
  /**
   * 操作Cookie 删除
   * @param name
   * @return
   */
  $.delCookie=function(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
      var cval= "";
      document.cookie= name + "="+cval+";expires="+exp.toGMTString()+";path=/";
  };


})(jQuery);
/*  des加密解密方法   */
function des(key,message,encrypt,mode,iv){
  //declaring this locally speeds things up a bit
  var spfunction1=new Array(0x1010400,0,0x10000,0x1010404,0x1010004,0x10404,0x4,0x10000,0x400,0x1010400,0x1010404,0x400,0x1000404,0x1010004,0x1000000,0x4,0x404,0x1000400,0x1000400,0x10400,0x10400,0x1010000,0x1010000,0x1000404,0x10004,0x1000004,0x1000004,0x10004,0,0x404,0x10404,0x1000000,0x10000,0x1010404,0x4,0x1010000,0x1010400,0x1000000,0x1000000,0x400,0x1010004,0x10000,0x10400,0x1000004,0x400,0x4,0x1000404,0x10404,0x1010404,0x10004,0x1010000,0x1000404,0x1000004,0x404,0x10404,0x1010400,0x404,0x1000400,0x1000400,0,0x10004,0x10400,0,0x1010004);
  var spfunction2=new Array(-0x7fef7fe0,-0x7fff8000,0x8000,0x108020,0x100000,0x20,-0x7fefffe0,-0x7fff7fe0,-0x7fffffe0,-0x7fef7fe0,-0x7fef8000,-0x80000000,-0x7fff8000,0x100000,0x20,-0x7fefffe0,0x108000,0x100020,-0x7fff7fe0,0,-0x80000000,0x8000,0x108020,-0x7ff00000,0x100020,-0x7fffffe0,0,0x108000,0x8020,-0x7fef8000,-0x7ff00000,0x8020,0,0x108020,-0x7fefffe0,0x100000,-0x7fff7fe0,-0x7ff00000,-0x7fef8000,0x8000,-0x7ff00000,-0x7fff8000,0x20,-0x7fef7fe0,0x108020,0x20,0x8000,-0x80000000,0x8020,-0x7fef8000,0x100000,-0x7fffffe0,0x100020,-0x7fff7fe0,-0x7fffffe0,0x100020,0x108000,0,-0x7fff8000,0x8020,-0x80000000,-0x7fefffe0,-0x7fef7fe0,0x108000);
  var spfunction3=new Array(0x208,0x8020200,0,0x8020008,0x8000200,0,0x20208,0x8000200,0x20008,0x8000008,0x8000008,0x20000,0x8020208,0x20008,0x8020000,0x208,0x8000000,0x8,0x8020200,0x200,0x20200,0x8020000,0x8020008,0x20208,0x8000208,0x20200,0x20000,0x8000208,0x8,0x8020208,0x200,0x8000000,0x8020200,0x8000000,0x20008,0x208,0x20000,0x8020200,0x8000200,0,0x200,0x20008,0x8020208,0x8000200,0x8000008,0x200,0,0x8020008,0x8000208,0x20000,0x8000000,0x8020208,0x8,0x20208,0x20200,0x8000008,0x8020000,0x8000208,0x208,0x8020000,0x20208,0x8,0x8020008,0x20200);
  var spfunction4=new Array(0x802001,0x2081,0x2081,0x80,0x802080,0x800081,0x800001,0x2001,0,0x802000,0x802000,0x802081,0x81,0,0x800080,0x800001,0x1,0x2000,0x800000,0x802001,0x80,0x800000,0x2001,0x2080,0x800081,0x1,0x2080,0x800080,0x2000,0x802080,0x802081,0x81,0x800080,0x800001,0x802000,0x802081,0x81,0,0,0x802000,0x2080,0x800080,0x800081,0x1,0x802001,0x2081,0x2081,0x80,0x802081,0x81,0x1,0x2000,0x800001,0x2001,0x802080,0x800081,0x2001,0x2080,0x800000,0x802001,0x80,0x800000,0x2000,0x802080);
  var spfunction5=new Array(0x100,0x2080100,0x2080000,0x42000100,0x80000,0x100,0x40000000,0x2080000,0x40080100,0x80000,0x2000100,0x40080100,0x42000100,0x42080000,0x80100,0x40000000,0x2000000,0x40080000,0x40080000,0,0x40000100,0x42080100,0x42080100,0x2000100,0x42080000,0x40000100,0,0x42000000,0x2080100,0x2000000,0x42000000,0x80100,0x80000,0x42000100,0x100,0x2000000,0x40000000,0x2080000,0x42000100,0x40080100,0x2000100,0x40000000,0x42080000,0x2080100,0x40080100,0x100,0x2000000,0x42080000,0x42080100,0x80100,0x42000000,0x42080100,0x2080000,0,0x40080000,0x42000000,0x80100,0x2000100,0x40000100,0x80000,0,0x40080000,0x2080100,0x40000100);
  var spfunction6=new Array(0x20000010,0x20400000,0x4000,0x20404010,0x20400000,0x10,0x20404010,0x400000,0x20004000,0x404010,0x400000,0x20000010,0x400010,0x20004000,0x20000000,0x4010,0,0x400010,0x20004010,0x4000,0x404000,0x20004010,0x10,0x20400010,0x20400010,0,0x404010,0x20404000,0x4010,0x404000,0x20404000,0x20000000,0x20004000,0x10,0x20400010,0x404000,0x20404010,0x400000,0x4010,0x20000010,0x400000,0x20004000,0x20000000,0x4010,0x20000010,0x20404010,0x404000,0x20400000,0x404010,0x20404000,0,0x20400010,0x10,0x4000,0x20400000,0x404010,0x4000,0x400010,0x20004010,0,0x20404000,0x20000000,0x400010,0x20004010);
  var spfunction7=new Array(0x200000,0x4200002,0x4000802,0,0x800,0x4000802,0x200802,0x4200800,0x4200802,0x200000,0,0x4000002,0x2,0x4000000,0x4200002,0x802,0x4000800,0x200802,0x200002,0x4000800,0x4000002,0x4200000,0x4200800,0x200002,0x4200000,0x800,0x802,0x4200802,0x200800,0x2,0x4000000,0x200800,0x4000000,0x200800,0x200000,0x4000802,0x4000802,0x4200002,0x4200002,0x2,0x200002,0x4000000,0x4000800,0x200000,0x4200800,0x802,0x200802,0x4200800,0x802,0x4000002,0x4200802,0x4200000,0x200800,0,0x2,0x4200802,0,0x200802,0x4200000,0x800,0x4000002,0x4000800,0x800,0x200002);
  var spfunction8=new Array(0x10001040,0x1000,0x40000,0x10041040,0x10000000,0x10001040,0x40,0x10000000,0x40040,0x10040000,0x10041040,0x41000,0x10041000,0x41040,0x1000,0x40,0x10040000,0x10000040,0x10001000,0x1040,0x41000,0x40040,0x10040040,0x10041000,0x1040,0,0,0x10040040,0x10000040,0x10001000,0x41040,0x40000,0x41040,0x40000,0x10041000,0x1000,0x40,0x10040040,0x1000,0x41040,0x10001000,0x40,0x10000040,0x10040000,0x10040040,0x10000000,0x40000,0x10001040,0,0x10041040,0x40040,0x10000040,0x10040000,0x10001000,0x10001040,0,0x10041040,0x41000,0x41000,0x1040,0x1040,0x40040,0x10000000,0x10041000);

  //create the 16 or 48 subkeys we will need
  var keys=des_createKeys(key);
  var m=0,i,j,temp,temp2,right1,right2,left,right,looping;
  var cbcleft,cbcleft2,cbcright,cbcright2
  var endloop,loopinc;
  var len=message.length;
  var chunk=0;
  //set up the loops for single and triple des
  var iterations=keys.length==32?3 :9;//single or triple des
  if(iterations==3){looping=encrypt?new Array(0,32,2):new Array(30,-2,-2);}
  else{looping=encrypt?new Array(0,32,2,62,30,-2,64,96,2):new Array(94,62,-2,32,64,2,30,-2,-2);}

  message+="\0\0\0\0\0\0\0\0";//pad the message out with null bytes
  //store the result here
  var result="";
  var tempresult="";

  if(mode==1){//CBC mode
    cbcleft=(iv.charCodeAt(m++)<<24)|(iv.charCodeAt(m++)<<16)|(iv.charCodeAt(m++)<<8)|iv.charCodeAt(m++);
    cbcright=(iv.charCodeAt(m++)<<24)|(iv.charCodeAt(m++)<<16)|(iv.charCodeAt(m++)<<8)|iv.charCodeAt(m++);
    m=0;
  }

  //loop through each 64 bit chunk of the message
  while(m<len){
    if(encrypt){
      left=(message.charCodeAt(m++)<<16)|message.charCodeAt(m++);
      right=(message.charCodeAt(m++)<<16)|message.charCodeAt(m++);
    }else{
      left=(message.charCodeAt(m++)<<24)|(message.charCodeAt(m++)<<16)|(message.charCodeAt(m++)<<8)|message.charCodeAt(m++);
      right=(message.charCodeAt(m++)<<24)|(message.charCodeAt(m++)<<16)|(message.charCodeAt(m++)<<8)|message.charCodeAt(m++);
    }
    //for Cipher Block Chaining mode,xor the message with the previous result
    if(mode==1){if(encrypt){left^=cbcleft;right^=cbcright;}else{cbcleft2=cbcleft;cbcright2=cbcright;cbcleft=left;cbcright=right;}}

    //first each 64 but chunk of the message must be permuted according to IP
    temp=((left>>>4)^right)&0x0f0f0f0f;right^=temp;left^=(temp<<4);
    temp=((left>>>16)^right)&0x0000ffff;right^=temp;left^=(temp<<16);
    temp=((right>>>2)^left)&0x33333333;left^=temp;right^=(temp<<2);
    temp=((right>>>8)^left)&0x00ff00ff;left^=temp;right^=(temp<<8);
    temp=((left>>>1)^right)&0x55555555;right^=temp;left^=(temp<<1);

    left=((left<<1)|(left>>>31));
    right=((right<<1)|(right>>>31));

    //do this either 1 or 3 times for each chunk of the message
    for(j=0;j<iterations;j+=3){
      endloop=looping[j+1];
      loopinc=looping[j+2];
      //now go through and perform the encryption or decryption
      for(i=looping[j];i!=endloop;i+=loopinc){//for efficiency
        right1=right^keys[i];
        right2=((right>>>4)|(right<<28))^keys[i+1];
        //the result is attained by passing these bytes through the S selection functions
        temp=left;
        left=right;
        right=temp^(spfunction2[(right1>>>24)&0x3f]|spfunction4[(right1>>>16)&0x3f]|spfunction6[(right1>>>8)&0x3f]|spfunction8[right1&0x3f]|spfunction1[(right2>>>24)&0x3f]|spfunction3[(right2>>>16)&0x3f]|spfunction5[(right2>>>8)&0x3f]|spfunction7[right2&0x3f]);
      }
      temp=left;left=right;right=temp;//unreverse left and right
    }//for either 1 or 3 iterations

    //move then each one bit to the right
    left=((left>>>1)|(left<<31));
    right=((right>>>1)|(right<<31));

    //now perform IP-1,which is IP in the opposite direction
    temp=((left>>>1)^right)&0x55555555;right^=temp;left^=(temp<<1);
    temp=((right>>>8)^left)&0x00ff00ff;left^=temp;right^=(temp<<8);
    temp=((right>>>2)^left)&0x33333333;left^=temp;right^=(temp<<2);
    temp=((left>>>16)^right)&0x0000ffff;right^=temp;left^=(temp<<16);
    temp=((left>>>4)^right)&0x0f0f0f0f;right^=temp;left^=(temp<<4);

    //for Cipher Block Chaining mode,xor the message with the previous result
    if(mode==1){if(encrypt){cbcleft=left;cbcright=right;}else{left^=cbcleft2;right^=cbcright2;}}
    if(encrypt){
      tempresult+=String.fromCharCode((left>>>24),((left>>>16)&0xff),((left>>>8)&0xff),(left&0xff),(right>>>24),((right>>>16)&0xff),((right>>>8)&0xff),(right&0xff));
    }
    else{
      tempresult+=String.fromCharCode(((left>>>16)&0xffff),(left&0xffff),((right>>>16)&0xffff),(right&0xffff));
    }
    encrypt?chunk+=16:chunk+=8;
    if(chunk==512){result+=tempresult;tempresult="";chunk=0;}
  }//for every 8 characters,or 64 bits in the message

  //return the result as an array
  var retVal="";
  retVal=result+tempresult;
  return retVal;
}//end of des

//des_createKeys
//this takes as input a 64 bit key(even though only 56 bits are used)
//as an array of 2 integers,and returns 16 48 bit keys
function des_createKeys(key){
  //declaring this locally speeds things up a bit
  pc2bytes0=new Array(0,0x4,0x20000000,0x20000004,0x10000,0x10004,0x20010000,0x20010004,0x200,0x204,0x20000200,0x20000204,0x10200,0x10204,0x20010200,0x20010204);
  pc2bytes1=new Array(0,0x1,0x100000,0x100001,0x4000000,0x4000001,0x4100000,0x4100001,0x100,0x101,0x100100,0x100101,0x4000100,0x4000101,0x4100100,0x4100101);
  pc2bytes2=new Array(0,0x8,0x800,0x808,0x1000000,0x1000008,0x1000800,0x1000808,0,0x8,0x800,0x808,0x1000000,0x1000008,0x1000800,0x1000808);
  pc2bytes3=new Array(0,0x200000,0x8000000,0x8200000,0x2000,0x202000,0x8002000,0x8202000,0x20000,0x220000,0x8020000,0x8220000,0x22000,0x222000,0x8022000,0x8222000);
  pc2bytes4=new Array(0,0x40000,0x10,0x40010,0,0x40000,0x10,0x40010,0x1000,0x41000,0x1010,0x41010,0x1000,0x41000,0x1010,0x41010);
  pc2bytes5=new Array(0,0x400,0x20,0x420,0,0x400,0x20,0x420,0x2000000,0x2000400,0x2000020,0x2000420,0x2000000,0x2000400,0x2000020,0x2000420);
  pc2bytes6=new Array(0,0x10000000,0x80000,0x10080000,0x2,0x10000002,0x80002,0x10080002,0,0x10000000,0x80000,0x10080000,0x2,0x10000002,0x80002,0x10080002);
  pc2bytes7=new Array(0,0x10000,0x800,0x10800,0x20000000,0x20010000,0x20000800,0x20010800,0x20000,0x30000,0x20800,0x30800,0x20020000,0x20030000,0x20020800,0x20030800);
  pc2bytes8=new Array(0,0x40000,0,0x40000,0x2,0x40002,0x2,0x40002,0x2000000,0x2040000,0x2000000,0x2040000,0x2000002,0x2040002,0x2000002,0x2040002);
  pc2bytes9=new Array(0,0x10000000,0x8,0x10000008,0,0x10000000,0x8,0x10000008,0x400,0x10000400,0x408,0x10000408,0x400,0x10000400,0x408,0x10000408);
  pc2bytes10=new Array(0,0x20,0,0x20,0x100000,0x100020,0x100000,0x100020,0x2000,0x2020,0x2000,0x2020,0x102000,0x102020,0x102000,0x102020);
  pc2bytes11=new Array(0,0x1000000,0x200,0x1000200,0x200000,0x1200000,0x200200,0x1200200,0x4000000,0x5000000,0x4000200,0x5000200,0x4200000,0x5200000,0x4200200,0x5200200);
  pc2bytes12=new Array(0,0x1000,0x8000000,0x8001000,0x80000,0x81000,0x8080000,0x8081000,0x10,0x1010,0x8000010,0x8001010,0x80010,0x81010,0x8080010,0x8081010);
  pc2bytes13=new Array(0,0x4,0x100,0x104,0,0x4,0x100,0x104,0x1,0x5,0x101,0x105,0x1,0x5,0x101,0x105);

  //how many iterations(1 for des,3 for triple des)
  var iterations=key.length>=24?3 :1;
  //stores the return keys
  var keys=new Array(32 * iterations);
  //now define the left shifts which need to be done
  var shifts=new Array(0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,0);
  //other variables
  var lefttemp,righttemp,m=0,n=0,temp;

  for(var j=0;j<iterations;j++){//either 1 or 3 iterations
    var left=(key.charCodeAt(m++)<<24)|(key.charCodeAt(m++)<<16)|(key.charCodeAt(m++)<<8)|key.charCodeAt(m++);
    var right=(key.charCodeAt(m++)<<24)|(key.charCodeAt(m++)<<16)|(key.charCodeAt(m++)<<8)|key.charCodeAt(m++);

    temp=((left>>>4)^right)&0x0f0f0f0f;right^=temp;left^=(temp<<4);
    temp=((right>>>-16)^left)&0x0000ffff;left^=temp;right^=(temp<<-16);
    temp=((left>>>2)^right)&0x33333333;right^=temp;left^=(temp<<2);
    temp=((right>>>-16)^left)&0x0000ffff;left^=temp;right^=(temp<<-16);
    temp=((left>>>1)^right)&0x55555555;right^=temp;left^=(temp<<1);
    temp=((right>>>8)^left)&0x00ff00ff;left^=temp;right^=(temp<<8);
    temp=((left>>>1)^right)&0x55555555;right^=temp;left^=(temp<<1);

    //the right side needs to be shifted and to get the last four bits of the left side
    temp=(left<<8)|((right>>>20)&0x000000f0);
    //left needs to be put upside down
    left=(right<<24)|((right<<8)&0xff0000)|((right>>>8)&0xff00)|((right>>>24)&0xf0);
    right=temp;

    //now go through and perform these shifts on the left and right keys
    for(i=0;i<shifts.length;i++){
      //shift the keys either one or two bits to the left
      if(shifts[i]){left=(left<<2)|(left>>>26);right=(right<<2)|(right>>>26);}
      else{left=(left<<1)|(left>>>27);right=(right<<1)|(right>>>27);}
      left&=-0xf;right&=-0xf;

      //now apply PC-2,in such a way that E is easier when encrypting or decrypting
      //this conversion will look like PC-2 except only the last 6 bits of each byte are used
      //rather than 48 consecutive bits and the order of lines will be according to
      //how the S selection functions will be applied:S2,S4,S6,S8,S1,S3,S5,S7
      lefttemp=pc2bytes0[left>>>28]|pc2bytes1[(left>>>24)&0xf]
          |pc2bytes2[(left>>>20)&0xf]|pc2bytes3[(left>>>16)&0xf]
          |pc2bytes4[(left>>>12)&0xf]|pc2bytes5[(left>>>8)&0xf]
          |pc2bytes6[(left>>>4)&0xf];
      righttemp=pc2bytes7[right>>>28]|pc2bytes8[(right>>>24)&0xf]
          |pc2bytes9[(right>>>20)&0xf]|pc2bytes10[(right>>>16)&0xf]
          |pc2bytes11[(right>>>12)&0xf]|pc2bytes12[(right>>>8)&0xf]
          |pc2bytes13[(right>>>4)&0xf];
      temp=((righttemp>>>16)^lefttemp)&0x0000ffff;
      keys[n++]=lefttemp^temp;keys[n++]=righttemp^(temp<<16);
    }
  }//for each iterations
  //return the keys we've created
  return keys;
}//end of des_createKeys

var base64EncodeChars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64DecodeChars=new Array(
    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,
    52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,
    -1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,
    15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,
    -1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
    41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1);

function base64encode(str){
  var out,i,len;
  var c1,c2,c3;

  len=str.length;
  i=0;
  out="";
  while(i<len){
    c1=str.charCodeAt(i++)&0xff;
    if(i==len)
    {
      out+=base64EncodeChars.charAt(c1>>2);
      out+=base64EncodeChars.charAt((c1&0x3)<<4);
      out+="==";
      break;
    }
    c2=str.charCodeAt(i++);
    if(i==len)
    {
      out+=base64EncodeChars.charAt(c1>>2);
      out+=base64EncodeChars.charAt(((c1&0x3)<<4)|((c2&0xF0)>>4));
      out+=base64EncodeChars.charAt((c2&0xF)<<2);
      out+="=";
      break;
    }
    c3=str.charCodeAt(i++);
    out+=base64EncodeChars.charAt(c1>>2);
    out+=base64EncodeChars.charAt(((c1&0x3)<<4)|((c2&0xF0)>>4));
    out+=base64EncodeChars.charAt(((c2&0xF)<<2)|((c3&0xC0)>>6));
    out+=base64EncodeChars.charAt(c3&0x3F);
  }
  return out;
}

function base64decode(str){
  var c1,c2,c3,c4;
  var i,len,out;

  len=str.length;
  i=0;
  out="";
  while(i<len){
    /* c1 */
    do{
      c1=base64DecodeChars[str.charCodeAt(i++)&0xff];
    }while(i<len&&c1==-1);
    if(c1==-1)
      break;

    /* c2 */
    do{
      c2=base64DecodeChars[str.charCodeAt(i++)&0xff];
    }while(i<len&&c2==-1);
    if(c2==-1)
      break;

    out+=String.fromCharCode((c1<<2)|((c2&0x30)>>4));

    /* c3 */
    do{
      c3=str.charCodeAt(i++)&0xff;
      if(c3==61)
        return out;
      c3=base64DecodeChars[c3];
    }while(i<len&&c3==-1);
    if(c3==-1)
      break;

    out+=String.fromCharCode(((c2&0XF)<<4)|((c3&0x3C)>>2));

    /* c4 */
    do{
      c4=str.charCodeAt(i++)&0xff;
      if(c4==61)
        return out;
      c4=base64DecodeChars[c4];
    }while(i<len&&c4==-1);
    if(c4==-1)
      break;
    out+=String.fromCharCode(((c3&0x03)<<6)|c4);
  }
  return out;
}







