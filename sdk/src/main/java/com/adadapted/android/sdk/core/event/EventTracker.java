package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.event.model.EventTypes;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class EventTracker {
    private static final String LOGTAG = EventTracker.class.getName();

    private static final int MAX_QUEUE_SIZE = 10;
    private static final int MAX_FAILED_RETRIES = 2;

    public static final String EVENTNAME_EMPTY = "";
    public static final String EVENTNAME_PAYLOAD_DELIVERED = "";

    private final EventAdapter mEventAdapter;
    private final EventRequestBuilder mBuilder;
    private final Set<JSONObject> mQueuedEvents;

    private int mFailedRetries;

    private final EventAdapterListener mEventAdapterListener = new EventAdapterListener() {
        @Override
        public void onSuccess() {
            mFailedRetries = 0;
        }

        @Override
        public void onFailure(final JSONArray json) {
            mFailedRetries++;
            sendBatchRetry(json);
        }
    };

    public EventTracker(final EventAdapter eventAdapter,
                        final EventRequestBuilder builder) {
        mEventAdapter = eventAdapter;
        mBuilder = builder;

        mQueuedEvents = new HashSet<>();

        mFailedRetries = 0;
    }

    public Set<JSONObject> getQueuedEvents() {
        return mQueuedEvents;
    }

    public void publishEvents() {
        if(!mQueuedEvents.isEmpty()) {
            final Set<JSONObject> currentEvents = new HashSet<>(getQueuedEvents());
            mQueuedEvents.clear();

            final JSONArray eventsArray = new JSONArray(currentEvents);
            mEventAdapter.sendBatch(eventsArray, mEventAdapterListener);
        }
    }

    private void sendBatchRetry(final JSONArray json) {
        if(mFailedRetries <= MAX_FAILED_RETRIES) {
            mEventAdapter.sendBatch(json, mEventAdapterListener);
        }
        else {
            Log.w(LOGTAG, "Maximum failed retries. No longer sending batch retries.");
        }
    }

    public void trackImpressionBeginEvent(final Session session, final Ad ad) {
        if(session == null || ad == null) {
            return;
        }

        ad.incrementImpressionViews();
        trackEvent(session, ad, EventTypes.IMPRESSION, EVENTNAME_EMPTY);
    }

    public void trackImpressionEndEvent(final Session session, final Ad ad) {
        if(session == null || ad == null) {
            return;
        }

        trackEvent(session, ad, EventTypes.IMPRESSION_END, EVENTNAME_EMPTY);
    }


    public void trackInteractionEvent(final Session session, final Ad ad) {
        if(session == null || ad == null) {
            return;
        }

        trackEvent(session, ad, EventTypes.INTERACTION, EVENTNAME_EMPTY);
    }

    public void trackPopupBeginEvent(final Session session, final Ad ad) {
        if(session == null || ad == null) {
            return;
        }

        trackEvent(session, ad, EventTypes.POPUP_BEGIN, EVENTNAME_EMPTY);
    }

    public void trackPopupEndEvent(final Session session, final Ad ad) {
        if(session == null || ad == null) {
            return;
        }

        trackEvent(session, ad, EventTypes.POPUP_END, EVENTNAME_EMPTY);
    }

    public void trackCustomEvent(final Session session, final Ad ad, final String eventName) {
        if(session == null || ad == null || eventName == null) {
            return;
        }

        trackEvent(session, ad, EventTypes.CUSTOM, eventName);
    }

    private void trackEvent(final Session session,
                            final Ad ad,
                            final EventTypes eventType,
                            final String eventName) {
        mQueuedEvents.add(mBuilder.build(session, ad, eventType, eventName));

        if(mQueuedEvents.size() >= MAX_QUEUE_SIZE) {
            publishEvents();
        }
    }

    @Override
    public String toString() {
        return "EventTracker{" +
                "eventAdapter=" + mEventAdapter +
                ", builder=" + mBuilder +
                ", queuedEvents=" + mQueuedEvents +
                '}';
    }
}
