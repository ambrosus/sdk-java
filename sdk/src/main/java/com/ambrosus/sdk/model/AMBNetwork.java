/*
 * Copyright: Ambrosus Inc.
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.Entity;
import com.ambrosus.sdk.EntityNotFoundException;
import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.NetworkCallAdapter;
import com.ambrosus.sdk.DataConverter;
import com.ambrosus.sdk.Query;
import com.ambrosus.sdk.SearchRequestAdapter;
import com.ambrosus.sdk.SearchResult;

import java.util.List;

public class AMBNetwork extends Network {

    public NetworkCall<SearchResult<AMBEvent>> findAMBEvents(Query<? extends AMBEvent> query) {
        return findAMBEvents(query, true);
    }

    //TODO limit search for AMB events there by setting appropriate type (data[type]=pattern(ambrosus.event.*))
    @NonNull
    public NetworkCall<SearchResult<AMBEvent>> findAMBEvents(Query<? extends AMBEvent> query, boolean ignoreRestrictedEvents) {
        NetworkCall<SearchResult<Event>> networkCall = findEvents(query);
        return new SearchRequestAdapter<>(networkCall, AMBEvent.class, new EventAdapter(ignoreRestrictedEvents));
    }

    @NonNull
    public NetworkCall<AMBAssetInfo> getAssetInfo(@NonNull String assetID){
        Query<AMBAssetInfo> query = new AssetInfoQueryBuilder()
                .forAsset(assetID)
                .build();

        NetworkCall<SearchResult<AMBAssetInfo>> assetInfoSearchRequest = findAssetInfo(query, false);

        return new NetworkCallAdapter<>(assetInfoSearchRequest, new DataConverter<SearchResult<AMBAssetInfo>, AMBAssetInfo>() {
            @Override
            public AMBAssetInfo convert(SearchResult<AMBAssetInfo> source) throws Throwable {
                List<AMBAssetInfo> resultsList = source.getItems();
                if(resultsList.isEmpty())
                    throw new EntityNotFoundException("Cant find AssetInfo for asset: " + assetID);
                if(resultsList.size() > 1)
                    throw new IllegalArgumentException("There are several asset info objects for assetID: " + assetID + ". Please use findAssetInfo(Query<AMBAssetInfo>) to get list of results.");
                return resultsList.get(0);
            }
        });
    }

    public NetworkCall<SearchResult<AMBAssetInfo>> findAssetInfo(Query<AMBAssetInfo> query) {
        return findAssetInfo(query, true);
    }

    public NetworkCall<SearchResult<AMBAssetInfo>> findAssetInfo(Query<AMBAssetInfo> query, boolean ignoreRestrictedEvents) {
        return new SearchRequestAdapter<>(findEvents(query ), query.resultType, new AssetInfoAdapter(ignoreRestrictedEvents));
    }

    @Override
    public NetworkCall<SearchResult<? extends Entity>> find(Query query) {
        if(AMBAssetInfo.class.isAssignableFrom(query.resultType))
            return findAssetInfo(query, true);
        else if(AMBEvent.class.isAssignableFrom(query.resultType))
            return findAMBEvents(query, true);
        else
            return super.find(query);
    }
}
