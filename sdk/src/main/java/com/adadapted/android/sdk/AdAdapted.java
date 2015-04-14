package com.adadapted.android.sdk;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class AdAdapted implements DeviceInfoBuilder.Listener, SessionManager.Listener {
    private static final String TAG = "AdAdapted";

    private static AdAdapted instance;

    interface Listener {
        void onSessionLoaded(Session session);
    }

    private final Set<Listener> listeners;

    private final  Context context;

    private DeviceInfo deviceInfo;

    private Session session;
    private boolean sessionLoaded;

    private SessionManager sessionManager;
    private EventTracker eventTracker;

    private final String sessionInitUrl;
    private final String sessionReinitUrl;
    private final String eventBatchUrl;

    private final String appId;
    private final String[] zones;
    private final String sdkVersion;

    private AdAdapted(Context context, String appId, String[] zones, boolean prodMode) {
        this.listeners = new HashSet<>();

        this.context = context;

        this.session = null;
        this.sessionLoaded = false;

        if(prodMode) {
            this.sessionInitUrl = context.getString(R.string.prod_session_init_object_url);
            this.sessionReinitUrl = context.getString(R.string.prod_session_reinit_object_url);
            this.eventBatchUrl = context.getString(R.string.prod_event_batch_object_url);
        }
        else {
            this.sessionInitUrl = context.getString(R.string.sandbox_session_init_object_url);
            this.sessionReinitUrl = context.getString(R.string.sandbox_session_reinit_object_url);
            this.eventBatchUrl = context.getString(R.string.sandbox_event_batch_object_url);
        }

        this.appId = appId;
        this.zones = zones;
        this.sdkVersion = context.getString(R.string.sdk_version);

        ImageCache.getInstance().purgeCache();
    }

    static synchronized AdAdapted getInstance() {
        return instance;
    }

    public static synchronized void init(Context context, String appId, String[] zones, boolean prodMode) {
        Log.d(TAG, "init() called for appId: " + appId + " and zones: " + Arrays.toString(zones));
        instance = new AdAdapted(context, appId, zones, prodMode);

        getInstance().initialize();
    }

    private void initialize() {
        DeviceInfoBuilder builder = new DeviceInfoBuilder();
        builder.addListener(this);
        builder.execute(new BuildDeviceInfoParam(context, appId, zones));
    }

    Context getContext() {
        return context;
    }

    DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    Session getSession() {
        return session;
    }

    private boolean sessionIsLoaded() {
        return sessionLoaded;
    }

    private SessionManager getSessionManager() {
        if(sessionManager == null) {
            this.sessionManager = new SessionManager(new HttpSessionAdapter(sessionInitUrl, sessionReinitUrl));
            this.sessionManager.addListener(this);
        }

        return sessionManager;
    }

    EventTracker getEventTracker() {
        if(eventTracker == null) {
            this.eventTracker = new EventTracker(new HttpEventAdapter(eventBatchUrl));
        }

        return eventTracker;
    }

    public void addListener(AdAdapted.Listener listener) {
        listeners.add(listener);

        if(sessionIsLoaded()) {
            notifySessionLoaded();
        }
    }

    public void removeListener(AdAdapted.Listener listener) {
        listeners.remove(listener);
    }

    private void notifySessionLoaded() {
        for(Listener listener : listeners) {
            listener.onSessionLoaded(session);
        }
    }

    @Override
    public void onDeviceInfoCollected(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;

        getSessionManager().initialize(deviceInfo);
    }

    public void onSessionInitialized(Session session) {
        this.session = session;
        this.sessionLoaded = true;

        notifySessionLoaded();
    }

    @Override
    public String toString() {
        return "AdAdapted{" +
                "listeners=" + listeners +
                ", context=" + context +
                ", deviceInfo=" + deviceInfo +
                ", session=" + session +
                ", sessionManager=" + sessionManager +
                ", eventTracker=" + eventTracker +
                ", sessionInitUrl='" + sessionInitUrl + '\'' +
                ", sessionReinitUrl='" + sessionReinitUrl + '\'' +
                ", eventBatchUrl='" + eventBatchUrl + '\'' +
                ", appId='" + appId + '\'' +
                ", zones=" + Arrays.toString(zones) +
                '}';
    }
}
