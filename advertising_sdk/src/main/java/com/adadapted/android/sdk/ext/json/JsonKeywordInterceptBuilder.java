package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonKeywordInterceptBuilder {
    private static final String TAG = JsonKeywordInterceptBuilder.class.getName();

    private static final String SEARCH_ID = "search_id";
    private static final String REFRESH_TIME = "refresh_time";
    private static final String MIN_MATCH_LENGTH = "min_match_length";
    private static final String TERM_ID = "term_id";
    private static final String TERM = "term";
    private static final String AUTO_FILL = "autofill";
    private static final String REPLACEMENT = "replacement";
    private static final String ICON = "icon";
    private static final String TAG_LINE = "tagline";
    private static final String PRIORITY = "priority";

    public KeywordIntercept build(final JSONObject json) {
        if(json == null) { return KeywordIntercept.empty(); }

        try {
            final String searchId = json.has(SEARCH_ID) ? json.getString(SEARCH_ID) : "";
            final long refreshTime = json.has(REFRESH_TIME) ? Long.parseLong(json.getString(REFRESH_TIME)) : 0L;
            final int minMatchLength = json.has(MIN_MATCH_LENGTH) ? Integer.parseInt(json.getString(MIN_MATCH_LENGTH)) : 2;

            final List<AutoFill> intercepts = sortAutoFills(parseAutoFills(json));

            return new KeywordIntercept(searchId, refreshTime, minMatchLength, intercepts);
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);

            final Map<String, String> params = new HashMap<>();
            params.put("error", ex.getMessage());
            params.put("payload", json.toString());
            AppEventClient.trackError(
                "KI_PAYLOAD_PARSE_FAILED",
                "Failed to parse KI payload for processing.",
                params
            );
        }

        return KeywordIntercept.empty();
    }

    private List<AutoFill> parseAutoFills(final JSONObject json) throws JSONException {
        final List<AutoFill> autoFills = new ArrayList<>();

        final Object obj = json.get(AUTO_FILL);
        if(obj instanceof JSONObject) {
            final JSONObject autoFillJson = (JSONObject)obj;

            for(final Iterator<String> z = autoFillJson.keys(); z.hasNext();) {
                final String term = z.next();
                final JSONObject jsonTerm = autoFillJson.getJSONObject(term);

                final String termId = jsonTerm.has(TERM_ID) ?  jsonTerm.getString(TERM_ID) : "";
                final String replacement = jsonTerm.has(REPLACEMENT) ? jsonTerm.getString(REPLACEMENT) : "";
                final String icon = jsonTerm.has(ICON) ? jsonTerm.getString(ICON) : "";
                final String tagLine = jsonTerm.has(TAG_LINE) ? jsonTerm.getString(TAG_LINE) : "";
                final int priority = jsonTerm.has(PRIORITY) ? jsonTerm.getInt(PRIORITY) : 0;

                autoFills.add(new AutoFill(termId, term, replacement, icon, tagLine, priority));
            }
        }

        return autoFills;
    }

    private List<AutoFill> sortAutoFills(final List<AutoFill> autoFills) {
        final AutoFill[] arr = new AutoFill[autoFills.size()];
        autoFills.toArray(arr);
        final int size = arr.length;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (arr[i].compareTo(arr[j]) < 1) {
                    final AutoFill temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        return Arrays.asList(arr);
    }
}
