package com.ambrosus.sdk.model;

import com.ambrosus.sdk.NetworkCall;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssetInfoIntegrationTest {

    @Test
    public void testAssetInfoIntegration(){
        AMBNetwork network = new AMBNetwork();

        NetworkCall<List<AMBAssetInfo>> networkCall = network.getAssetInfo(
                //searching for "PURE DARK CHOCOLATE BAR 92%"
                "0x602023f73ab25f0c95a3cf4e92c9cb2f4c9c09dbd3ca6e167d362de6e7f1eeae"
        );

        try {
            List<AMBAssetInfo> assetInfoList = networkCall.execute();
            AMBAssetInfo assetInfo = assetInfoList.get(0);
            assertEquals("PURE DARK CHOCOLATE BAR 92%", assetInfo.getName());
            assertEquals(1496250888000L, assetInfo.getGMTTimeStamp());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
