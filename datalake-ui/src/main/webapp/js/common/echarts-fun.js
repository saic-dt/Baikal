/**
 * Created by wangwenhu on 17/8/19.
 */
/*
* 折线图 无轴线
* id:id
* xData:x轴数据 ["","",""]
* vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
* clickFlag:是否存在点击事件 true:有
* clickFun:点击事件
* */
function lineChartNoBorderFun(id,xData,vData,clickFlag,clickFun){
    var colorMap=[
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(0, 135, 216, 1)'
        }, {
            offset: 0.8,
            color: 'rgba(0, 135, 216, 0)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(180, 224, 247, 0.8)'
        }, {
            offset: 0.8,
            color: 'rgba(180, 224, 247, 0.3)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(255, 255, 255, 0.5)'
        }, {
            offset: 0.8,
            color: 'rgba(255, 255, 255, 0)'
        }], false)
    ];
    var myChart = echarts.init(document.getElementById(id));
    var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
                name: this.name,
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 5,
                showSymbol: false,
                lineStyle: {
                    normal: {
                        width: 1
                        }
                    },
                    areaStyle: {
                        normal: {
                            color:colorMap[i],
                            shadowColor: 'rgba(255, 255, 255, 0.1)',
                            shadowBlur: 10
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: "#61bcfb",
                            // borderColor: 'rgba(2,167,236,0.5)',
                            borderWidth: 1
                        }
                    },
                    data: this.data
                };
        seriesData.push(seriesObj);
    });

    option={
        backgroundColor: '#daf3fe',
        title:{},
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#0b97e1'
                }
            },
            backgroundColor: "rgba(255,255,255,0.5)",
            borderRadius: 0,
            textStyle: {
                color: "#0094d1",
                fontSize: 14
            },
            formatter: function (obj) {
                var ret="",tit="";
                $.each(obj,function (i) {
                    tit=this.name;
                    ret+="<br/>"+this.seriesName+":"+this.value;
                });
                return tit+ret;
            }
        },
        grid: {
            left: '0',
            right: '0',
            bottom: '0',
            containLabel: true
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#8ec9ed'
                }
            },
            axisLabel:{
                inside: true
            },
            data: xData
        }],
        yAxis: [{
            type: 'value',
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#8ec9ed'
                }
            },
            axisLabel: {
                margin:20,
                inside: true
            },
            splitLine: {
                lineStyle: {
                    color: null
                }
            }
        }],
        // color:["#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb","#61bcfb"],
        series: seriesData
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}

