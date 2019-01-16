package com.ambrosus.sdk.model;

import android.provider.CalendarContract;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.NetworkResultAdapter;
import com.ambrosus.sdk.SearchResult;

import java.util.ArrayList;
import java.util.List;

class AssetInfoAdapter implements NetworkResultAdapter<SearchResult<Event>, List<AMBAssetInfo>> {

    @Override
    public List<AMBAssetInfo> convert(SearchResult<Event> source) {

        List<AMBAssetInfo> result = new ArrayList<>(1);
        for (Event event : source.getValues()) {
            if(isValidSourceEvent(event))
                result.add(new AMBAssetInfo(event));
        }
        return result;
    }

    private static boolean isValidSourceEvent(Event event) {
        return AMBAssetInfo.isValidSourceEvent(event);
    }
}
