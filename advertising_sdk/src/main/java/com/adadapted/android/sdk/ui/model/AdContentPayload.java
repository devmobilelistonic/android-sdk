package com.adadapted.android.sdk.ui.model;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.core.event.AppEventClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdContentPayload implements ContentPayload {
    private static final String LOGTAG = AdContentPayload.class.getName();

    public static final int ADD_TO_LIST = 0;
    public static final int RECIPE_FAVORITE = 1;

    public static final String FIELD_ADD_TO_LIST_ITEMS = "add_to_list_items";

    private final Ad mAd;
    private final int mType;
    private final JSONObject mPayload;

    private boolean mHandled;

    private AdContentPayload(final Ad ad,
                             final int type,
                             final JSONObject payload) {
        mAd = ad;
        mType = type;
        mPayload = payload;

        mHandled = false;
    }

    public static AdContentPayload createAddToListContent(final Ad ad) {
        final List items = new ArrayList(); //((ContentAdAction)ad.getAd().getAdAction()).getItems();
        final JSONObject json = new JSONObject();

        try {
            json.put(FIELD_ADD_TO_LIST_ITEMS, new JSONArray(items));
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing JSON");
        }

        return new AdContentPayload(ad, ADD_TO_LIST, json);
    }

    public static AdContentPayload createRecipeFavoriteContent(final Ad ad) {
        return new AdContentPayload(ad, RECIPE_FAVORITE, new JSONObject());
    }

    public synchronized void acknowledge() {
        if(mHandled) {
            Log.w(LOGTAG, "Content Payload acknowledged multiple times.");
            return;
        }

        mHandled = true;
        Log.d(LOGTAG, "Content Payload acknowledged.");

        try {
            final JSONArray array = getPayload().getJSONArray("add_to_list_items");
            for (int i = 0; i < array.length(); i++) {
                trackItem(mAd.getId(), array.getString(i));
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing JSON");
        }

        AdEventClient.trackInteraction(mAd);
    }

    private void trackItem(final String adId, final String itemName) {
        final Map<String, String> params = new HashMap<>();
        params.put("ad_id", adId);
        params.put("item_name", itemName);

        AppEventClient.trackSdkEvent("atl_added_to_list", params);
    }

    public String getZoneId() {
        return mAd.getZoneId();
    }

    public int getType() {
        return mType;
    }

    public JSONObject getPayload() {
        return mPayload;
    }

    @Override
    public String toString() {
        return "AdContentPayload";
    }
}