/*
 * 折线图 无轴
 * id:id
 * xData:x轴数据 ["","",""]
 * vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function indexlineChartNoShaftFun(id,xData,vData,clickFlag,clickFun){
    var colorMap=[
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(0, 135, 216, 1)'
        }, {
            offset: 0.8,
            color: 'rgba(0, 135, 216, 0)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(180, 224, 247, 0.8)'
        }, {
            offset: 0.8,
            color: 'rgba(180, 224, 247, 0.3)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(255, 255, 255, 0.5)'
        }, {
            offset: 0.8,
            color: 'rgba(255, 255, 255, 0)'
        }], false)
    ];
    var myChart = echarts.init(document.getElementById(id));
    var colorNormal=["#bde1fc","#61bcfb","#b3d465","#13b5b1","#89cd84"];
    var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
            name: this.name,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
                normal: {
                    width: 1,
                    color: colorNormal[i]
                }
            },
            areaStyle: {
                normal: {
                    color:colorMap[i],
                    shadowColor: 'rgba(255, 255, 255, 0.1)',
                    shadowBlur: 10
                }
            },
            itemStyle: {
                normal: {
                    color: colorNormal[i],
                    borderColor: 'rgba(2,167,236,0.5)',
                    borderWidth: 0
                }
            },
            data: this.data
        };
        seriesData.push(seriesObj);
    });

    option={
        //backgroundColor: '#daf3fe',
        tooltip: {
            trigger: 'axis',
            bottom: 0,
            axisPointer: {
                type: 'line',
                lineStyle: {
                    color: '#0b97e1'
                }
            },
            backgroundColor: "rga(255,255,255,0.5)",
            borderRadius: 0,
            textStyle: {
                color: "#0094d1",
                fontSize: 14
            }/*,
            position:function(p){ //其中p为当前鼠标的位置
                return [p[0] + 10, p[1] + 20];
            }*/
        },
        grid: {
            top:0,
            left: 0,
            right: 0,
            bottom: 0
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#8ec9ed'
                }
            },
            axisLabel:{
                inside: true,
                show: false
            },
            data: xData
        }],
        yAxis: [{
            show: false
        }],
        series: seriesData
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}
/*
* 折线图 无轴
* id:id
* xData:x轴数据 ["","",""]
* vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
* clickFlag:是否存在点击事件 true:有
* clickFun:点击事件
* */
function lineChartNoShaftFun(id,xData,vData,clickFlag,clickFun,initFlag){
    var colorMap=[
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(0, 135, 216, 1)'
        }, {
            offset: 0.8,
            color: 'rgba(0, 135, 216, 0)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(180, 224, 247, 0.8)'
        }, {
            offset: 0.8,
            color: 'rgba(180, 224, 247, 0.3)'
        }], false),
        new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(255, 255, 255, 0.5)'
        }, {
            offset: 0.8,
            color: 'rgba(255, 255, 255, 0)'
        }], false)
    ];
    var myChart;
    if(echarts.getInstanceByDom(document.getElementById(id))==undefined || echarts.getInstanceByDom(document.getElementById(id))=="" || echarts.getInstanceByDom(document.getElementById(id))==null){
        myChart = echarts.init(document.getElementById(id));
    }else{
        myChart = echarts.getInstanceByDom(document.getElementById(id));
    }

    // if(initFlag){
    //     var myChart = echarts.init(document.getElementById(id));
    // }else{
    //     var myChart = echarts.getInstanceByDom(document.getElementById(id));
    // }
    var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
            name: this.name,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
                normal: {
                    width: 1,
                    color:colorMap[i]
                }
            },
            areaStyle: {
                normal: {
                    color:colorMap[i],
                    shadowColor: 'rgba(255, 255, 255, 0.1)',
                    shadowBlur: 10
                }
            },
            itemStyle: {
                normal: {
                    color: 'rgb(255,255,255,1)',
                    borderColor: 'rgba(2,167,236,0.5)',
                    borderWidth: 12
                }
            },
            data: this.data
        };
        seriesData.push(seriesObj);
    });

    option={
        //backgroundColor: '#daf3fe',
        tooltip: {
            trigger: 'axis',
            bottom: 0,
            axisPointer: {
                type: 'line',
                lineStyle: {
                    color: '#0b97e1'
                }
            },
            backgroundColor: "rga(255,255,255,0.5)",
            borderRadius: 0,
            textStyle: {
                color: "#0094d1",
                fontSize: 14
            },
            position:function(p){ //其中p为当前鼠标的位置
                return [p[0] + 10, p[1] - 10];
            },
            formatter: function (params) {
                //console.log(params);
                return params[0].name+" : "+params[0].value;
            }
        },
        grid: {
            top:0,
            left: 0,
            right: 0,
            bottom: 0
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#8ec9ed'
                }
            },
            axisLabel:{
                inside: true,
                show: false
            },
            data: xData
        }],
        yAxis: [{
            show: false
        }],
        animation:false,
        series: seriesData
    };
    myChart.clear();
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
    colorMap=[];
    seriesData=[];
}

