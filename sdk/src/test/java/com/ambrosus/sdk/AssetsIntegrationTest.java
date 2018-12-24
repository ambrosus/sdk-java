package com.ambrosus.sdk;

import com.ambrosus.sdk.AMBNetwork;
import com.ambrosus.sdk.AMBNetworkCall;
import com.ambrosus.sdk.models.Asset;
import com.ambrosus.sdk.AssetSearchParamsBuilder;
import com.ambrosus.sdk.Identifiers;
import com.ambrosus.sdk.SearchResult;

import junit.framework.Assert;

import org.junit.Test;

public class AssetsIntegrationTest {

    @Test
    public void findAsset() {
        final String expectedAssetID = "0xa444489cf4c63adba081d3ba29d007e08517f1694e6a173cf6616e0fbb1d8882";

        AMBNetwork ambNetwork = new AMBNetwork();

        AssetSearchParamsBuilder searchParamsBuilder = new AssetSearchParamsBuilder().byEventIdentifier(Identifiers.GTIN, "39219898012908123");

        AMBNetworkCall<SearchResult<Asset>> networkCall = ambNetwork.findAssets(searchParamsBuilder.build());

        try {
            SearchResult<Asset> result = networkCall.execute();
            for (Asset asset : result.values) {
                if(expectedAssetID.equals(asset.getAssetId()))
                    return;
            }
            Assert.fail("Wasn't able to find assert with ID: ");

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}