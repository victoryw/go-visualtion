var echarts = require('echarts');

var myChart = echarts.init(document.getElementById('main'));
// 显示标题，图例和空的坐标轴
myChart.setOption({
    title: {
        text: ''
    },
    tooltip: {},
    legend: {
        data:['重试率']
    },
    xAxis: {
        data: []
    },
    yAxis: {},
    series: [{
        name:'trigger count',
        type: 'line',
        data: []
    }]
});

// 异步加载数据
$.get('data.json').done(function (data) {
    // 填入数据
    myChart.setOption({
        title:{
          text: data.title
        },
        legend: {
            data:[data.name]
        },
        xAxis: {
            data: data.categories
        },
        series: [{
            data: data.data
        }]
    });
});