/*
 * 折线图
 * id:id
 * xData:x轴数据 ["","",""]
 * vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function lineChartFun(id,xData,legendData,vData,clickFlag,clickFun){
    var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
            name: this.name,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
                normal: {
                    width: 1
                }
            },
            itemStyle: {
                normal: {
                    borderWidth: 0
                }
            },
            data: this.data
        };
        seriesData.push(seriesObj);
    });
    var myChart = echarts.init(document.getElementById(id));
    option = {
        backgroundColor: '',
        title: {
            text: '',
            textStyle: {
                fontWeight: 'normal',
                fontSize: 16,
                color: '#128de3'
            },
            left: '6%'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#57617B'
                }
            }
        },
        legend: {
            icon: 'circle', //圆形
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 13,//间距
            barBorderRadius:10,
            data: legendData,
            left: '4%',
            textStyle: {
                fontSize: 12,
                color: '#128de3'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#128de3'
                }
            },
            splitLine: {
                show:false
            },
            data: xData
        }],
        yAxis: [{
            type: 'value',
            name: '',
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#128de3'
                }
            },
            axisLabel: {
                margin: 10,
                textStyle: {
                    fontSize: 14
                }
            },
            splitLine: {
                show:false
            }
        }],
        color:["#1896ed","#055c9e","#b3d465","#13b5b1","#89cd84"],
        series: seriesData
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}

/*
 * 折线图
 * id:id
 * xData:x轴数据 ["","",""]
 * vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function doubleLineChartFun(id,xData,legendData,vData,clickFlag,clickFun){
    var seriesData=[];
    $.each(vData,function(i){
        var yAxisIndex=0;
        if(this.name=="total"){
            yAxisIndex=1;
        }
        var seriesObj={
            name: this.name,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            showSymbol: false,
            yAxisIndex:yAxisIndex,
            lineStyle: {
                normal: {
                    width: 1
                }
            },
            itemStyle: {
                normal: {
                    borderWidth: 0
                }
            },
            data: this.data
        };
        seriesData.push(seriesObj);
    });
    var myChart = echarts.init(document.getElementById(id));
    option = {
        backgroundColor: '',
        title: {
            text: '',
            textStyle: {
                fontWeight: 'normal',
                fontSize: 16,
                color: '#128de3'
            },
            left: '6%'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#57617B'
                }
            }
        },
        legend: {
            icon: 'circle', //圆形
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 13,//间距
            barBorderRadius:10,
            data: legendData,
            left: '4%',
            textStyle: {
                fontSize: 12,
                color: '#128de3'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#128de3'
                }
            },
            splitLine: {
                show:false
            },
            data: xData
        }],
        yAxis: [{
            type: 'value',
            name: 'lag',
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#128de3'
                }
            },
            axisLabel: {
                margin: 10,
                textStyle: {
                    fontSize: 14
                }
            },
            splitLine: {
                show:false
            }
        },{
            type: 'value',
            name: 'total',
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#128de3'
                }
            },
            axisLabel: {
                margin: 10,
                textStyle: {
                    fontSize: 14
                }
            },
            splitLine: {
                show:false
            }
        }],
        color:["#1896ed","#055c9e","#b3d465","#13b5b1","#89cd84"],
        series: seriesData
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}
/*
 * 数据资产管理 入库条数 折线图
 * id:id
 * cData:数据
 * dataObj:
 * chartKey:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function labelLineChartFun(id, cData, dataObj) {
    var myChart = echarts.init(document.getElementById(id), 'dark');
    var chartData = cData[dataObj];
    var titleVal;
    switch (dataObj) {
        case 'ip24Count':
            titleVal = 'E550-入库记录条数';
            break;
        case 'ip24VINCount':
            titleVal = 'E550-VIN号记录';
            break;
        case 'ep11Count':
            titleVal = 'E50-入库记录条数';
            break;
        case 'ep11VINCount':
            titleVal = 'E50-VIN号记录';
            break;
        case 'rx5Count':
            titleVal = 'rx5-入库记录条数';
            break;
        case 'rx5VINCount':
            titleVal = 'rx5-VIN号记录';
            break;
    }
    var seriesArr = [];
    for (var i = 0; i < chartData.length; i++) {
        var obj = {
            name:dataObj,
            type:'line',
            stack: '总量',
            areaStyle: {
                normal: {
                    color:'#6dc0ff'
                }
            },
            itemStyle : { //折点颜色
                normal : {
                    color:'#1bb7fd'
                }
            },
            lineStyle: { //折线颜色
                normal: {
                    width: 2,
                    color:'#6dc0ff'
                }
            },
            data:chartData[i].row_count
        };
        seriesArr.push(obj);
    }
    option = {
        title: {
            text: titleVal,
            left: 'center',
            textStyle: {
                color: '#1391e9',
                fontSize: 14
            }
        },
        tooltip:{
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#0b97e1'
                }
            },
            backgroundColor: "#0585dd",
            borderRadius: 5,
            textStyle: {
                color: "#fff",
                fontSize: 14
            },
            axisTick: {            // 坐标轴小标记
                show: true,       // 属性show控制显示与否，默认不显示
                interval: 'auto',
                inside : false,    // 控制小标记是否在grid里
                length :5,         // 属性length控制线长
                lineStyle: {       // 属性lineStyle控制线条样式
                    color: '#333',
                    width: 1
                }
            }
        },
        grid: {
            left: '1%',
            right: '1%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : true,//离x轴的距离
                data : chartData[0].xArr,
                axisTick: {
                    show: false
                },
                axisLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                }
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisTick: {
                    show: false
                },
                axisLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                },
                splitLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                }
            }
        ],
        series : seriesArr
    };
    myChart.setOption(option);
    $(window).resize(function(){
        myChart.resize();
    });
}
/*
 * 折线图
 * id:id
 * xData:x轴数据 ["","",""]
 * legendArr:数据
 * seriesArr:数据
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function labelLineTwoChartFun(id,xData,legendArr,seriesArr){
    var myChart = echarts.init(document.getElementById(id));
    /* 只有一个折线图时，样式定制 - zwj 4-10*/
    if (seriesArr.length <= 2) {
        for (var i = seriesArr.length - 1; i >= 0; i--) {
            seriesArr[i].areaStyle = {
                normal: {
                    opacity: 0.5
                }
            }
        }
    }
    option = {
        tooltip:{
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#0b97e1'
                }
            },
            backgroundColor: "#0585dd",
            borderRadius: 5,
            textStyle: {
                color: "#fff",
                fontSize: 14
            },
            axisTick: {            // 坐标轴小标记
                show: true,       // 属性show控制显示与否，默认不显示
                interval: 'auto',
                // onGap: null,
                inside : false,    // 控制小标记是否在grid里
                length :5,         // 属性length控制线长
                lineStyle: {       // 属性lineStyle控制线条样式
                    color: '#333',
                    width: 1
                }
            }
        },
        legend: {
            data:legendArr
        },
        grid: {
            left: '1%',
            right: '1%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : true,//离x轴的距离
                data : xData,
                axisTick: {
                    show: false
                },
                axisLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                },
                axisLabel:{
                    show:true
                }

            }
        ],
        yAxis : [
            {
                type : 'value',
                axisTick: {
                    show: false
                },
                axisLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                },
                splitLine: {
                    lineStyle: {
                        color: '#8ec9ed'
                    }
                }
            }
        ],
        series : seriesArr
    };
    myChart.setOption(option);
    $(window).resize(function(){
        myChart.resize();
    });
}

