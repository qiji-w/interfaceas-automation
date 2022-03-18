/*
用于启用压缩，配合nginx设置，减小chunk-vendor.js大小 
*/
//【echarts配置】,添加依赖
const CopyWebpackPlugin = require('copy-webpack-plugin')
const path = require('path');

const webpack = require('webpack')
const CompressionWebpackPlugin = require('compression-webpack-plugin')
const productionGzipExtensions = ['js', 'css']
module.exports = {
    productionSourceMap: false,
    configureWebpack: {
        resolve: {
            alias: {
                '@': path.resolve(__dirname, './src'),
                '@i': path.resolve(__dirname, './src/assets'),
            }
        },
        plugins: [
            // Ignore all locale files of moment.js
            new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),

            // 配置compression-webpack-plugin压缩
            new CompressionWebpackPlugin({
                algorithm: 'gzip',
                test: new RegExp('\\.(' + productionGzipExtensions.join('|') + ')$'),
                threshold: 10240,
                minRatio: 0.8
            }),
            new webpack.optimize.LimitChunkCountPlugin({
                maxChunks: 5,
                minChunkSize: 100
            }),
            //【echart配置】，将echarts的js文件直接拷贝到dist目录
            new CopyWebpackPlugin([
                { from: './src/assets/js/echarts.common.min.js' ,to:'./js'},
             ])
        ],
        //【echart配置】，打包时排除echarts，减小chunk-vendor.js大小 ，提高首次加载速度
        externals: {
            echarts: "echarts",
        }
    }
}