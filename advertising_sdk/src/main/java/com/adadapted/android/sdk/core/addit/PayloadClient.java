package com.adadapted.android.sdk.core.addit;

import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoClient;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PayloadClient {
    @SuppressWarnings("unused")
    private static final String LOGTAG = PayloadClient.class.getName();

    public interface Callback {
        void onPayloadAvailable(List<Content> content);
    }

    private static PayloadClient instance;

    private static boolean deeplinkInProgress = false;
    private static final Lock lock = new ReentrantLock();

    public static synchronized void createInstance(final PayloadAdapter adapter) {
        if(instance == null) {
            instance = new PayloadClient(adapter);
        }
    }

    private static synchronized PayloadClient getInstance() {
        return instance;
    }

    public static synchronized void pickupPayloads(final Callback callback) {
        if(instance == null || deeplinkInProgress) {
            return;
        }

        lock.lock();
        try {
            if(deeplinkInProgress) {
                return;
            }
        }
        finally {
            lock.unlock();
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performPickupPayload(callback);
            }
        });
    }

    public static synchronized void deeplinkInProgress() {
        lock.lock();
        try {
            deeplinkInProgress = true;
        }
        finally {
            lock.unlock();
        }
    }

    public static synchronized void deeplinkCompleted() {
        lock.lock();
        try {
            deeplinkInProgress = false;
        }
        finally {
            lock.unlock();
        }
    }

    static synchronized void markContentAcknowledged(final Content content) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final List<AddToListItem> payload = content.getPayload();
                for (AddToListItem item : payload) {
                    final Map<String, String> eventParams = new HashMap<>();
                    eventParams.put("payload_id", content.getPayloadId());
                    eventParams.put("tracking_id", item.getTrackingId());
                    eventParams.put("item_name", item.getTitle());
                    eventParams.put("source", content.getSource());

                    AppEventClient.trackSdkEvent("addit_added_to_list", eventParams);
                }

                if(content.isPayloadSource()) {
                    getInstance().trackPayload(content, "delivered");
                }
            }
        });
    }

    static synchronized void markContentDuplicate(final Content content) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());

                AppEventClient.trackSdkEvent("addit_duplicate_payload", eventParams);

                if(content.isPayloadSource()) {
                    getInstance().trackPayload(content, "duplicate");
                }
            }
        });
    }

    static synchronized void markContentFailed(final Content content,
                                               final String message) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());

                AppEventClient.trackError("ADDIT_CONTENT_FAILED", message, eventParams);

                if(content.isPayloadSource()) {
                    getInstance().trackPayload(content, "rejected");
                }
            }
        });
    }

    private final PayloadAdapter adapter;

    private PayloadClient(final PayloadAdapter adapter) {
        this.adapter = adapter;
    }

    private void performPickupPayload(final Callback callback) {
        DeviceInfoClient.getDeviceInfo(new DeviceInfoClient.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                AppEventClient.trackSdkEvent("payload_pickup_attempt");

                adapter.pickup(deviceInfo, new PayloadAdapter.Callback() {
                    @Override
                    public void onSuccess(List<Content> content) {
                        callback.onPayloadAvailable(content);
                    }
                });
            }
        });
    }

    private void trackPayload(final Content content,
                              final String result) {
        final PayloadEvent event = new PayloadEvent(content.getPayloadId(), result);
        adapter.publishEvent(event);
    }
}
