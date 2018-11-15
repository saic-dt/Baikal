(function(root, factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['exports', 'echarts'], factory);
    } else if (typeof exports === 'object' && typeof exports.nodeName !== 'string') {
        // CommonJS
        factory(exports, require('echarts'));
    } else {
        // Browser globals
        factory({}, root.echarts);
    }
}(this, function(exports, echarts) {
    var log = function(msg) {
        if (typeof console !== 'undefined') {
            console && console.error && console.error(msg);
        }
    };
    if (!echarts) {
        log('ECharts is not Loaded');
        return;
    }
    var contrastColor = '#29323c';
    var axisCommon = function() {
        return {
            axisLine: {
                lineStyle: {
                    color: contrastColor
                }
            },
            axisTick: {
                lineStyle: {
                    color: '#d0dde9'
                }
            },
            axisLabel: {
                textStyle: {
                    color: contrastColor
                }
            },
            splitLine: {
                lineStyle: {
                    type: 'solid',
                    color: '#d0dde9'
                }
            },
            splitArea: {
                areaStyle: {
                    color: 'contrastColor'
                }
            }
        };
    };

    var colorPalette = ['#7cb5ec', '#2c3742', '#f1db57', '#71e2db', '#ea7e53', '#eec178', '#73a373', '#73b9bc', '#70d2e6', '#91ca8c', '#f49f42'];
    var theme = {
        color: colorPalette,
        backgroundColor: '#fff',
        tooltip: {
            axisPointer: {
                lineStyle: {
                    color: contrastColor
                },
                crossStyle: {
                    color: contrastColor
                }
            }
        },
        legend: {
            textStyle: {
                color: contrastColor
            }
        },
        // categoryAxis = 坐标轴
        categoryAxis: {
            //轴线
            axisLine: {
                show: true,
                lineStyle: {
                    color: "#666"
                }
            },
            //刻度
            axisTick: {
                show: false,
                lineStyle: {
                    color: "#666"
                }
            },

            //文字
            axisLabel: {
                show: true,
                textStyle: {
                    color: "#666"
                }
            },
            //区域中间分割线
            splitLine: {
                show: true,
                lineStyle: {
                    color: [
                        "#4639d9"
                    ]
                }
            },
            //网格
            splitArea: {
                show: false,
                areaStyle: {
                    color: [
                        "#fff"
                    ]
                }
            }
        },
        title: {
            textStyle: {
                color: "#666"
            },
            subtextStyle: {
                color: "#666"
            },
            left: 'center'
        },
        toolbox: {
            iconStyle: {
                normal: {
                    borderColor: contrastColor
                }
            }
        },
        dataZoom: {
            textStyle: {
                color: contrastColor
            }
        },
        timeline: {
            lineStyle: {
                color: contrastColor
            },
            itemStyle: {
                normal: {
                    color: colorPalette[1]
                }
            },
            label: {
                normal: {
                    textStyle: {
                        color: contrastColor
                    }
                }
            },
            controlStyle: {
                normal: {
                    color: contrastColor,
                    borderColor: contrastColor
                }
            }
        },
        timeAxis: axisCommon(),
        logAxis: axisCommon(),
        valueAxis: axisCommon(),
        line: {
            symbol: 'circle',
        },
        graph: {
            color: colorPalette
        },
        gauge: {
            title: {
                textStyle: {
                    color: contrastColor
                }
            }
        },
        /* 折线图设置 */
        line: {
            markPoint: {
                label: {
                    normal: {
                        textStyle: {
                            color: "#666",
                            fontWeight: "bold",
                        }
                    }
                },
            }
        },
        bar: {
            markPoint: {
                label: {
                    normal: {
                        textStyle: {
                            color: "#666",
                            fontWeight: "bold"
                        }
                    }
                }
            }
        },
        candlestick: {
            itemStyle: {
                normal: {
                    color: '#FD1050',
                    color0: '#0CF49B',
                    borderColor: '#FD1050',
                    borderColor0: '#0CF49B'
                }
            }
        }
    };
    theme.categoryAxis.splitLine.show = false;
    echarts.registerTheme('dark', theme);
}));