package com.adadapted.android.sdk;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/26/15.
 */
interface EventAdapter {
    interface Listener {
        void onEventsPublished();
    }

    void sendBatch(JSONArray events);

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifyEventsPublished();
}
