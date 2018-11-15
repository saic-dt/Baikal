/* 
   page1 = 第1个页面
   tab2 = 第2个选项卡的内容
   ct2 = 第2个图表(从上到下，从左到右)
   page1_tab2_ct2 =  第1个页面 第2个选项卡，第2个图表
*/

/* 请求 DataBases页tab1所需要的数据 */
function ajaxDataBases_tab1(startDate, endDate) {
    $('h2 .icon-spinner').show();
    $.ajax({
            url: baseUrl + '/Tab1/newEnergyData',
            type: 'GET',
            dataType: 'json',
            data: {
                startDate: startDate,
                endDate: endDate
            }
        })
        .done(function(chartData) {
            SaveChartDataPage1.chart_tab1 = chartData;
            /* 构造page1 tab1 页面里12个图表 */
            structureCharts_12(chartData);
        })
        .fail(function() {
            alert("系统异常");
        })
        .always(function() {
            $('h2 .icon-spinner').hide();
        })

}

/* 构造page1 tab1 页面里12个图表 */
function structureCharts_12(chartData) {
    var arrKeyName = []
    for (x in chartData) {
        arrKeyName.push(x);
    }
    samllCharts('page1_tab1_ct1', chartData, 'ip24Count');
    samllCharts('page1_tab1_ct3', chartData, 'ep11Count');
    samllCharts('page1_tab1_ct5', chartData, 'rx5Count');
    samllCharts('page1_tab1_ct7', chartData, 'ip24VINCount');
    samllCharts('page1_tab1_ct9', chartData, 'ep11VINCount');
    samllCharts('page1_tab1_ct11', chartData, 'rx5VINCount');
    initCalendar('page1_tab1_ct2', chartData, 'ip24Count');
    initCalendar('page1_tab1_ct4', chartData, 'ep11Count');
    initCalendar('page1_tab1_ct6', chartData, 'rx5Count');
    initCalendar('page1_tab1_ct8', chartData, 'ip24VINCount');
    initCalendar('page1_tab1_ct10', chartData, 'ep11VINCount');
    initCalendar('page1_tab1_ct12', chartData, 'rx5VINCount');
}

/* ... */
function initEcharts(chartDom, history_para_1, history_para_2) {
    var myChart = echarts.init(document.getElementById(chartDom), 'dark');
    var title = "";
    var xArr = [];
    var yDataArr = [];
    var legendArr = [];
    var seriesArr = [];
    var yAixsUnit = "";
    var xAxisUnit = "";
    var xAxisName = "";
    var yAxisName = "";
    var subTitle = "";
    myChart.clear();

    var list = page1_tab2_ct2.data;
    title = list.title;
    xArr = list.xArr;
    legendArr = list.legendArr;
    seriesArr = list.seriesArr;
    if (list.maxValue) {
        chartCont.append('<div class="maxValue">当天最大交易量 : ' + list.maxValue + '</div>')
    }
    for (var i = 0; i < seriesArr.length; i++) {
        seriesArr[i].stack = '总量';
        seriesArr[i].smooth = true;
    }
    yAixsUnit = list.yAxisUnit;
    yAxisName = list.yAxisName;
    xAxisUnit = list.xAxisUnit;
    xAxisName = list.xAxisName;
    subTitle = list.subTitle;
    option = {
        // animation: false,
        title: {
            top: 30,
            text: title,
            subtext: subTitle
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: "shadow"
            }
        },
        legend: {
            data: legendArr,
            x: 'left'
        },
        toolbox: {
            show: true,
            feature: {
                mark: {
                    show: true
                },
                dataView: {
                    show: false,
                    readOnly: true
                },
                magicType: {
                    show: false,
                    type: ['line', 'bar']
                },
                restore: {
                    show: true
                },
                saveAsImage: {
                    show: true,
                    title: '保存为图片',
                    type: 'png',
                    lang: ['点击保存']
                }
            }
        },
        grid: {
            left: '2%',
            right: '7%',
            top: 100,
            bottom: 55,
            containLabel: true,
            textStyle: {
                color: "#fff"
            }
        },
        xAxis: [{
            nameLocation: 'end',
            nameGap: 10,
            boundaryGap: true,
            data: xArr,
            name: xAxisName
        }],
        yAxis: [{
            type: 'value',
            name: yAxisName
        }],
        series: seriesArr
    };
    /* 只有一个折线图时，样式定制 - zwj 4-10*/
    if (seriesArr[0].type == 'line') {
        option.yAxis[0].splitLine = {
            lineStyle: {
                color: ['#ccc'],
                type: 'solid'
            }
        };
        seriesArr[0].areaStyle = {
            normal: {}
        }
        seriesArr[1].areaStyle = {
            normal: {}
        }
    }
    myChart.setOption(option);
    myChart.hideLoading();
};