/*
* 热力图
* id:id
* xData:x轴数据 ["","",""]
* vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
* clickFlag:是否存在点击事件 true:有
* clickFun:点击事件
* */
function thermodynamicChartFun(id,xData,yData,vData,clickFlag,clickFun){
    var seriesData =[];
    $.each(vData,function(i){
        seriesData.push([this[1], this[0], this[2] || '-']);
    });

    var myChart = echarts.init(document.getElementById(id));
    option = {
        color: [
            "#0c80d0",
            "#bde3ff",
            "#e9f8ff",
            "#ffffff"
        ],
        tooltip: {
            position: 10, //悬浮框离上边的距离 top
            formatter: function (p) {
                return (p.data[1]+1)+"月"+(p.data[0]+1)+"日:"+p.data[2];
            }
        },
        animation: false,
        grid: {
            height: '80%',
            y: '10%',
            show:true
        },
        xAxis: {
            type: 'category',
            data: xData,
            axisTick:{
                show:false
            },
            axisLine: {
                show: true,
                lineStyle: {
                    color: "#70bef8"
                }
            }
        },
        yAxis: [{
            type: 'category',
            data: yData,
            axisTick:{
                show:false
            },
            axisLine: {
                show: true,
                lineStyle: {
                    color: "#70bef8"
                }
            }
        }],
        visualMap: {
            show: false,
            min: 1,
            max: 10,
            calculable: false,
            orient: 'horizontal',
            left: 'center',
            target:{
                inRange:{
                    color: [
                        "#ffffff",
                        "#ecf8ff",
                        "#bde3ff",
                        "#0c80d0"
                    ]
                }
            }
        },
        series: [{
            name: 'Card',
            type: 'heatmap',
            data: vData,
            label: {
                normal: {
                    show: false
                }
            },
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 129, 206, 0.5)'
                }
            }
        }]
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });

    seriesData =[];
}

