package com.adadapted.android.sdk.ext.http;

import android.util.Log;

import com.adadapted.android.sdk.core.anomaly.AnomalyAdapter;
import com.adadapted.android.sdk.core.anomaly.AnomalyAdapterListener;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class HttpAnomalyAdapter implements AnomalyAdapter {
    private static final String LOGTAG = HttpAnomalyAdapter.class.getName();

    private final String mBatchUrl;

    public HttpAnomalyAdapter(String batchUrl) {
        this.mBatchUrl = batchUrl;
    }

    @Override
    public void sendBatch(final JSONArray json,
                          final AnomalyAdapterListener listener) {
        if(json == null || listener == null) {
            return;
        }
        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                mBatchUrl,
                new Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(LOGTAG, "Anomaly Track Request Succeeded: " + json.toString());
                        listener.onSuccess();
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGTAG, "Anomaly Track Request Failed.", error);
                listener.onFailure(json);
            }
        }){
            @Override
            public byte[] getBody() {
                return json.toString().getBytes();
            }
        };

        HttpRequestManager.getQueue().add(stringRequest);
    }
}
