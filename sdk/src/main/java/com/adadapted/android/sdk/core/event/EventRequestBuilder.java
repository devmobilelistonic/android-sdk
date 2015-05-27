package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.model.EventTypes;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface EventRequestBuilder {
    JSONObject build(DeviceInfo deviceInfo,
                     String sessionId,
                     Ad ad,
                     EventTypes eventType,
                     String eventName);
}
