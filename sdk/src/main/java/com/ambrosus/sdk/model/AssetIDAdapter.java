package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.NetworkResultAdapter;
import com.ambrosus.sdk.SearchResult;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AssetIDAdapter implements NetworkResultAdapter<SearchResult<Event>, List<String>> {

    @Override
    public List<String> convert(SearchResult<Event> source) {
        Set<String> resultSet = new LinkedHashSet<>();
        for (Event event : source.getValues()) {
            resultSet.add(event.getAssetId());
        }
        List<String> resultList = new ArrayList<>(resultSet.size());
        resultList.addAll(resultSet);
        return resultList;
    }
}
