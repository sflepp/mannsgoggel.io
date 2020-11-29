const {override, addWebpackPlugin} = require('customize-cra')
const {addReactRefresh} = require('customize-cra-react-refresh')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');

module.exports = override(addReactRefresh(),
    addWebpackPlugin(new MonacoWebpackPlugin())
)