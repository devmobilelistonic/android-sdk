package com.adadapted.android.sdk.config;

public class Config {
    public static final String SDK_VERSION = "1.1.10";
    public static final long DEFAULT_AD_POLLING = 300000L;
    public static final long DEFAULT_EVENT_POLLING = 5000L;
    public static final long DEFAULT_AD_REFRESH = 60L;

    public static class Prod {
        public static final String URL_SESSION_INIT = "https://ads.adadapted.com/v/0.9.4/android/session/init";
        public static final String URL_EVENT_BATCH = "https://ads.adadapted.com/v/0.9.4/android/event/batch";
        public static final String URL_AD_GET = "https://ads.adadapted.com/v/0.9.4/android/ad/get";
        public static final String URL_KI_INIT = "https://ads.adadapted.com/v/0.9.4/android/ki/init";
        public static final String URL_KI_TRACK = "https://ads.adadapted.com/v/0.9.4/android/ki/track";

        public static final String URL_APP_EVENT_TRACK = "https://ec.adadapted.com/v/1/android/events";
        public static final String URL_APP_ERROR_TRACK = "https://ec.adadapted.com/v/1/android/errors";

        public static final String URL_APP_PAYLOAD_PICKUP = "https://payload.adadapted.com/v/1/pickup";
        public static final String URL_APP_PAYLOAD_TRACK = "https://payload.adadapted.com/v/1/tracking";
    }

    public static class Sand {
        public static final String URL_SESSION_INIT = "https://sandbox.adadapted.com/v/0.9.4/android/session/init";
        public static final String URL_EVENT_BATCH = "https://sandbox.adadapted.com/v/0.9.4/android/event/batch";
        public static final String URL_AD_GET = "https://sandbox.adadapted.com/v/0.9.4/android/ad/get";
        public static final String URL_KI_INIT = "https://sandbox.adadapted.com/v/0.9.4/android/ki/init";
        public static final String URL_KI_TRACK = "https://sandbox.adadapted.com/v/0.9.4/android/ki/track";

        public static final String URL_APP_EVENT_TRACK = "https://sandec.adadapted.com/v/1/android/events";
        public static final String URL_APP_ERROR_TRACK = "https://sandec.adadapted.com/v/1/android/errors";

        public static final String URL_APP_PAYLOAD_PICKUP = "https://sandpayload.adadapted.com/v/1/pickup";
        public static final String URL_APP_PAYLOAD_TRACK = "https://sandpayload.adadapted.com/v/1/tracking";
    }
}
