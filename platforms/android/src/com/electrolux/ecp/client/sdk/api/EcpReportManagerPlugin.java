package com.electrolux.ecp.client.sdk.api;

import android.util.Log;

import com.electrolux.ecp.client.sdk.async.EcpCallback;
import com.electrolux.ecp.client.sdk.exception.EcpError;
import com.electrolux.ecp.client.sdk.exception.EcpException;
import com.electrolux.ecp.client.sdk.manager.EcpReportManager;
import com.electrolux.ecp.client.sdk.model.configuration.EcpApplianceNumber;
import com.electrolux.ecp.client.sdk.model.reporting.request.model.EcpCommandsTotalSelection;
import com.electrolux.ecp.client.sdk.model.reporting.request.model.EcpShareOfPropertiesValuesSelection;
import com.electrolux.ecp.client.sdk.model.reporting.response.EcpApplianceSearchResponse;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpApplianceAlert;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpApplianceEvent;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpLatestAmountApplianceState;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpLatestApplianceState;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpMostUsedProgram;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpReportCommand;
import com.electrolux.ecp.client.sdk.model.reporting.response.model.EcpWeeklyRuns;
import com.electrolux.ecp.client.sdk.model.usermanagement.request.model.EcpAppliance;
import com.electrolux.ecp.client.sdk.util.EcpDateUtil;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ondrejkubo on 13/06/2017.
 */

public final class EcpReportManagerPlugin extends CordovaPlugin {

