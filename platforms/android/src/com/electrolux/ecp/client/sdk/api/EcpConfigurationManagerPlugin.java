package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpConfigurationManager;
import com.electrolux.ecp.client.sdk.model.asset.EcpAssetProfile;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.electrolux.ecp.client.sdk.model.configuration.response.model.EcpApplianceConfiguration;
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
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpConfigurationManagerPlugin extends CordovaPlugin {

    private final Gson gson = new Gson();
    private EcpConfigurationManager mManager;
    private static final String TAG = "ConfigMngr CONSOLE";
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpConfigurationManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("getApplianceConfigurationsAsync".equals(action)) {
            getApplianceConfigurationsAsync(args, callbackContext);
            return true;
        }
        if ("getConfigurationProfile".equals(action)) {
            getConfigurationProfile(args, callbackContext);
            return true;
        }
        if ("getAssetsAsync".equals(action)) {
            getAssetsAsync(args, callbackContext);
            return true;
        }
        if ("buildConfigurationAssetXMLAsync".equals(action)) {
            buildConfigurationAssetXMLAsync(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public void getApplianceConfigurationsAsync(final JSONArray args, final CallbackContext callbackContext) {
        /*
        final EcpCallback<List<EcpApplianceConfiguration>> callback= new EcpCallback<List<EcpApplianceConfiguration>>() {
            @Override
            public void onSuccess(List<EcpApplianceConfiguration> ecpApplianceConfigurations) {
                callbackContext.success(gson.toJson(ecpApplianceConfigurations));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getApplianceConfigurationsAsync(callback);
        */

        callbackContext.error("Method getApplianceConfigurationsAsync is no longer supported.");
    }

    public void getConfigurationProfile(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((args.get(0))));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<EcpConfigurationBundle> callback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle ecpConfigurationBundle) {
                callbackContext.success(gson.toJson(ecpConfigurationBundle));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getConfigurationProfile(callback, applianceNumber);
    }
    public void getAssetsAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<EcpConfigurationBundle> callback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle ecpConfigurationBundle) {
                final EcpCallback<List<EcpAssetProfile.AssetBundle>> callback = new EcpCallback<List<EcpAssetProfile.AssetBundle>>() {
                    @Override
                    public void onSuccess(List<EcpAssetProfile.AssetBundle> assetBundles) {
                        callbackContext.success(gson.toJson(assetBundles));
                    }

                    @Override
                    public void onFailure(EcpError ecpError) {
                        callbackContext.error(ecpError.getReason().getMessage());
                    }
                };
                mManager.getAssetsAsync(callback, ecpConfigurationBundle);
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getConfigurationProfile(callback, applianceNumber);

    }
    public void buildConfigurationAssetXMLAsync(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.error("Method can't be invoked directly on Android platform.");
    }
    /*
    private final static EcpApplianceNumber createApplianceNumberFromArgs(final JSONArray args) throws EcpException {

        JSONObject arguments = new JSONObject();
        String pnc = "", elc = "", serialNumber = "", timeFrom= "", macAddress = "";
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                pnc = arguments.getString("pnc");
                elc = arguments.getString("elc");
                serialNumber = arguments.getString("serialNumber");
                macAddress = arguments.getString("macAddress");
            } catch (JSONException e) {
                throw new EcpException("Parsing Error", "Error parsing JSON arguments");
            }
        }
        else {
            throw new EcpException("Parsing Error", "Error parsing JSON arguments");
        }
        final EcpApplianceNumber applianceNumber = new EcpApplianceNumber.Builder()
                .pnc(pnc)
                .elc(elc)
                .serialNo(serialNumber)
                .macAddress(macAddress)
                .build();
        return applianceNumber;
    }
    */
}
