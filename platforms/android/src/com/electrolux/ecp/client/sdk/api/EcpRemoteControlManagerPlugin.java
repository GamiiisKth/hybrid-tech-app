package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.command.EcpComponentValue;
import com.electrolux.ecp.client.sdk.exception.EcpComponentException;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpConfiguration;
import com.electrolux.ecp.client.sdk.manager.EcpConfigurationManager;
import com.electrolux.ecp.client.sdk.manager.EcpRemoteControlManager;
import com.electrolux.ecp.client.sdk.model.EcpProfile2;
import com.electrolux.ecp.client.sdk.model.commands.EcpApplianceJSONCommand;
import com.electrolux.ecp.client.sdk.model.commands.response.EcpSendJSONCommandResponse;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.configuration.response.EcpConfigurationBundle;
import com.electrolux.ecp.client.sdk.model.profile.EcpApplianceProfile;
import com.electrolux.ecp.client.sdk.model.profile.EcpApplianceState;
import com.electrolux.ecp.client.sdk.model.profile.EcpBaseComponent;
import com.electrolux.ecp.client.sdk.model.profile.EcpCommand;
import com.electrolux.ecp.client.sdk.model.profile.EcpComponentStep;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.model.EcpAppliance;
import com.electrolux.ecp.client.sdk.util.EcpRemoteControlUtil;
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

public final class EcpRemoteControlManagerPlugin extends CordovaPlugin {

    private final Gson gson = new Gson();
    private EcpRemoteControlManager mManager;
    private EcpConfigurationManager mConfigManager;
    private static EcpConfiguration mConfiguration = EcpConfigurationPlugin.getConfiguration();
    private static final String TAG = "RemoteCtrlMngr CONSOLE";
    private EcpConfigurationBundle mConfigurationBundle;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpRemoteControlManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if (mConfigManager == null) {
            mConfigManager = new EcpConfigurationManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("sendJSONCommandAsync".equals(action)) {
            sendJSONCommandAsync(args, callbackContext);
            return true;
        }
        if ("sendJSONCommandAsync".equals(action)) {
            sendJSONCommandAsync(args, callbackContext);
            return true;
        }
        if ("getApplianceCommandRequest".equals(action)) {
            getApplianceCommandRequest(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void getApplianceCommandRequest(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        final String commandName;
        final String source;

        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs(((JSONObject)((JSONArray) args.get(0)).get(0)));
            commandName = ((JSONArray) args.get(0)).getString(1);

            source = (((JSONArray) args.get(0)).get(2)).toString();
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        EcpCallback<EcpConfigurationBundle> profileCallback = new EcpCallback<EcpConfigurationBundle>() {
            @Override
            public void onSuccess(EcpConfigurationBundle ecpConfigurationBundle) {
                final EcpCommand command;
                mConfiguration.setContext(EcpRemoteControlManagerPlugin.this.cordova.getActivity());
                mConfiguration.storeConfiguration(applianceNumber, ecpConfigurationBundle);
                final EcpApplianceProfile applianceProfile = mConfiguration.findConfigurationByApplianceNumber(applianceNumber);
                final EcpApplianceState applianceState = new EcpApplianceState(applianceProfile);
                final List<EcpBaseComponent> applianceComponents = applianceProfile.getComponents(applianceState);
                EcpBaseComponent targetComponent = null;
                for (EcpBaseComponent component : applianceComponents) {
                    if (component.getKey().equals(commandName)) {
                        targetComponent = component;
                        break;
                    }
                }

                command = EcpCommand.from(targetComponent);

                EcpComponentValue componentValue = null;
                for (EcpComponentStep step : command.getSteps()) {
                    if (applianceState.getComponentValue(command) != null) {
                        if (!applianceState.getComponentValue(command).isEqualTo(step)) {
                            componentValue = EcpComponentValue.from(command.getDataFormat(), step.getValue());
                        }
                    }
                }
                command.setValue(componentValue);
                try {
                    final EcpApplianceJSONCommand jsonCommand = EcpRemoteControlUtil.getApplianceCommandRequest(
                            command,
                            null,
                            applianceState,
                            source,
                            "ad",
                            "EXE");
                    callbackContext.success(gson.toJson(jsonCommand));
                } catch (EcpComponentException e) {
                    callbackContext.error(e.getMessage());
                }
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };

        mConfigManager.getConfigurationProfileAsync(profileCallback, applianceNumber);


    }

    public final void sendJSONCommandAsync(final JSONArray args, final CallbackContext callbackContext) {
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
        String command = "";
        try {
            if (((JSONArray) args.get(0)).length() > 1) {
                try {
                    command = (((JSONObject)((JSONArray) args.get(0)).get(1))).toString();
                } catch (JSONException e) {
                    callbackContext.error(e.getMessage());
                    return;
                }
            }
            else {
                callbackContext.error("Not enough parameters.");
                return;
            }
        } catch (JSONException e) {
            callbackContext.error("Couldn't parse command.");
            return;
        }
        final EcpCallback<EcpSendJSONCommandResponse> callback = new EcpCallback<EcpSendJSONCommandResponse>() {
            @Override
            public void onSuccess(EcpSendJSONCommandResponse ecpSendJSONCommandResponse) {
                callbackContext.success(gson.toJson(ecpSendJSONCommandResponse));
            }

            @Override
            public void onFailure(EcpError ecpError) {

                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.sendCommandAsync(callback, applianceNumber, gson.fromJson(command, EcpApplianceJSONCommand.class));
    }

}