/*
 * 饼图
 * id:id
 * titleName:标题
 * legendData:legend数据 ["","",""]
 * vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function pieChartFun(id,titleName,legendData,vData,clickFlag,clickFun){
    /*var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
            name:this.name,
            value:this.data
        };
        seriesData.push(seriesObj);
    });*/
    var myChart = echarts.init(document.getElementById(id));
    option2 = {
        title : {
            text: '',
            subtext: '',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            icon: 'circle', //圆形
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 13,//间距
            barBorderRadius:10,
            data: legendData,
            left: '4%',
            textStyle: {
                fontSize: 12,
                color: '#128de3'
            }
        },
        color:["#4ac6fe","#055c9e","#13b5b1","#cfc884","#89cd84"],
        series : [
            {
                name: titleName,
                type: 'pie',
                label:{
                    normal:{
                        show:false //控制饼图引导线的隐藏
                    },
                    emphasis:{
                        show :false //控制鼠标悬浮时的显隐
                    }
                },
                radius : '70%',
                center: ['50%', '60%'],
                animation:false,
                data:vData
            }
        ]
    };
    myChart.setOption(option2);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}
/*
 * 比例图
 * id:id
 * xData:x轴数据 ["","",""]
 * vData:数据 ［{"name":""trade/hdx"","data":[120, 132, 101, 134, 90, 230, 210]}］
 * clickFlag:是否存在点击事件 true:有
 * clickFun:点击事件
 * */
function proportionChartFun(id,legendData,vData,clickFlag,clickFun){
    var centerLfet1=["30.0%","55%","80.0%"];
    var centerLfet2=["35.0%","70%"];
    var centerLfet3=["50.0%"];
    var centerLfet=[];
    var leg=vData.length;
    if(leg==1){
        centerLfet=centerLfet3;
    }else if(leg==2){
        centerLfet=centerLfet2;
    }else if(leg==3){
        centerLfet=centerLfet1;
    }
    var seriesData=[];
    $.each(vData,function(i){
        var seriesObj={
            name: this.name,
            center: [
                centerLfet[i],
                '40%'
            ],
            radius: [
                '40%',
                '50%'
            ],
            type: 'pie',
            labelLine: {
                normal: {
                    show: false
                }
            },
            data: [{
                value: this.data[0].value,
                name: this.data[0].name,

                itemStyle: {
                    normal: {
                        color: '#1896ed'
                    },
                    emphasis: {
                        color: '#1896ed'
                    }
                },
                label: {
                    normal: {
                        formatter: '{d} %',
                        position: 'center',
                        show: true,
                        textStyle: {
                            fontSize: '14',
                            color: '#000'
                        }
                    }
                },
                hoverAnimation: false

            }, {
                value: this.sum,
                name: '',
                tooltip: {
                    show: false
                },
                itemStyle: {
                    normal: {
                        color: '#cdebfd'
                    },
                    emphasis: {
                        color: '#cdebfd'
                    }
                },
                hoverAnimation: false
            }]
        };
        seriesData.push(seriesObj);
    });
    var myChart = echarts.init(document.getElementById(id));
    option= {
        tooltip: {
            trigger: 'item',
            formatter: function(params, ticket, callback) {
                var res = params.seriesName;
                res += '<br/>' + params.name + ' : ' + params.percent + '%';
                return res;
            }
        },
        grid: {
            bottom:140,
            left:50
        },
        xAxis: [{
            type: 'category',
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                show:true,
                interval: 0,
                formatter:function(params){
                    var newParamsName = "";// 最终拼接成的字符串
                    var paramsNameNumber = params.length;// 实际标签的个数
                    var provideNumber = 10;// 每行能显示的字的个数
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);// 换行的话，需要显示几行，向上取整

                    if (paramsNameNumber > provideNumber) {
                        /** 循环每一行,p表示行 */
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";// 表示每一次截取的字符串
                            var start = p * provideNumber;// 开始截取的位置
                            var end = start + provideNumber;// 结束截取的位置
                            // 此处特殊处理最后一行的索引值
                            if (p == rowNumber - 1) {
// 最后一次不换行
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                // 每一次拼接字符串并换行
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;// 最终拼成的字符串
                        }

                    } else {

                        newParamsName = params;
                    }

                    return newParamsName

                }
            },
            data: legendData
        }],
        yAxis: [{
            show: false
        }],
        series: seriesData
    };
    myChart.setOption(option);
    if(clickFlag){
        myChart.on('click', function(params) {
            clickFun(params);
        });
    }
    $(window).resize(function(){
        myChart.resize();
    });
}

