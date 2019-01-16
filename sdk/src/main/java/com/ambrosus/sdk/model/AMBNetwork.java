package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.EventSearchParamsBuilder;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;

import java.util.List;

public class AMBNetwork extends Network {

    //TODO think about making converter public and converting from NetworkCall<List<AMBAssetInfo>> to NetworkCall<AMBAssetInfo>
    @NonNull
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(String assetID){
        NetworkCall<List<AMBAssetInfo>> networkCall = findEvents(
                new EventSearchParamsBuilder()
                        .forAsset(assetID)
                        .byDataObjectType(AMBAssetInfo.DATA_OBJECT_TYPE_ASSET_INFO)
                        .build(),
                new AssetInfoFactory()
        );
        return networkCall;
    }

    @NonNull
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(Identifier identifier){
        NetworkCall<List<AMBAssetInfo>> networkCall = findEvents(
                new AMBEventSearchParamsBuilder()
                        .byDataObjectType(AMBAssetInfo.DATA_OBJECT_TYPE_ASSET_INFO)
                        .byDataObjectIdentifier(identifier)
                        .build(),
                new AssetInfoFactory()
        );
        return networkCall;
    }

}
