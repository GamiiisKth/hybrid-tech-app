package com.electrolux.ecp.client.sdk.api;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.exception.EcpLocalizationValueNotFoundException;
import com.electrolux.ecp.client.sdk.exception.EcpNoLocalizationKeyException;
import com.electrolux.ecp.client.sdk.manager.EcpConfigurationManager;
import com.electrolux.ecp.client.sdk.manager.EcpLocalizationManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.electrolux.ecp.client.sdk.model.localization.EcpLocale;
import com.electrolux.ecp.client.sdk.model.profile.EcpApplianceProfile;
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

public final class EcpLocalizationManagerPlugin extends CordovaPlugin {
    private EcpLocalizationManager mLocalizationManager;
    private EcpApplianceProfile mApplianceProfile;
    private final Gson gson = new Gson();
    private EcpConfigurationManager mManager;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (mManager == null) {
            mManager = new EcpConfigurationManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("getAvailableTranslations".equals(action)) {
            getAvailableTranslations(args, callbackContext);
            return true;
        }
        if ("getValue".equals(action)) {
            getValue(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void getAvailableTranslations(final JSONArray args, final CallbackContext callbackContext) {
        String key = "";
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0));
            key = (String)((JSONArray)((JSONArray) args.get(0))).get(1);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final String requestedKey = key;
        final EcpCallback<EcpConfigurationBundle> callback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle configurationBundle) {
                mApplianceProfile = EcpApplianceProfile.from(configurationBundle.getProfile(), cordova.getActivity().getApplicationContext());
                mLocalizationManager = new EcpLocalizationManager(configurationBundle.getLocalizationProfile());
                /*mManager.getAssetsAsync(new EcpSimpleCallback<List<EcpAssetProfile.AssetBundle>>(cordova.getActivity()) {
                    @Override
                    public void onSuccess(List<EcpAssetProfile.AssetBundle> assetBundles) {
                        mAssetBundles = assetBundles;
                        displayAllComponents();
                        subscribeToAppliance();
                    }
                }, configurationBundle);*/
                callbackContext.success(gson.toJson(mLocalizationManager.getAvailableTranslations(requestedKey)));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getConfigurationProfile(callback, applianceNumber);
    }
    public final void getValue(final JSONArray args, final CallbackContext callbackContext) {
        String key = "";
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0));
            key = (String)((JSONArray)((JSONArray) args.get(0))).get(1);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final String requestedKey = key;
        final EcpCallback<EcpConfigurationBundle> callback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle configurationBundle) {
                mApplianceProfile = EcpApplianceProfile.from(configurationBundle.getProfile(), cordova.getActivity().getApplicationContext());
                mLocalizationManager = new EcpLocalizationManager(configurationBundle.getLocalizationProfile());
                /*mManager.getAssetsAsync(new EcpSimpleCallback<List<EcpAssetProfile.AssetBundle>>(cordova.getActivity()) {
                    @Override
                    public void onSuccess(List<EcpAssetProfile.AssetBundle> assetBundles) {
                        mAssetBundles = assetBundles;
                        displayAllComponents();
                        subscribeToAppliance();
                    }
                }, configurationBundle);*/
                try {
                    callbackContext.success(mLocalizationManager.getValue(requestedKey, new EcpLocale("EN")));
                } catch (EcpLocalizationValueNotFoundException e) {
                    callbackContext.error(e.getMessage());
                } catch (EcpNoLocalizationKeyException e) {
                    callbackContext.error(e.getMessage());
                }
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getConfigurationProfile(callback, applianceNumber);
    }


}
