package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpOnboardingManager;
import com.electrolux.ecp.client.sdk.manager.EcpOtaManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.ota.EcpOtaDescriptionFile;
import com.electrolux.ecp.client.sdk.model.ota.EcpOtaStatusUpdateEvent;
import com.electrolux.ecp.client.sdk.model.ota.response.model.EcpOtaStatus;
import com.electrolux.ecp.client.sdk.router.EcpAllJoynModule;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpOtaManagerPlugin extends CordovaPlugin {

    private EcpOtaManager mManager;
    private final Gson gson = new Gson();
    private static final String TAG = "OTA Manager CONSOLE";
    EcpOtaManager.EcpOtaStateUpdateListener mListener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpOtaManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }

        if ("getOTADownloadStatusAsync".equals(action)) {
            getOTADownloadStatusAsync(args, callbackContext);
            return true;
        }
        if ("update".equals(action)) {
            update(args, callbackContext);
            return true;
        }
        if ("addOnOTAStatusUpdateListener".equals(action)) {
            addOnOTAStatusUpdateListener(args, callbackContext);
            return true;
        }
        if ("removeOnOTAStatusUpdateListener".equals(action)) {
            removeOnOTAStatusUpdateListener(args, callbackContext);
            return true;
        }
        if ("downloadDescriptionFile".equals(action)) {
            downloadDescriptionFile(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void getOTADownloadStatusAsync(final JSONArray args, final CallbackContext callbackContext) {
        EcpCallback<EcpOtaStatus> callback = new EcpCallback<EcpOtaStatus>() {
            @Override
            public void onSuccess(EcpOtaStatus ecpOtaStatus) {
                callbackContext.success(gson.toJson(ecpOtaStatus));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.getOTADownloadStatusAsync(callback);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }
    public final void update(final JSONArray args, final CallbackContext callbackContext) {
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
        try {
            if (mManager.update(ecpApplianceNumber)) {
                callbackContext.success(gson.toJson(ecpApplianceNumber));
            }
            else {
                callbackContext.error(gson.toJson(ecpApplianceNumber));
            }
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }
    public final void addOnOTAStatusUpdateListener(final JSONArray args, final CallbackContext callbackContext) {
        if (mListener == null) {

            mListener = new EcpOtaManager.EcpOtaStateUpdateListener() {
                @Override
                public void onOtaStateChanged(EcpOtaStatusUpdateEvent ecpOtaStatusUpdateEvent) {
                    callbackContext.success(gson.toJson(ecpOtaStatusUpdateEvent));
                }
            };
        }
        try {
            mManager.addOnOTAStatusUpdateListener(mListener);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }

    public final void removeOnOTAStatusUpdateListener(final JSONArray args, final CallbackContext callbackContext) {
        try {
            mManager.removeOnOTAStatusUpdateListener(mListener);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }

    public final void downloadDescriptionFile(final JSONArray args, final CallbackContext callbackContext) {
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
        final EcpCallback<EcpOtaDescriptionFile> callback = new EcpCallback<EcpOtaDescriptionFile>() {
            @Override
            public void onSuccess(EcpOtaDescriptionFile descriptionFile) {
                callbackContext.success(gson.toJson(descriptionFile));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.downloadDescriptionFile(callback, ecpApplianceNumber);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }

}
