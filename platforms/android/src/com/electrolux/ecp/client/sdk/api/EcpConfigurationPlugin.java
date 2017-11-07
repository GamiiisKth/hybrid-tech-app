package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpConfiguration;
import com.electrolux.ecp.client.sdk.manager.EcpConfigurationManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.electrolux.ecp.client.sdk.model.profile.EcpApplianceProfile;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.model.EcpAppliance;
import com.electrolux.ecp.client.sdk.util.StringUtil;
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

public final class EcpConfigurationPlugin extends CordovaPlugin {

    private static EcpConfiguration mConfiguration = new EcpConfiguration.Builder().build();
    private final Gson gson = new Gson();
    private EcpConfigurationManager mManager;

    private static final String TAG = "ConfigMngr CONSOLE";

    public final static EcpApplianceNumber createApplianceNumberFromArgs(final JSONObject number) throws EcpException {

        String pnc = "", elc = "", serialNumber = "", timeFrom= "", macAddress = "";
        if (number.length() > 0) {
            try {
                pnc = number.getString("pnc");
                elc = number.getString("elc");
                serialNumber = number.getString("serial_number");
                if (number.has("mac")) {
                    macAddress = number.getString("mac");
                }
                else {
                    macAddress = number.getString("mac_address");
                }
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

    public final static EcpAppliance createEcpApplianceFromArgs(final JSONArray args) throws EcpException {

        JSONObject arguments = new JSONObject();
        JSONObject appliancePropsJson = new JSONObject();
        JSONObject applianceJson = new JSONObject();
        EcpAppliance ecpAppliance;
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                appliancePropsJson = arguments.getJSONObject("appliance_properties");
                applianceJson = appliancePropsJson.getJSONObject("appliance");
                ecpAppliance = new EcpAppliance.Builder()
                        .applianceType(applianceJson.getString("appliance_type"))
                        .pnc(applianceJson.getString("pnc"))
                        .elc(applianceJson.getString("elc"))
                        .sn(applianceJson.getString("serial_number"))
                        .mac(applianceJson.getString("mac"))
                        .brand(applianceJson.getString("brand"))
                        .currency(applianceJson.getString("currency"))
                        .storeId(applianceJson.getString("store_id"))
                        .purchaseDate(applianceJson.getString("purchase_date"))
                        .build();
            } catch (JSONException e) {
                throw new EcpException("Parsing Error", "Invalid parameters");
            }
        }
        else {
            throw new EcpException("Parsing Error", "Invalid parameters (Not enough parameters)");
        }
        return ecpAppliance;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null && mConfiguration.getBaseUrl() != null) {
            mManager = new EcpConfigurationManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("create".equals(action)) {
            create(args, callbackContext);
            return true;
        }
        if ("findConfigurationByApplianceNumber".equals(action)) {
            findConfigurationByApplianceNumber(args, callbackContext);
            return true;
        }
        if ("storeConfiguration".equals(action)) {
            storeConfiguration(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private final void create(JSONArray args, CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                mConfiguration = new EcpConfiguration.Builder()
                        .baseUrl(arguments.getString("baseUrl"))
                        .apiKey(arguments.getString("apiKey"))
                        .clientId(arguments.getString("clientId"))
                        .offlineMode(arguments.getBoolean("isOfflineMode"))
                        .context(this.cordova.getActivity()
                        )
                        .build();
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        callbackContext.success();
    }
    private final void findConfigurationByApplianceNumber(JSONArray args, CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = createApplianceNumberFromArgs((JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpApplianceProfile profile = mConfiguration.findConfigurationByApplianceNumber(applianceNumber);
        if (profile != null) {
            callbackContext.success(gson.toJson(profile));
            return;
        }
        else {
            callbackContext.error("Couldn't find configuration profile for the appliance number: " + applianceNumber.getApplianceId());
            return;
        }

    }
    private final void storeConfiguration(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = createApplianceNumberFromArgs((JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final String applianceAsNumber = StringUtil.applianceAsNumber(applianceNumber.getPnc(), applianceNumber.getElc(), applianceNumber.getSerialNo());
        final EcpCallback<EcpConfigurationBundle> callback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle ecpConfigurationBundle) {
                mConfiguration.storeConfiguration(applianceNumber, ecpConfigurationBundle);
                callbackContext.error("Success: EcpConfigurationPlugin::storeConfiguration");
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getConfigurationProfile(callback, applianceNumber);
    }

    static EcpConfiguration getConfiguration() {
        return mConfiguration;
    }

}
