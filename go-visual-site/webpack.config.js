var webpack = require('webpack');
 
module.exports = {
    //页面入口文件配置
    entry: {
        index : './index.js'
    },
    //入口文件输出配置
    output: {
        path: __dirname,
        filename: "bundle.js"
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery"
        })
    ]
};