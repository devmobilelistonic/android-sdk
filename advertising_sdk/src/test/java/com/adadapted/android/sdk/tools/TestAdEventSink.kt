package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventSink
import com.adadapted.android.sdk.core.session.Session

open class TestAdEventSink: AdEventSink {
    override fun sendBatch(session: Session?, events: Set<AdEvent?>?) {
        //do nothing
    }
}