    private static final String TAG = "ApplianceMngr CONSOLE";
    private EcpReportManager mManager;
    private static final String dateFormat = "yyyy.MM.dd HH:mm:ss";
    private static final String dateFormatJS = "yyyy/MM/dd HH:mm:ss";
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
            mManager = new EcpReportManager(this.cordova.getActivity(), EcpConfigurationPlugin.getConfiguration());
        }
        if ("getCommandsTotalAsync".equals(action)) {
            getCommandsTotalAsync(args, callbackContext);
            return true;
        }
        if ("getShareOfPropertyValuesAsync".equals(action)) {
            getShareOfPropertyValuesAsync(args, callbackContext);
            return true;
        }
        if ("getCommandsAsync".equals(action)) {
            getCommandsAsync(args, callbackContext);
            return true;
        }
        if ("getApplianceAlertsAsync".equals(action)) {
            getApplianceAlertsAsync(args, callbackContext);
            return true;
        }
        if ("getMostUsedProgramsAsync".equals(action)) {
            getMostUsedProgramsAsync(args, callbackContext);
            return true;
        }
        if ("getWeeklyRunsAsync".equals(action)) {
            getWeeklyRunsAsync(args, callbackContext);
            return true;
        }
        if ("getUserAppliancesAsync".equals(action)) {
            getUserAppliancesAsync(args, callbackContext);
            return true;
        }
        if ("searchApplianceAsync".equals(action)) {
            searchApplianceAsync(args, callbackContext);
            return true;
        }
        if ("getHistoricEventsAsync".equals(action)) {
            getHistoricEventsAsync(args, callbackContext);
            return true;
        }
        if ("getLatestAsync".equals(action)) {
            getLatestAsync(args, callbackContext);
            return true;
        }
        if ("getLatestAmountAsync".equals(action)) {
            getLatestAmountAsync(args, callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    public final void getCommandsTotalAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        String applianceType = "", pnc = "", elc = "", eventName = "", group = "",
                serialNumber = "", timeFrom = "";
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                applianceType = arguments.getString("appliance_type");
                pnc = arguments.getString("pnc");
                elc = arguments.getString("elc");
                serialNumber = arguments.getString("serial_number");
                eventName = arguments.getString("event_name");
                timeFrom = arguments.getString("time_from");
                group = arguments.getString("group_by");
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
        }
        final EcpCommandsTotalSelection selection = new EcpCommandsTotalSelection.Builder(applianceType)
                .eventName(eventName)
                .pnc(pnc)
                .elc(elc)
                .serialNumber(serialNumber)
                .timeFrom(EcpDateUtil.toDate(timeFrom, dateFormatJS))
                .timeTo(new Date())
                .group(group)
                .build();
        final EcpCallback<HashMap<String, String>> callback = new EcpCallback<HashMap<String, String>>() {

            @Override
            public final void onSuccess(HashMap<String, String> reportData) {
                callbackContext.success(gson.toJson(reportData));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getCommandsTotalAsync(callback, selection);
    }
    public final void getShareOfPropertyValuesAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        String applianceType = "", pnc = "", elc = "", eventName = "", timeFrom = "";
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) args.get(0);
                applianceType = arguments.getString("appliance_type");
                pnc = arguments.getString("pnc");
                elc = arguments.getString("elc");
                eventName = arguments.getString("event_name");
                timeFrom = arguments.getString("time_from");
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
        }
        final EcpShareOfPropertiesValuesSelection selection = new EcpShareOfPropertiesValuesSelection.Builder(applianceType)
                .eventName(eventName)
                .pnc(pnc)
                .elc(elc)
                .timeFrom(EcpDateUtil.toDate(timeFrom, dateFormatJS))
                .timeTo(new Date())
                .values(null)
                .build();
        final EcpCallback<HashMap<String, String>> callback = new EcpCallback<HashMap<String, String>>() {

            @Override
            public final void onSuccess(HashMap<String, String> reportData) {
                callbackContext.success(gson.toJson(reportData));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getShareOfPropertyValuesAsync(callback, selection);
    }
    public final void getCommandsAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)(args.get(0)));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            //(JSONObject)((JSONArray)((JSONArray) args.get(0))).get(0)
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<List<EcpReportCommand>> callback = new EcpCallback<List<EcpReportCommand>>() {
            @Override
            public final void onSuccess(List<EcpReportCommand> reportData) {
                callbackContext.success(gson.toJson(reportData));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getCommandsAsync(callback, applianceNumber);
    }
    public final void getHistoricEventsAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONArray events = new JSONArray();
        if (args.length() > 0) {
            try {
                events = (JSONArray) ((JSONArray)args.get(0)).get(1);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
        }
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
        final EcpCallback<List<EcpApplianceEvent>> callback = new EcpCallback<List<EcpApplianceEvent>>() {

            @Override
            public final void onSuccess(List<EcpApplianceEvent> ecpApplianceEvents) {
                callbackContext.success(gson.toJson(ecpApplianceEvents));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getHistoricEventsAsync(callback, applianceNumber, events.toString());
    }
    public final void getLatestAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)(((JSONArray) args.get(0))).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }

        final EcpCallback<List<EcpLatestApplianceState>> callback = new EcpCallback<List<EcpLatestApplianceState>>() {

            @Override
            public final void onSuccess(List<EcpLatestApplianceState> ecpLatestApplianceStates) {
                callbackContext.success(gson.toJson(ecpLatestApplianceStates));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getLatestAsync(callback, applianceNumber, null, false);
    }
    public final void getLatestAmountAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)(((JSONArray) args.get(0))).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }


        final List<String> states = new ArrayList<String>();
        callbackContext.success();
        final EcpCallback<List<EcpLatestAmountApplianceState>> callback = new EcpCallback<List<EcpLatestAmountApplianceState>>() {

            @Override
            public final void onSuccess(List<EcpLatestAmountApplianceState> ecpLatestAmountApplianceStates) {
                callbackContext.success(gson.toJson(ecpLatestAmountApplianceStates));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getLatestAmountAsync(callback, applianceNumber, states);
    }
    public final void getApplianceAlertsAsync(final JSONArray args, final CallbackContext callbackContext) {
        JSONObject arguments = new JSONObject();
        String timeFrom= "";
        if (args.length() > 0) {
            try {
                arguments = (JSONObject) ((JSONArray)args.get(0)).get(0);
                timeFrom = (String)((JSONArray)args.get(0)).get(1);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
        }
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs(arguments);
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        }

        final EcpCallback<List<EcpApplianceAlert>> callback = new EcpCallback<List<EcpApplianceAlert>>() {
            @Override
            public final void onSuccess(List<EcpApplianceAlert> reportData) {
                callbackContext.success(gson.toJson(reportData));
            }
            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getApplianceAlertsAsync(callback, new ArrayList<String>(), applianceNumber, EcpDateUtil.toDate(timeFrom, dateFormatJS), new Date());
    }
    public final void getMostUsedProgramsAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((JSONArray)args.get(0)).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<List<EcpMostUsedProgram>> callback = new EcpCallback<List<EcpMostUsedProgram>>() {

            @Override
            public final void onSuccess(List<EcpMostUsedProgram> mostUsedPrograms) {
                callbackContext.success(gson.toJson(mostUsedPrograms));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getMostUsedProgramsAsync(callback, applianceNumber, "", "");
    }
    public final void getWeeklyRunsAsync(final JSONArray args, final CallbackContext callbackContext) {
        final EcpApplianceNumber applianceNumber;
        try {
            applianceNumber = EcpConfigurationPlugin.createApplianceNumberFromArgs((JSONObject)((JSONArray)args.get(0)).get(0));
        } catch (EcpException e) {
            callbackContext.error(e.getMessage());
            return;
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return;
        }
        final EcpCallback<List<EcpWeeklyRuns>> callback = new EcpCallback<List<EcpWeeklyRuns>>() {

            @Override
            public final void onSuccess(List<EcpWeeklyRuns> weeklyRuns) {
                callbackContext.success(gson.toJson(weeklyRuns));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getWeeklyRunsAsync(callback, applianceNumber.getPnc(), applianceNumber.getElc(), applianceNumber.getSerialNo(), "", "");
    }
    public final void getUserAppliancesAsync(final JSONArray args, final CallbackContext callbackContext) {
        String userId= "";
        if (args.length() > 0) {
            try {
                userId = (String) ((JSONArray)args.get(0)).get(0);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        final EcpCallback<List<EcpAppliance>> callback = new EcpCallback<List<EcpAppliance>>() {

            @Override
            public final void onSuccess(List<EcpAppliance> ecpApplianceInfos) {
                callbackContext.success(gson.toJson(ecpApplianceInfos));
            }

            @Override
            public final void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.getUserAppliancesAsync(callback, userId);
    }
    public final void searchApplianceAsync(final JSONArray args, final CallbackContext callbackContext) {
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
        final EcpCallback<EcpApplianceSearchResponse> callback = new EcpCallback<EcpApplianceSearchResponse>() {
            @Override
            public void onSuccess(EcpApplianceSearchResponse ecpApplianceSearchResponse) {
                callbackContext.success(gson.toJson(ecpApplianceSearchResponse));
            }
            @Override
            public void onFailure(EcpError ecpError) {
                callbackContext.error(ecpError.getReason().getMessage());
            }
        };
        mManager.searchApplianceAsync(callback, applianceNumber, null, 1);
    }


}
