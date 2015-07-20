package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.keywordintercept.model.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class AaKeyworkInterceptMatcher implements SessionManager.Listener, KeywordInterceptManager.Listener {
    private static final String TAG = AaKeyworkInterceptMatcher.class.getName();

    private final KeywordInterceptManager manager;
    private final AaSuggestionTracker suggestionTracker;
    
    private KeywordIntercept keywordIntercept;
    private boolean loaded = false;
    private Session session;

    public AaKeyworkInterceptMatcher(Context context) {
        manager = KeywordInterceptManagerFactory.getInstance(context).createKeywordInterceptManager();
        manager.setListener(this);

        suggestionTracker = new AaSuggestionTracker(manager);

        SessionManagerFactory.getInstance(context).createSessionManager().addListener(this);
    }

    public SuggestionPayload match(CharSequence constraint) {
        Set<String> suggestions = new HashSet<>();

        if((constraint != null && constraint.length() >= 3) && isLoaded()) {
            for (String item : keywordIntercept.getAutofill().keySet()) {
                if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    AutoFill autofill = keywordIntercept.getAutofill().get(item);
                    suggestions.add(autofill.getReplacement());

                    suggestionTracker.suggestionMatched(session, item, autofill.getReplacement(), constraint.toString());
                }
            }
        }

        return new SuggestionPayload(suggestionTracker, suggestions);
    }

    private boolean isLoaded() {
        return loaded;
    }

    @Override
    public void onKeywordInterceptInitSuccess(KeywordIntercept keywordIntercept) {
        this.keywordIntercept = keywordIntercept;
        this.loaded = true;
    }

    @Override
    public void onSessionInitialized(Session session) {
        this.session = session;
        manager.init(session, AdAdapted.getInstance().getDeviceInfo());
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onSessionNotReinitialized() {}
}