/* page1 - tab2 || page2 -tab1 ||  page3 -tab1 */
function initEchartsPage1(chartDom, tab2_chartOption) {
    var myChart = echarts.init(document.getElementById(chartDom), 'dark');
    myChart.clear();
    option = {
        animation: false,
        title: {
            top: 30,
            text: tab2_chartOption.title,
            textStyle: {
                color: '#666',
                fontSize: 14
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: "shadow"
            }
        },
        legend: {
            data: tab2_chartOption.legendArr,
            x: 'left'
        },
        grid: {
            left: '2%',
            right: '2%',
            top: 80,
            bottom: 25,
            containLabel: true,
            textStyle: {
                color: "#fff"
            }
        },
        xAxis: [{
            nameLocation: 'end',
            nameGap: 10,
            boundaryGap: true,
            data: tab2_chartOption.xArr,
            axisLabel: {
                rotate: 30
            }
        }],
        yAxis: [{
            type: 'value',
            /*       name: yAxisName*/
        }],
        series: tab2_chartOption.seriesArr
    };
    /* 只有一个折线图时，样式定制 - zwj 4-10*/
    if (tab2_chartOption.seriesArr.length <= 2) {
        for (var i = tab2_chartOption.seriesArr.length - 1; i >= 0; i--) {
            tab2_chartOption.seriesArr[i].areaStyle = {
                normal: {
                    opacity: 0.5
                }
            }
        }
    }
    myChart.setOption(option);
};

/* page1 - tab2 - examples */
function samllCharts(chartDom, cData, chartKey) {
    var myChart = echarts.init(document.getElementById(chartDom), 'dark');
    var chartData = cData[chartKey];
    var titleVal;
    switch (chartKey) {
        case 'ip24Count':
            titleVal = 'ip24-入库记录条数';
            break;
        case 'ip24VINCount':
            titleVal = 'ip24-VIN号记录';
            break;
        case 'ep11Count':
            titleVal = 'ep11-入库记录条数';
            break;
        case 'ep11VINCount':
            titleVal = 'ep11-VIN号记录';
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
            name: chartKey,
            type: "line",
            itemStyle: {
                normal: {
                    color: ""
                }
            },
            areaStyle: {
                normal: {
                    opacity: 0.3
                }
            },
            data: chartData[i].row_count
        }
        seriesArr.push(obj);
    }
    option = {
        animation: false,
        title: {
            top: 20,
            text: titleVal,
            textStyle: {
                color: '#666',
                fontSize: 14
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: "shadow"
            }
        },
        legend: {
            show: false
        },
        grid: {
            left: '2%',
            right: 20,
            top: 80,
            bottom: 15,
            containLabel: true,
            textStyle: {
                color: "#fff"
            }
        },
        xAxis: [{
            nameLocation: 'end',
            nameGap: 10,
            boundaryGap: true,
            data: chartData[0].xArr
        }],
        yAxis: [{
            type: 'value',
            /*       name: yAxisName*/
        }],
        series: seriesArr
    };
    myChart.setOption(option);
};

/* 日历类型 */
function initCalendar(chartDom, chartData, chartKey) {
    var myChart = echarts.init(document.getElementById(chartDom), 'dark');
    var chartData = chartData[chartKey];
    var titleVal;
    switch (chartKey) {
        case 'ip24Count':
            titleVal = 'ip24';
            break;
        case 'ip24VINCount':
            titleVal = 'ip24';
            break;
        case 'ep11Count':
            titleVal = 'ep11';
            break;
        case 'ep11VINCount':
            titleVal = 'ep11';
            break;
        case 'rx5Count':
            titleVal = 'rx5';
            break;
        case 'rx5VINCount':
            titleVal = 'rx5';
            break;
    }

    function getVirtulData(year) {
        year = year || '2017';
        var date = +echarts.number.parseDate(year + '-01-01');
        var end = +echarts.number.parseDate((+year + 1) + '-01-01');
        var dayTime = 3600 * 24 * 1000;
        var data = [];
        for (var time = date; time < end; time += dayTime) {
            data.push([
                echarts.format.formatTime('yyyy-MM-dd', time),
                '1'
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
    option = {
        title: {
            top: 110,
            left: 12,
            text: titleVal,
            textStyle: {
                color: '#999',
                fontWeight: 'normal',
                fontSize: 14,
            },
        },
        tooltip: {
            trigger: 'item',
            formatter: function(params) {
                var d = params.data[1];
                return d;
            }
        },
        calendar: {
            bottom: 20,
            left: '20%',
            orient: 'vertical',
            type: 'piecewise',
            cellSize: 'auto',
            yearLabel: {
                show: false,
                margin: 45,
                textStyle: {
                    fontSize: 20,
                    color: '#999'
                }
            },
            dayLabel: {
                firstDay: 1,
                nameMap: 'cn',
                textStyle: {
                    color: '#666'
                }
            },
            monthLabel: {
                nameMap: 'cn',
                margin: 15,
                textStyle: {
                    fontSize: 14,
                    color: '#999'
                }
            },
            //分割线样式
            splitLine: {
                show: false
            },
            itemStyle: {
                normal: {
                    color: '#fafafa'
                }
            },
            range: [calendarDate.substr(0, 7)]
        },
        visualMap: {
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
            type: 'scatter',
            symbolSize: 0.5,
            hoverAnimation: false,
            legendHoverLink: false,
            visualMap: false,
            itemStyle: {
                normal: {
                    color: 'red',
                    opacity: 0
                }
            },
            label: {
                normal: {
                    show: true,
                    formatter: function(params) {
                        var d = echarts.number.parseDate(params.value[0]);
                        return d.getDate();
                    },
                    textStyle: {
                        color: '#333'
                    }
                }
            },
            coordinateSystem: 'calendar',
            data: getVirtulData(2017)
        }, {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: heatmapDate
        }]
    };
    if (flag == heatmapDate.length) {
        option.visualMap.inRange.color = ['#fff', '#fff'];
    }
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

}