package com.electrolux.ecp.client.sdk.api;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpApplianceConnectionStateListener;
import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpConfigurationManager;
import com.electrolux.ecp.client.sdk.manager.EcpOnboardingManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.onboarding.model.EcpOnboardable;
import com.electrolux.ecp.client.sdk.model.onboarding.model.EcpWiFiNetwork;
import com.electrolux.ecp.client.sdk.model.onboarding.request.EcpOnboardApplianceRequest;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.model.EcpAppliance;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpOnboardingManagerPlugin extends CordovaPlugin {

    private EcpOnboardingManager mManager;
    private final Gson gson = new Gson();
    private static final String TAG = "OnboardMngr CONSOLE";
    private static final int PERMISSIONS_REQUEST_CODE = 2;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpOnboardingManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }

        if ("getRegistrationFieldsAsync".equals(action)) {
            startApplianceDiscovery(args, callbackContext);
            return true;
        }
        if ("startApplianceDiscovery".equals(action)) {
            startApplianceDiscovery(args, callbackContext);
            return true;
        }
        if ("stopApplianceDiscovery".equals(action)) {
            stopApplianceDiscovery(args, callbackContext);
            return true;
        }
        if ("getWiFiNetworksAsync".equals(action)) {
            getWiFiNetworksAsync(args, callbackContext);
            return true;
        }
        if ("onboardAppliance".equals(action)) {
            onboardAppliance(args, callbackContext);
            return true;
        }
        if ("offboardApplianceAsync".equals(action)) {
            offboardApplianceAsync(args, callbackContext);
            return true;
        }
        if ("addApplianceConnectionStateListener".equals(action)) {
            addApplianceConnectionStateListener(args, callbackContext);
            return true;
        }
        if ("removeApplianceConnectionStateListener".equals(action)) {
            removeApplianceConnectionStateListener(args, callbackContext);
            return true;
        }
        return false;
    }
    public void startApplianceDiscovery(final JSONArray args, final CallbackContext callbackContext) {
        if (checkPermissions()) startApplianceDiscoveryChecked(args, callbackContext);
    }

    private boolean checkPermissions() {
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this.cordova.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int storagePermissionCheck = ContextCompat.checkSelfPermission(this.cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> requiredPermissions = new ArrayList<String>();

        if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (storagePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (requiredPermissions.size() == 0) {
            return true;
        }

        ActivityCompat.requestPermissions(this.cordova.getActivity(), requiredPermissions.toArray(new String[requiredPermissions.size()]), PERMISSIONS_REQUEST_CODE);
        return false;
    }

    public final void startApplianceDiscoveryChecked(final JSONArray args, final CallbackContext callbackContext) {
        EcpCallback<List<EcpOnboardable>> callback = new EcpCallback<List<EcpOnboardable>>() {
            @Override
            public void onSuccess(List<EcpOnboardable> ecpOnboardables) {
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, gson.toJson(ecpOnboardables));
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.startApplianceDiscovery(callback);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }

    public final void stopApplianceDiscovery(final JSONArray args, final CallbackContext callbackContext) {
        try {
            mManager.stopApplianceDiscovery();
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
        callbackContext.success("Successfully stopped discovery.");
    }
    public final void getWiFiNetworksAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpOnboardable onboardable;
        try {
            onboardable = gson.fromJson(args.getJSONArray(0).getJSONObject(0).toString(), EcpOnboardable.class);
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<List<EcpWiFiNetwork>> callback =  new EcpCallback<List<EcpWiFiNetwork>>() {
            @Override
            public void onSuccess(List<EcpWiFiNetwork> ecpWiFiNetworks) {
                callbackContext.success(gson.toJson(ecpWiFiNetworks));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.getWiFiNetworksAsync(callback, onboardable);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }
    public final void onboardAppliance(final JSONArray args, final CallbackContext callbackContext) {
        final EcpOnboardable onboardable;
        final EcpWiFiNetwork wifi;
        final String wifiPassword;
        try {
            onboardable = gson.fromJson(args.getJSONArray(0).getJSONObject(0).toString(), EcpOnboardable.class);
            wifi = new EcpWiFiNetwork(args.getJSONArray(0).getString(1));
            wifiPassword = args.getJSONArray(0).getString(2);
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        EcpOnboardApplianceRequest request = new EcpOnboardApplianceRequest(
                onboardable,
                wifi,
                wifiPassword, "", "", "", "");
        final EcpCallback<EcpApplianceNumber> callback = new EcpCallback<EcpApplianceNumber>() {
            @Override
            public void onSuccess(EcpApplianceNumber ecpApplianceNumber) {
                callbackContext.success(gson.toJson(ecpApplianceNumber));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.onboardAppliance(callback, request);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }
    public final void offboardApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
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
        EcpCallback<Boolean> callback = new EcpCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                callbackContext.success(gson.toJson(aBoolean));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        try {
            mManager.offboardApplianceAsync(callback, applianceNumber);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }

    private EcpApplianceConnectionStateListener mStateListener = null;
    public final void addApplianceConnectionStateListener(final JSONArray args, final CallbackContext callbackContext) {
        mStateListener = new EcpApplianceConnectionStateListener() {
            @Override
            public void onApplianceConnected(EcpAppliance ecpAppliance) {
                callbackContext.success(gson.toJson(ecpAppliance));
            }

            @Override
            public void onApplianceLost(EcpAppliance ecpAppliance) {
                callbackContext.success(gson.toJson(ecpAppliance));
            }
        };
        try {
            mManager.addApplianceConnectionStateListener(mStateListener);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }
    public final void removeApplianceConnectionStateListener(final JSONArray args, final CallbackContext callbackContext) {
        try {
            mManager.removeApplianceConnectionStateListener(mStateListener);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
        }
    }


}
