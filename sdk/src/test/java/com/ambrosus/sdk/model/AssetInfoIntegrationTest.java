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

package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Asset;
import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.TestData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssetInfoIntegrationTest {

    private Network network;

    @Before
    public void setUpNetwork(){
        network = new Network();
    }

    @Test
    public void testAssetInfoIntegration(){
        AMBNetwork network = new AMBNetwork();

        NetworkCall<AMBAssetInfo> networkCall = network.getAssetInfo(
                //searching for "PURE DARK CHOCOLATE BAR 92%"
                "0x602023f73ab25f0c95a3cf4e92c9cb2f4c9c09dbd3ca6e167d362de6e7f1eeae"
        );

        try {
            AMBAssetInfo assetInfo = networkCall.execute();
            assertEquals("0xafa2e53de0855ba93597e5f5985e0cf8f39ca4f011456bef808c1c2fca1005a9", assetInfo.getSystemId());
            assertEquals("PURE DARK CHOCOLATE BAR 92%", assetInfo.getName());
            assertEquals(new Date(1496250888000L), assetInfo.getTimestamp());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

//    @Test
//    public void pushAssetInfo() throws Throwable {
//        String privateKey = TestData.UNREGISTERED_PRIVATE_KEY;
//
//        Asset asset = new Asset.Builder().createAsset(privateKey);
//
//        network.pushAsset(asset).execute();
//
//        String assetInfo = "{\n" +
//                "            \"type\": \"ambrosus.asset.info\",\n" +
//                "            \"name\": \"PURE DARK CHOCOLATE BAR 9123%\",\n" +
//                "            \"assetType\": \"ambrosus.assetTypes.batch\",\n" +
//                "            \"images\": {\n" +
//                "              \"default\": {\n" +
//                "                \"url\": \"https://madecasse.com/wp-content/uploads/2016/10/92-dark-chocolate-hero-2.jpg\"\n" +
//                "              },\n" +
//                "              \"WEB-CHOCOLATE-BARS\": {\n" +
//                "                \"url\": \"https://madecasse.com/wp-content/uploads/2016/10/WEB-CHOCOLATE-BARS.jpg\"\n" +
//                "              },\n" +
//                "              \"?format=750w\": {\n" +
//                "                \"url\": \"https://static1.squarespace.com/static/585c5b5a9de4bb6fe48becb4/t/5a930c178165f549b5be0c2c/1519586329653/?format=750w\"\n" +
//                "              }\n" +
//                "            },\n" +
//                "            \"size\": \"2.64 oz.\",\n" +
//                "            \"Product Information\": {\n" +
//                "              \"attributes\": \"No-GMOs, Vegan, Gluten Free, Kosher, Soy Free\",\n" +
//                "              \"ingredients\": \"Organic cocoa beans, organic sugar, organic cocoa butter\",\n" +
//                "              \"Brand\": \"Madecasse\"\n" +
//                "            },\n" +
//                "            \"Batch Information\": {\n" +
//                "              \"Origin\": \"Madagascar\"\n" +
//                "            }\n" +
//                "          }";
//
//        String identifiers = " {\n" +
//                "            \"type\": \"ambrosus.asset.identifiers\",\n" +
//                "            \"identifiers\": {\n" +
//                "              \"ambrosus_batchId\": [\n" +
//                "                \"0xb9224ff5b50bfa9cf5651ce1262a7400882cadc8f6e8a8b9a1e24ef95763e4be\"\n" +
//                "              ],\n" +
//                "              \"gtin\": [\n" +
//                "                \"1043345532\"\n" +
//                "              ],\n" +
//                "              \"rfid\": [\n" +
//                "                \"E2001AC16987B6305024DED8\"\n" +
//                "              ],\n" +
//                "              \"ean13\": [\n" +
//                "                \"3451080000324\"\n" +
//                "              ],\n" +
//                "              \"batchId\": [\n" +
//                "                \"0x....\"\n" +
//                "              ],\n" +
//                "              \"Lot\": [\n" +
//                "                \"6126L70313EJ184\"\n" +
//                "              ]\n" +
//                "            }\n" +
//                "          }";
//
//
//        Event.Builder builder = new Event.Builder(asset.getSystemId())
//                .addData("ambrosus.asset.info", new Gson().fromJson(assetInfo, JsonObject.class))
//                .addData("ambrosus.asset.identifiers", new Gson().fromJson(identifiers, JsonObject.class));
//
//        Event event = builder.createEvent(privateKey);
//        network.pushEvent(event).execute();
//    }

}
