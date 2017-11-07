package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.listener.EcpApplianceStateUpdateEvent;
import com.electrolux.ecp.client.sdk.listener.EcpApplianceStateUpdateListener;
import com.electrolux.ecp.client.sdk.manager.EcpRemoteMonitoringManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpRemoteMonitoringManagerPlugin extends CordovaPlugin {

    private final Gson gson = new Gson();
    private EcpRemoteMonitoringManager mManager;
    private static final String TAG = "RemoteMonMngr CONSOLE";
    private EcpConfigurationBundle mConfigurationBundle;
    private final Map<String, EcpApplianceStateUpdateListener> listeners = new HashMap<String, EcpApplianceStateUpdateListener>();


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpRemoteMonitoringManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("subscribeAsync".equals(action)) {
            subscribeAsync(args, callbackContext);
            return true;
        }
        if ("unsubscribe".equals(action)) {
            unsubscribe(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void subscribeAsync(final JSONArray args, final CallbackContext callbackContext) {
        EcpApplianceNumber ecpApplianceNumber;
        try {
            ecpApplianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject) ((JSONArray)args.get(0)).get(1));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<Boolean> callback = new EcpCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "Successfully subscribed to state updates: " + aBoolean.toString());
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        final EcpApplianceStateUpdateListener listener = new EcpApplianceStateUpdateListener() {
            @Override
            public void onStateUpdated(EcpApplianceStateUpdateEvent ecpApplianceStateUpdateEvent) {

                Timber.i("Update event: " + gson.toJson(ecpApplianceStateUpdateEvent));

                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, gson.toJson(ecpApplianceStateUpdateEvent));
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        };
        int sessionTime = 3600;
        mManager.subscribeAsync(callback, sessionTime, ecpApplianceNumber, listener);
        listeners.put(ecpApplianceNumber.getApplianceId(), listener);
    }

    public final void unsubscribe(final JSONArray args, final CallbackContext callbackContext) {

        EcpApplianceNumber ecpApplianceNumber;
        try {
            ecpApplianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject) args.get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpApplianceStateUpdateListener listener = new EcpApplianceStateUpdateListener() {
            @Override
            public void onStateUpdated(EcpApplianceStateUpdateEvent ecpApplianceStateUpdateEvent) {
                callbackContext.success(gson.toJson(ecpApplianceStateUpdateEvent));
            }
        };
        try {
            mManager.unsubscribe(ecpApplianceNumber, listener);
            listeners.remove(ecpApplianceNumber.getApplianceId());
            //callbackContext.success("Successfully unsubscribed.");
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

}