/* 日历类型
* id:id
* cData:
* dataObj:
*
* */
function dateChartFun(id, chartData, dataObj,year) {
    var myChart = echarts.init(document.getElementById(id), 'dark');
    var chartData = chartData[dataObj];
    var titleVal;
    switch (dataObj) {
        case 'ip24Count':
            titleVal = 'E550';
            break;
        case 'ip24VINCount':
            titleVal = 'E550';
            break;
        case 'ep11Count':
            titleVal = 'E50';
            break;
        case 'ep11VINCount':
            titleVal = 'E50';
            break;
        case 'rx5Count':
            titleVal = 'rx5';
            break;
        case 'rx5VINCount':
            titleVal = 'rx5';
            break;
    }
    function getVirtulData() {
        var date = +echarts.number.parseDate(year + '-01-01');
        var end = +echarts.number.parseDate((+year + 1) + '-01-01');
        var dayTime = 3600 * 24 * 1000;
        var data = [];
        for (var time = date; time < end; time += dayTime) {
            data.push([
                echarts.format.formatTime('yyyy-MM-dd', time),
                Math.floor(Math.random() * 10000)
            ]);
        }
        return data;
    }
    var heatmapDate = [];
    var xArr = chartData[0].xArr; //日期
    var row_count = chartData[0].row_count; //数值
    var calendarDate = xArr[xArr.length - 1]; //日期中最后一个值2017-03-13
    var visuaMax = Math.max.apply(null, row_count);
    var flag = 0;
    for (var i = 0; i < xArr.length; i++) {
        heatmapDate[i] = [];
        heatmapDate[i].push(xArr[i], row_count[i]);
        if (row_count[i] == 0) {
            flag++;
        }
    }
    $("#"+id+"H").html(titleVal+$.convertToChinese(new Number(calendarDate.substr(5, 2)))+"月");
    var cellSize = [30, 30];
    var scatterData = getVirtulData();
    optionD1 = {

        tooltip: {
            trigger: 'item',
            formatter: function(params) {
                var d = params.data[1];
                return d;
            }
        },
        calendar: {
            bottom:"0",
            left: '0%',
            right: '0%',
            top: '15%',
            show:true,
            orient: 'vertical',
            cellSize: cellSize,
            yearLabel: {
                show: false,
                margin: 45,
                textStyle: {
                    fontSize: 20,
                    color: '#999'
                }
            },
            dayLabel: {
                color: '#0993f2',
                margin: 10,
                firstDay: 1,
                fontSize:12,
                //top:"5px",
                nameMap: ['日', '一', '二', '三', '四', '五', '六']
            },
            monthLabel: {
                nameMap: 'cn',
                margin: 15,
                textStyle: {
                    fontSize: 14,
                    color: '#999'
                }
            },
            splitLine: {
                show: true,
                lineStyle: {
                    color: '#189af3'
                }
            },
            itemStyle: {
                normal: {
                    color: '#ddf2ff',
                    borderWidth: 1,
                    borderColor: '#6dc0ff'
                }
            },
            range: [calendarDate.substr(0, 7)]
        },visualMap: {
            // show=false 关闭visualMap组件
            show: false,
            min: 0,
            max: visuaMax * 2.5,
            left: 'center',
            bottom: 20,
            inRange: {
                color: ['#fff', '#7ebfff'],
                opacity: 1
            },
            orient: 'horizontal'
        },
        series: [{
            id: 'label',
            type: 'scatter',
            coordinateSystem: 'calendar',
            symbolSize: 0.5,
            label: {
                normal: {
                    show: true,
                    formatter: function (params) {
                        return echarts.format.formatTime('dd', params.value[0]);
                    },
                    //offset: [-cellSize[0] / 2+10, -cellSize[1] / 2 + 10],
                    textStyle: {
                        color: '#0993f2',
                        fontSize: 14
                    }
                }
            },
            data: scatterData
        },{
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: heatmapDate
        }]
    };
    if (flag == heatmapDate.length) {
        optionD1.visualMap.inRange.color = ['#ddf2ff', '#ddf2ff'];
    }
    if (optionD1 && typeof optionD1 === "object") {
        myChart.setOption(optionD1, true);
    }
    $(window).resize(function(){
        myChart.resize();
    });
}