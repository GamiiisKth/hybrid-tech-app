package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.manager.EcpUserManager;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.EcpRegisterUserRequest;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.EcpUpdateUserRequest;
import com.electrolux.ecp.client.sdk.model.usermanagement.response.EcpGetSessionKeyResponse;
import com.electrolux.ecp.client.sdk.model.usermanagement.response.EcpRegisterUserResponse;
import com.electrolux.ecp.client.sdk.model.usermanagement.response.model.EcpUserInfo;
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

public final class EcpUserManagerPlugin extends CordovaPlugin {

    //private static final String TAG = EcpUserManagerPlugin.class.getSimpleName();
    private static final String TAG = "UserManager CONSOLE";
    private EcpUserManager mManager;
    private final Gson gson = new Gson();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Action triggered: " + action);
        if (mManager == null) {
            mManager = new EcpUserManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("getRegistrationFieldsAsync".equals(action)) {
            getRegistrationFieldsAsync(args, callbackContext);
            return true;
        }
        if ("registerUserAsync".equals(action)) {
            registerUserAsync(args, callbackContext);
            return true;
        }
        if ("getSessionKeyAsync".equals(action)) {
            getSessionKeyAsync(args, callbackContext);
            return true;
        }
        if ("getUserAsync".equals(action)) {
            getUserAsync(args, callbackContext);
            return true;
        }
        if ("updateUserAsync".equals(action)) {
            updateUserAsync(args, callbackContext);
            return true;
        }
        if ("deleteUserAsync".equals(action)) {
            deleteUserAsync(args, callbackContext);
            return true;
        }

        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void getRegistrationFieldsAsync(final JSONArray args, final CallbackContext callbackContext) {
        callbackContext.error("Unsupported method on Android platform.");
    }

    public final void registerUserAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        JSONObject userProps = new JSONObject();
        EcpRegisterUserRequest request = null;
        if (args.length() > 0) {
            try {
                request = gson.fromJson(((JSONObject)args.get(0)).getJSONObject("user_properties").getJSONObject("user_info").toString(), EcpRegisterUserRequest.class);
                request.setCountry(((JSONObject)args.get(0)).getJSONObject("user_properties").getString("country_name"));
                request.setUsername(((JSONObject)args.get(0)).getJSONObject("user_properties").getJSONObject("user_info").getString("email"));
                request.setData(gson.fromJson(((JSONObject)args.get(0)).getJSONObject("user_properties").getJSONObject("user_info").toString(), EcpRegisterUserRequest.Data.class));
                /*
                arguments = (JSONObject) args.get(0);
                userInfo.setEmail(arguments.getString("email"));
                userInfo.setPassword(arguments.getString("password"));
                userInfo.setFirstname(arguments.getString("firstName"));
                userInfo.setLastname(arguments.getString("lastName"));
                userInfo.setAddress(arguments.getString("address"));
                userInfo.setCity(arguments.getString("city"));
                userInfo.setPostalCode(arguments.getString("postalCode"));
                */
                //request =new EcpRegisterUserRequest(arguments.getString("country"), userInfo);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        EcpCallback<EcpRegisterUserResponse> callback = new EcpCallback<EcpRegisterUserResponse>() {
            @Override
            public void onSuccess(EcpRegisterUserResponse ecpRegisterUserResponse) {
                callbackContext.success(gson.toJson(ecpRegisterUserResponse.getData()));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.registerUserAsync(callback, request);
    }

    public final void getSessionKeyAsync(final JSONArray args, final CallbackContext callbackContext) {
        Log.i(TAG, "getSessionKeyAsync");
        EcpCallback<EcpGetSessionKeyResponse> callback = new EcpCallback<EcpGetSessionKeyResponse>() {

            @Override
            public void onSuccess(EcpGetSessionKeyResponse ecpGetSessionKeyResponse) {
                Log.i(TAG, "Session token response: " + ecpGetSessionKeyResponse.toString());
                callbackContext.success(gson.toJson(ecpGetSessionKeyResponse.getData()));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                Log.i(TAG, "Session token error: " + ecpError.getReason().getMessage());
                callbackContext.error(ecpError.getErrorCode());
            }
        };
        String
            email = "",
            password = "",
            countryName = "",
            deviceId = "";
        JSONObject arguments = new JSONObject();
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                email = arguments.getString("email");
                password = arguments.getString("password");
                countryName = arguments.getString("countryName");
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
        }
        mManager.getSessionKeyAsync(callback, email, password, countryName);
    }

    public final void getUserAsync(final JSONArray args, final CallbackContext callbackContext) {
        EcpCallback<EcpUserInfo> callback = new EcpCallback<EcpUserInfo>() {

            @Override
            public void onSuccess(EcpUserInfo ecpUserInfo) {
                callbackContext.success(gson.toJson(ecpUserInfo));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getUserAsync(callback);
    }

    public final void updateUserAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        EcpUpdateUserRequest.Data request = null;
        EcpRegisterUserRequest.Data regRequest = null;
        if (args.length() > 0) {
            try {
                request = gson.fromJson(((JSONObject)args.get(0)).get("user_properties").toString(), EcpUpdateUserRequest.Data.class);
                regRequest = gson.fromJson(((JSONObject)args.get(0)).get("user_properties").toString(), EcpRegisterUserRequest.Data.class);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        EcpCallback<EcpUserInfo> callback = new EcpCallback<EcpUserInfo>() {
            @Override
            public void onSuccess(EcpUserInfo userinfo) {
                callbackContext.success(gson.toJson(userinfo));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.updateUserAsync(callback, request);
    }

    public final void deleteUserAsync(final JSONArray args, final CallbackContext callbackContext) {
        EcpCallback<Boolean> callback = new EcpCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean response) {
                callbackContext.success(gson.toJson(response));
            }

            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.deleteUserAsync(callback);
    }


}
