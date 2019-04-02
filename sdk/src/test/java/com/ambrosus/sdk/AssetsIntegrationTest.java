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

package com.ambrosus.sdk;

import com.ambrosus.sdk.model.Identifier;

import junit.framework.Assert;
import static org.junit.Assert.*;

import org.junit.Test;

public class AssetsIntegrationTest {

//    @Test
//    public void findAsset() {
//        final String expectedAssetID = "0xa444489cf4c63adba081d3ba29d007e08517f1694e6a173cf6616e0fbb1d8882";
//
//        Network network = new Network();
//
//        AssetQueryBuilder searchParamsBuilder = new AssetQueryBuilder().byEventIdentifier(Identifier.GTIN, "39219898012908123");
//
//        NetworkCall<SearchResult<Asset>> networkCall = network.findAssets(searchParamsBuilder.build());
//
//        try {
//            SearchResult<Asset> result = networkCall.execute();
//            for (Asset asset : result.getItems()) {
//                if(expectedAssetID.equals(asset.getSystemId()))
//                    return;
//            }
//            Assert.fail("Wasn't able to find assert with ID: ");
//
//        } catch (Throwable t) {
//            throw new RuntimeException(t);
//        }
//    }

    @Test
    public void getAssetById(){
        final String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";

        Network network = new Network();

        NetworkCall<Asset> networkCall = network.getAsset(assetId);

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

        Network network = new Network();

        NetworkCall<Asset> networkCall = network.getAsset(assetId);

        try {
            Asset asset = networkCall.execute();
        } catch (EntityNotFoundException t) {
            //it's expected
            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = PermissionDeniedException.class)
    public void pushAsset() throws Throwable {
        Asset asset = new Asset.Builder().createAsset(TestData.UNREGISTERED_PRIVATE_KEY);
        Network network = new Network();
        network.pushAsset(asset).execute();
    }
}