const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const handlebarParser = require('./handlebarParser.js');

const SOURCE_ROOT = __dirname + '/src/main/webpack';
const s_root = __dirname + '/oca-comp-parsed'

module.exports = env => {

    const writeToDisk = env && Boolean(env.writeToDisk);
    console.log('recieved environment config: ', env);
    handlebarParser(env);

    return merge(common, {
        mode: 'development',
        performance: {
            hints: 'warning',
            maxAssetSize: 1048576,
            maxEntrypointSize: 1048576
        },
        plugins: [
            new HtmlWebpackPlugin({
                template: path.resolve(__dirname, s_root + '/index.html')
            })
        ],
        devServer: {
            proxy: [{
                context: ['/content', '/etc.clientlibs'],
                target: 'http://localhost:4502',
            }],
            client: {
                overlay: {
                    errors: true,
                    warnings: false,
                },
            },
            watchFiles: ['src/**/*'],
            hot: true,
            static: [
                path.resolve(__dirname, 'oca-comp-parsed'),
                path.resolve(__dirname, SOURCE_ROOT + '/resources')
            ],
            port: 8080,
            compress: true,
            open: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
                "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization"
            },
            devMiddleware: {
                writeToDisk: writeToDisk
            }
        }
    });
}
