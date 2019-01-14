package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventFactory;

import java.util.ArrayList;
import java.util.List;

public class AssetInfoFactory implements EventFactory<AMBAssetInfo> {

    @NonNull
    @Override
    public List<AMBAssetInfo> processEvents(List<Event> sourceEvents) {
        List<AMBAssetInfo> result = new ArrayList<>(1);
        for (Event event : sourceEvents) {
            if(isValidSourceEvent(event))
                result.add(new AMBAssetInfo(event));
        }
        return result;
    }

    @Override
    public boolean isValidSourceEvent(Event event) {
        return AMBAssetInfo.isValidSourceEvent(event);
    }
}
