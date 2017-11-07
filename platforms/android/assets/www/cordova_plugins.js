cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "cordova-hot-code-push-plugin.chcp",
    "file": "plugins/cordova-hot-code-push-plugin/www/chcp.js",
    "pluginId": "cordova-hot-code-push-plugin",
    "clobbers": [
      "chcp"
    ]
  },
  {
    "id": "cordova-plugin-cache-clear.CacheClear",
    "file": "plugins/cordova-plugin-cache-clear/www/CacheClear.js",
    "pluginId": "cordova-plugin-cache-clear",
    "clobbers": [
      "CacheClear"
    ]
  },
  {
    "id": "ecp-plugin.mobilesdk",
    "file": "plugins/ecp-plugin/www/mobilesdk.js",
    "pluginId": "ecp-plugin",
    "clobbers": [
      "mobilesdk"
    ]
  }
];
module.exports.metadata = 
// TOP OF METADATA
{
  "cordova-hot-code-push-plugin": "1.5.3",
  "cordova-plugin-add-swift-support": "1.7.0",
  "cordova-plugin-cache-clear": "1.3.7",
  "cordova-plugin-cocoapod-support": "1.3.0",
  "cordova-plugin-whitelist": "1.3.2",
  "ecp-plugin": "1.3.2"
};
// BOTTOM OF METADATA
});