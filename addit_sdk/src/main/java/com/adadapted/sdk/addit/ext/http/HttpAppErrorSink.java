package com.adadapted.sdk.addit.ext.http;

import android.util.Log;

import com.adadapted.sdk.addit.core.app.AppErrorSink;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class HttpAppErrorSink implements AppErrorSink {
    private static final String LOGTAG = HttpAppErrorSink.class.getName();

    private final String endpoint;

    public HttpAppErrorSink(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void publishError(final JSONObject json) {
        if(json == null) {
            return;
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                endpoint, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "App Error Request Failed.", error);
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 20, 2, 1.0f));

        HttpRequestManager.getQueue().add(jsonRequest);
    }
}
