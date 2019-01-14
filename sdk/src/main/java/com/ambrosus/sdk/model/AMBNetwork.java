package com.ambrosus.sdk.model;

import com.ambrosus.sdk.EventsSearchParamsBuilder;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;

import java.util.List;

public class AMBNetwork extends Network {

    //TODO think about making converter public and converting from NetworkCall<List<AMBAssetInfo>> to NetworkCall<AMBAssetInfo>
    public NetworkCall<List<AMBAssetInfo>> getAssetInfo(String assetID){
        NetworkCall<List<AMBAssetInfo>> networkCall = findEvents(
                new EventsSearchParamsBuilder()
                        .forAsset(assetID)
                        .build(),
                new AssetInfoFactory()
        );
        return networkCall;
    }

}
