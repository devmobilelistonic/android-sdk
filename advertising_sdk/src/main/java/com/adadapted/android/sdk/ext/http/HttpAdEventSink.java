package com.adadapted.android.sdk.ext.http;

import com.adadapted.android.sdk.core.ad.AdEvent;
import com.adadapted.android.sdk.core.ad.AdEventSink;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.ext.json.JsonAdEventBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpAdEventSink implements AdEventSink {
    private static final String LOGTAG = HttpAdEventSink.class.getName();

    private final String batchUrl;
    private final JsonAdEventBuilder builder;

    public HttpAdEventSink(final String batchUrl) {
        this.batchUrl = batchUrl == null ? "" : batchUrl;
        builder = new JsonAdEventBuilder();
    }

    @Override
    public void sendBatch(final Set<AdEvent> events) {
        final JSONArray json = builder.buildEvents(events);

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,
                batchUrl, json, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String reason = "";
                if(error != null && error.networkResponse != null) {
                    final int statusCode = error.networkResponse.statusCode;
                    final String data = new String(error.networkResponse.data);

                    reason = statusCode + " - " + data;
                }

                final Map<String, String> params = new HashMap<>();
                params.put("url", batchUrl);
                AppEventClient.trackError(
                    "AD_EVENT_TRACK_REQUEST_FAILED",
                    reason,
                    params
                );
            }

        });

        HttpRequestManager.queueRequest(jsonRequest);
    }
}
