package com.ambrosus.sdk;

import junit.framework.Assert;
import static org.junit.Assert.*;

import org.junit.Test;

public class AssetsIntegrationTest {

    @Test
    public void findAsset() {
        final String expectedAssetID = "0xa444489cf4c63adba081d3ba29d007e08517f1694e6a173cf6616e0fbb1d8882";

        AMBNetwork ambNetwork = new AMBNetwork();

        AssetSearchParamsBuilder searchParamsBuilder = new AssetSearchParamsBuilder().byEventIdentifier(Identifier.GTIN, "39219898012908123");

        AMBNetworkCall<SearchResult<Asset>> networkCall = ambNetwork.findAssets(searchParamsBuilder.build());

        try {
            SearchResult<Asset> result = networkCall.execute();
            for (Asset asset : result.getValues()) {
                if(expectedAssetID.equals(asset.getSystemId()))
                    return;
            }
            Assert.fail("Wasn't able to find assert with ID: ");

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Test
    public void getAssetById(){
        final String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";

        AMBNetwork ambNetwork = new AMBNetwork();

        AMBNetworkCall<Asset> networkCall = ambNetwork.getAsset(assetId);

        try {
            Asset asset = networkCall.execute();
            assertEquals(assetId, asset.getSystemId());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Test
    public void getAssetById_notFoundException(){
        final String assetId = "notPossible";

        AMBNetwork ambNetwork = new AMBNetwork();

        AMBNetworkCall<Asset> networkCall = ambNetwork.getAsset(assetId);

        try {
            Asset asset = networkCall.execute();
        } catch (EntityNotFoundException t) {
            //it's expected
            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}