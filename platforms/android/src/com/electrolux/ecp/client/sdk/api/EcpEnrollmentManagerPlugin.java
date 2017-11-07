package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.manager.EcpEnrollmentManager;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpEnrollmentManagerPlugin extends CordovaPlugin {

    private final Gson gson = new Gson();
    private EcpEnrollmentManager mManager;
    private static final String TAG = "ConfigMngr CONSOLE";
    private EcpConfigurationBundle mConfigurationBundle;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpEnrollmentManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("registerAsync".equals(action)) {
            registerAsync(args, callbackContext);
            return true;
        }
        if ("unregisterAsync".equals(action)) {
            unregisterAsync(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void registerAsync(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.success("Method invoked by EcpUserManagerPlugin");
    }
    public final void unregisterAsync(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.success("Method invoked by EcpUserManagerPlugin");
    }


}
