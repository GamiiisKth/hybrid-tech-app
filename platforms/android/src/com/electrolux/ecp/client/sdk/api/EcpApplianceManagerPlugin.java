package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.android.sdk.notification.INotificationListener;
import com.electrolux.android.sdk.notification.models.IndoorNotification;
import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpApplianceManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.model.EcpAppliance;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author ondrejkubo on 06/06/2017.
 */
public final class EcpApplianceManagerPlugin extends CordovaPlugin {

    private static final String TAG = "ApplianceMngr CONSOLE";
    private EcpApplianceManager mManager;
    private final Gson gson = new Gson();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpApplianceManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("getAppliancesAsync".equals(action)) {
            getAppliancesAsync(args, callbackContext);
            return true;
        }
        if ("getApplianceAsync".equals(action)) {
            getApplianceAsync(args, callbackContext);
            return true;
        }
        if ("registerApplianceAsync".equals(action)) {
            registerApplianceAsync(args, callbackContext);
            return true;
        }
        if ("updateApplianceAsync".equals(action)) {
            updateApplianceAsync(args, callbackContext);
            return true;
        }
        if ("deleteApplianceAsync".equals(action)) {
            deleteApplianceAsync(args, callbackContext);
            return true;
        }
        if ("addNotificationListener".equals(action)) {
            addNotificationListener(args, callbackContext);
            return true;
        }
        if ("removeNotificationListener".equals(action)) {
            removeNotificationListener(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }


    public void registerApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpAppliance ecpAppliance;
        try {
            ecpAppliance = EcpConfigurationPlugin.createEcpApplianceFromArgs(args);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<EcpAppliance> callback = new EcpCallback<EcpAppliance>() {
            @Override
            public void onSuccess(EcpAppliance ecpApplianceInfo) {
                callbackContext.success(gson.toJson(ecpApplianceInfo));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.registerApplianceAsync(callback, ecpAppliance);
    }
    public void getApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)args.get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<EcpAppliance> callback = new EcpCallback<EcpAppliance>() {

            @Override
            public void onSuccess(EcpAppliance applianceInfo) {
                callbackContext.success(gson.toJson(applianceInfo));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getApplianceAsync(callback, applianceNumber);
    }
    public void getAppliancesAsync(final JSONArray args, final CallbackContext callbackContext) {
        final Gson gson = new Gson();
        final EcpCallback<List<EcpAppliance>> callback = new EcpCallback<List<EcpAppliance>>() {

            @Override
            public void onSuccess(List<EcpAppliance> applianceInfos) {
                JSONArray response = new JSONArray();
                for (EcpAppliance appliance: applianceInfos) {
                    response.put(gson.toJson(appliance));
                }
                callbackContext.success(response);
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getAppliancesAsync(callback);
    }
    public void updateApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
        String applianceId;
        if (args.length() > 1) {
            try {
                applianceId = (String) args.get(1);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        else {
            callbackContext.error("Not enough parameters.");
            return;
        }
        EcpAppliance ecpAppliance;
        try {
            ecpAppliance = EcpConfigurationPlugin.createEcpApplianceFromArgs(args);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<EcpAppliance> callback =  new EcpCallback<EcpAppliance>() {
            @Override
            public void onSuccess(EcpAppliance ecpApplianceInfo) {
                callbackContext.success(gson.toJson(ecpApplianceInfo));
            }
            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.updateApplianceAsync(callback, ecpAppliance);
    }

    public void deleteApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs(((JSONObject)((JSONArray) args.get(0)).get(0)));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<String> callback = new EcpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                callbackContext.success(s);
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.deleteApplianceAsync(callback, applianceNumber);
    }
    public void addNotificationListener(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.error("Method addNotificationListener doesn't exist in the Manager");
    }
    public void removeNotificationListener(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.error("Method addNotificationListener doesn't exist in the Manager");
    }

}
