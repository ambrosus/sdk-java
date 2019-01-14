package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.EventsSearchParamsBuilder;

import org.junit.Test;

import java.util.List;

public class AssetInfoIntegrationTest {

    @Test
    public void testAssetInfoIntegration(){
        Network network = new Network();
        NetworkCall<List<AMBAssetInfo>> networkCall = network.findEvents(
                new EventsSearchParamsBuilder()
                        .forAsset("0xf819897e45f102dcf2197e33d9d81c3c31354e36445ecb0eac1658e5b6a07c3e")
                        .build(),
                new AssetInfoFactory()
        );

        try {
            List<AMBAssetInfo> assetInfoList = networkCall.execute();
            AMBAssetInfo assetInfo = assetInfoList.get(0);
            System.out.println(assetInfo.getIdentifiers());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
