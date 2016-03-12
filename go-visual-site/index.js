var echarts = require('echarts');

var myChart = echarts.init(document.getElementById('main'));
// 显示标题，图例和空的坐标轴
myChart.setOption({
    title: {
        text: '',
        x: 'center',
       align: 'right'
    },
    tooltip: {},
    legend: {
        data:['pipeline 运行次数','pipeline 连续失败'],
        x: 'left'
    },
    xAxis: {
        data: []
    },
    yAxis: [
      {
          name: '次数',
          type: 'value'
      }
    ],
    series: [{
        name:'pipeline 运行次数',
        type: 'bar',
        data: []
    },
    {
        name:'pipeline 连续失败',
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
        xAxis: {
            data: data.categories
        },
        series: [{
            data: data.pipelineRunTimes
        },
        {
            data: data.countinueFailurCount
        }]
    });
});


