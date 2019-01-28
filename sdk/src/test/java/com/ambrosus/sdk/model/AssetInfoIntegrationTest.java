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
            assertEquals("0xafa2e53de0855ba93597e5f5985e0cf8f39ca4f011456bef808c1c2fca1005a9", assetInfo.getEventId());
            assertEquals("PURE DARK CHOCOLATE BAR 92%", assetInfo.getName());
            assertEquals(1496250888000L, assetInfo.getTimeStamp());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
