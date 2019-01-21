/*
 * Copyright: Ambrosus Technologies GmbH
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

import com.ambrosus.sdk.Asset;
import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventSearchParams;
import com.ambrosus.sdk.EventSearchParamsBuilder;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.NetworkCallAdapter;
import com.ambrosus.sdk.SearchResult;

import java.util.List;

public class AMBNetwork extends Network {

    @NonNull
    public NetworkCall<List<AMBEvent>> findAMBEvents(EventSearchParams searchParams) {
        NetworkCall<SearchResult<Event>> networkCall = findEvents(searchParams);
        return new NetworkCallAdapter<>(networkCall, new EventAdapter());

    }

    //TODO think about making converter public and converting from NetworkCall<List<AMBAssetInfo>> to NetworkCall<AMBAssetInfo>
    @NonNull
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(@NonNull String assetID){
        NetworkCall<SearchResult<Event>> networkCall = findEvents(
                new EventSearchParamsBuilder()
                        .forAsset(assetID)
                        .byDataObjectType(AMBAssetInfo.DATA_OBJECT_TYPE_ASSET_INFO)
                        .build()
        );
        return new NetworkCallAdapter<>(networkCall, new AssetInfoAdapter());
    }

    @NonNull
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(Identifier identifier){
        NetworkCall<SearchResult<Event>> networkCall = findEvents(
                new AMBEventSearchParamsBuilder()
                        .byDataObjectType(AMBAssetInfo.DATA_OBJECT_TYPE_ASSET_INFO)
                        .byDataObjectIdentifier(identifier)
                        .build()
        );
        return new NetworkCallAdapter<>(networkCall, new AssetInfoAdapter());
    }

    @NonNull
    public NetworkCall<List<String>> getAssetIDs(Identifier identifier){
        NetworkCall<SearchResult<Event>> networkCall = findEvents(
                new AMBEventSearchParamsBuilder()
                        .byDataObjectType(Identifier.DATA_OBJECT_TYPE_ASSET_IDENTIFIERS)
                        .byDataObjectIdentifier(identifier)
                        .build()
        );
        return new NetworkCallAdapter<>(networkCall, new AssetIDAdapter());
    }
}
