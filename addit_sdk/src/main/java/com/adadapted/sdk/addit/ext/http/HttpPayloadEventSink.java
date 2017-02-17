package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.payload.PayloadEventSink;
import com.adadapted.sdk.addit.ext.management.AppErrorTrackingManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 2/9/17.
 */
public class HttpPayloadEventSink implements PayloadEventSink {
    private static final String LOGTAG = HttpPayloadEventSink.class.getName();

    private final String endpoint;

    public HttpPayloadEventSink(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void publishEvent(JSONObject payloadEvent) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, payloadEvent, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Payload Event Request Failed.", error);

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("endpoint", endpoint);
                errorParams.put("exception", error.getClass().getName());

                String errorType = "PAYLOAD_EVENT_REQUEST_FAILED";
                if(error instanceof NoConnectionError || error instanceof NetworkError) {
                    errorType = "PAYLOAD_EVENT_NO_NETWORK_CONNECTION";
                }

                AppErrorTrackingManager.registerEvent(
                        errorType,
                        error.getMessage(),
                        errorParams);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(1000 * 20, 2, 1.0f));

        HttpRequestManager.getQueue().add(request);
    }
}
