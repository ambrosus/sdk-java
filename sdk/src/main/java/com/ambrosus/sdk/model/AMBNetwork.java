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
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(String assetID){
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
