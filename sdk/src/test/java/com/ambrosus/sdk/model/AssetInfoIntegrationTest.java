package com.ambrosus.sdk.model;

import com.ambrosus.sdk.AMBNetwork;
import com.ambrosus.sdk.AMBNetworkCall;
import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventsSearchParamsBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class AssetInfoIntegrationTest {

    @Test
    public void testAssetInfoIntegration(){
        AMBNetwork ambNetwork = new AMBNetwork();
        AMBNetworkCall<List<AMBAssetInfo>> networkCall = ambNetwork.findEvents(
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
