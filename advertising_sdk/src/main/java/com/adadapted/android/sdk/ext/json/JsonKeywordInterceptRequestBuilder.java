package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonKeywordInterceptRequestBuilder {
    private static final String TAG = JsonKeywordInterceptRequestBuilder.class.getName();

    private static final String SESSION_ID = "session_id";
    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String DATETIME = "datetime";
    private static final String SDK_VERSION = "sdk_version";

    public JSONObject buildInitRequest(final Session session) {
        final JSONObject json = new JSONObject();
        final DeviceInfo deviceInfo = session.getDeviceInfo();

        try {
            json.put(SESSION_ID, session.getId());
            json.put(APP_ID, deviceInfo.getAppId());
            json.put(UDID, deviceInfo.getUdid());
            json.put(DATETIME, new Date().getTime());
            json.put(SDK_VERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
