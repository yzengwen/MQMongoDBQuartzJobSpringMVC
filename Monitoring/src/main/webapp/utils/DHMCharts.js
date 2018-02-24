sap.ui.define(function() {
    "use strict";
    var DHMCharts = {
        controller: null,
        timeMode: {
            Hour: 1,
            Day: 2,
            Week: 3,
            Month: 4
        },
        init: function(oController) {
            this.controller = oController;
            this.xTitle = 'Time Flow(hour)';
        },
        getXLabels: function(firstTimestamp, mode) {
            var ts = Number(firstTimestamp);

            var ret = [];
            var tenMin = 600000;
            if (mode == this.timeMode.Hour) {

                var x = ts; // - ts%tenMin + tenMin;
                for (var i = 0; i < 6; i++) {
                    var hour = moment(x).startOf('hour').hours();
                    var min = moment(x).startOf('minute').minutes();
                    var label = hour + ":" + min;
                    ret.push(label);
                    x = x + tenMin;
                }
            } else if (mode == this.timeMode.Day) {
                var x = ts; // - ts%hour + hour;
                for (var i = 0; i < 24 * 6; i++) {
                    var label = moment(x).startOf('hour').hours();
                    ret.push(label);
                    x = x + tenMin;
                }
            } else if (mode == this.timeMode.Week) {
                var x = ts; // - ts%hour + hour;
                for (var i = 0; i < 7 * 24 * 6; i++) {
                    var label = moment(x).format('MMM D');
                    ret.push(label);
                    x = x + tenMin;
                }
            } else if (mode == this.timeMode.Month) {
                var x = ts; // - ts%hour + hour;
                for (var i = 0; i < 30 * 24 * 6; i++) {
                    var label = moment(x).format('MMM D');
                    ret.push(label);
                    x = x + tenMin;
                }
            }
            return ret;
        },
        formatDateTime: function(timestamp, timemode) {
            if (timemode == this.timeMode.Hour) {
                var hour = moment(timestamp).startOf('hour').hours();
                var min = moment(timestamp).startOf('minute').minutes();
                var label = hour + ":" + min;
                return label;
            }
        },
        getDivisor: function(timemode) {
            if (timemode == this.timeMode.Hour) {
                return 5;
            }
            if (timemode == this.timeMode.Day) {
                return 24;
            }
            if (timemode == this.timeMode.Week) {
                return 14;
            }
            if (timemode == this.timeMode.Month) {
                return 30;
            }
        },
        getAVGData: function(timemode) {
            var that = this;
            var axisXSetting = {
                divisor: this.getDivisor(timemode),
                labelInterpolationFnc: function(value, index) {
                    if (timemode == that.timeMode.Hour) {
                        return value;
                    }
                    if (timemode == that.timeMode.Day) {
                        return index % 6 === 0 ? value : null;
                    }
                    if (timemode == that.timeMode.Week) {
                        return index % 72 === 0 ? value : null;
                    }
                    if (timemode == that.timeMode.Month) {
                        return index % 144 === 0 ? value : null;
                    }
                    return value;
                }
            };
            this.controller.service.get("/statistic/AVG" + "?timemode=" + timemode, this.controller, function(arr) {
                arr.sort(function(a, b) {
                    if (a.statisticalPoint < b.statisticalPoint)
                        return -1;
                    else if (a.statisticalPoint > b.statisticalPoint)
                        return 1;
                    else
                        return 0;
                });
                var firstPoint = arr[0];
                var arrY = arr.map(function(obj) {
                    return obj.averageProcessingTime
                });
                var data = {
                    labels: that.getXLabels(firstPoint.statisticalPoint, timemode),
                    series: [arrY]
                };
                var chart = that.createLineChart('.chartAVG', axisXSetting, that.xTitle, 'Avg Processing Time(s)', data);
                //chart.update();

            });
        },
        getStatusData: function() {
            var that = this;
            var xTitle = "Status";
            this.controller.service.get("/getIdocStatusNum", this.controller, function(arr) {
                var arrY = arr.map(function(obj) {
                    return obj.averageProcessingTime
                });
                var data = {
                    labels: arr.map(function(obj) {
                        return obj.status
                    }),
                    series: [arr.map(function(obj) {
                        return obj.itemNum
                    })]
                };

                new Chartist.Bar(
                	'.chartStat',
                	data, 
                	{
                		fullWidth: true,
                		chartPadding: {
	                        left: 60,
	                        right: 80,
	                        bottom: 15
                		},
	                    scaleMinSpace: 1,
	                    axisY: {
	                        type: Chartist.AutoScaleAxis,
	                        onlyInteger: true
	                    },
	                    plugins: [
	                        Chartist.plugins.ctAxisTitle({
	                            axisX: {
	                                axisTitle: xTitle,
	                                axisClass: 'ct-axis-title',
	                                offset: {
	                                    x: 0,
	                                    y: 40
	                                },
	                                textAnchor: 'middle'
	                            },
	                            axisY: {
	                                axisTitle: 'Number of Idocs',
	                                axisClass: 'ct-axis-title',
	                                offset: {
	                                    x: 0,
	                                    y: -5
	                                },
	                                textAnchor: 'middle',
	                                flipTitle: false
	                            }
	                        })
	                    ]
                	}, 
	                {
	                    //distributeSeries: true
	                }
                	).on('draw', function(elementType) {
                    if (elementType.type === 'bar') {
                        var xLabel = data.labels[elementType.index];
                        if (xLabel == "SUCCESS") {
                            elementType.element.attr({ //green
                                style: 'stroke-width: 30px;stroke:#2b7d2b'
                            });
                        } else if (xLabel == "PENDING_PUBLICATION") {
                            elementType.element.attr({ //yellow
                                style: 'stroke-width: 30px;stroke:#e78c07'
                            });
                        } else if (xLabel == "PARTIAL_ERROR") {
                            elementType.element.attr({ //pink
                                style: 'stroke-width: 30px;stroke:#f05b4f'
                            });
                        } else if (xLabel == "COMPLETE_FAILURE") {
                            elementType.element.attr({ //red
                                style: 'stroke-width: 30px;stroke:#bb0000'
                            });
                        } else if (xLabel == "SUPERCEDED") {
                            elementType.element.attr({ //blue
                                style: 'stroke-width: 30px;stroke:#0544d3'
                            });
                        }
                    }
                });
            });
        },
        getProcessData: function(timemode) {
            var that = this;
            var axisXSetting = {
                divisor: this.getDivisor(timemode),
                labelInterpolationFnc: function(value, index) {
                    if (timemode == that.timeMode.Hour) {
                        return value;
                    }
                    if (timemode == that.timeMode.Day) {
                        return index % 6 === 0 ? value : null;
                    }
                    if (timemode == that.timeMode.Week) {
                        return index % 72 === 0 ? value : null;
                    }
                    if (timemode == that.timeMode.Month) {
                        return index % 144 === 0 ? value : null;
                    }
                    return value;
                }
            };
            this.controller.service.get("/statistic/AVG" + "?timemode=" + timemode, this.controller, function(arr) {
                arr.sort(function(a, b) {
                    if (a.statisticalPoint < b.statisticalPoint)
                        return -1;
                    else if (a.statisticalPoint > b.statisticalPoint)
                        return 1;
                    else
                        return 0;
                });
                var firstPoint = arr[0];
                var arrY = arr.map(function(obj) {
                    return obj.inProcessAmount
                });
                var data = {
                    labels: that.getXLabels(firstPoint.statisticalPoint, timemode),
                    series: [arrY]
                };
                var chart = that.createLineChart('.chartInProcess', axisXSetting, that.xTitle, 'Number of Idocs in processing', data);
            });
        },
        createLineChart: function(chartName, axisXSetting, axisTitleX, axisTitleY, data) {
            var chart = new Chartist.Line(
            		chartName, //'.ct-chart', 
            		data, 
                    {
	                    fullWidth: true,
	                    chartPadding: {
	                        left: 60,
	                        right: 80,
	                        bottom: 18,
	                    },
	                    axisY: {
	                        onlyInteger: true
	                    },
	                    axisX: axisXSetting,
	                    lineSmooth: Chartist.Interpolation.simple(),
	                    plugins: [
	                        Chartist.plugins.ctAxisTitle({
	                            axisX: {
	                                axisTitle: axisTitleX,
	                                axisClass: 'ct-axis-title',
	                                offset: {
	                                    x: 0,
	                                    y: 40
	                                },
	                                textAnchor: 'middle'
	                            },
	                            axisY: {
	                                axisTitle: axisTitleY,
	                                axisClass: 'ct-axis-title',
	                                offset: {
	                                    x: 0,
	                                    y: -5
	                                },
	                                textAnchor: 'middle',
	                                flipTitle: false
	                            }
	                        })
	                    ]
                    }
            	);
            return chart;
        },
        onTimeRangeSelectChange: function(item) {
            var timemode = item.mParameters.selectedItem.getKey();
            if (timemode == this.timeMode.Hour) {
                this.xTitle = 'Time Flow(hour)';
            }
            if (timemode == this.timeMode.Day) {
                this.xTitle = 'Time Flow(day)';
            }
            if (timemode == this.timeMode.Week) {
                this.xTitle = 'Time Flow(week)';
            }
            if (timemode == this.timeMode.Month) {
                this.xTitle = 'Time Flow(month)';
            }
            var selectControlName = item.getSource().getProperty("name");
            if (selectControlName == "AVGSelect")
                this.getAVGData(item.mParameters.selectedItem.getKey());
            else if (selectControlName == "inProcessChangeSelect")
                this.getProcessData(item.mParameters.selectedItem.getKey());
        },
    };

    return DHMCharts;

});