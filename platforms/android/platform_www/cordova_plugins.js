cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "bms-core.BMSClient",
    "file": "plugins/bms-core/www/BMSClient.js",
    "pluginId": "bms-core",
    "clobbers": [
      "BMSClient"
    ]
  },
  {
    "id": "bms-core.BMSRequest",
    "file": "plugins/bms-core/www/BMSRequest.js",
    "pluginId": "bms-core",
    "clobbers": [
      "BMSRequest"
    ]
  },
  {
    "id": "bms-core.BMSLogger",
    "file": "plugins/bms-core/www/BMSLogger.js",
    "pluginId": "bms-core",
    "clobbers": [
      "BMSLogger"
    ]
  },
  {
    "id": "bms-core.BMSAnalytics",
    "file": "plugins/bms-core/www/BMSAnalytics.js",
    "pluginId": "bms-core",
    "clobbers": [
      "BMSAnalytics"
    ]
  },
  {
    "id": "bms-core.BMSAuthorizationManager",
    "file": "plugins/bms-core/www/BMSAuthorizationManager.js",
    "pluginId": "bms-core",
    "clobbers": [
      "BMSAuthorizationManager"
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
  "cordova-plugin-cocoapod-support": "1.3.0",
  "cordova-plugin-add-swift-support": "1.7.0",
  "bms-core": "2.3.9",
  "cordova-plugin-whitelist": "1.3.2",
  "ecp-plugin": "1.3.2"
};
// BOTTOM OF METADATA
});