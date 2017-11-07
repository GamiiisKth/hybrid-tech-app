package com.electrolux.ecp.client.sdk.api;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.listener.EcpApplianceStateUpdateEvent;
import com.electrolux.ecp.client.sdk.listener.EcpApplianceStateUpdateListener;
import com.electrolux.ecp.client.sdk.manager.EcpApplianceStateMonitoringManager;
import com.electrolux.ecp.client.sdk.manager.EcpRemoteMonitoringManager;
import com.electrolux.ecp.client.sdk.manager.EcpReportManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by ondrejkubo on 13/06/2017.
 */

public class EcpApplianceStateMonitoringManagerPlugin extends CordovaPlugin {


    private final Gson gson = new Gson();
    private EcpApplianceStateMonitoringManager mManager;
    private static final String TAG = "ApplianceStateMngr CONSOLE";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (mManager == null) {
            final EcpReportManager rm = new EcpReportManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
            final EcpRemoteMonitoringManager rmm = new EcpRemoteMonitoringManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
            mManager = new EcpApplianceStateMonitoringManager(rm, rmm);
        }
        if ("subscribeApplianceStateAsync".equals(action)) {
            subscribeApplianceStateAsync(args, callbackContext);
            return true;
        }
        if ("unsubscribeApplianceStateAsync".equals(action)) {
            unsubscribeApplianceStateAsync(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void subscribeApplianceStateAsync(final JSONArray args, final CallbackContext callbackContext) {
        EcpApplianceNumber ecpApplianceNumber;
        int sessionTime = 3600;
        try {
            ecpApplianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject) ((JSONArray)args.get(0)).get(1));
            sessionTime = (Integer) ((JSONArray)args.get(0)).get(0);
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
        mManager.subscribeApplianceStateAsync(callback, sessionTime, ecpApplianceNumber, listener);
    }
    public final void unsubscribeApplianceStateAsync(final JSONArray args, final CallbackContext callbackContext) {
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
        final EcpCallback<Boolean> callback = new EcpCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

                callbackContext.success("Successfully unsubscribed from state updates: " + aBoolean.toString());
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.unsubscribeApplianceStateAsync(callback, ecpApplianceNumber);
    }
}
