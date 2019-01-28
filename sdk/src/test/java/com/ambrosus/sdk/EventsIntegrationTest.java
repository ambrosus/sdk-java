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

package com.ambrosus.sdk;

import com.ambrosus.sdk.utils.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class EventsIntegrationTest {

    private static Network network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new Network();
    }


//TODO restore this test
//    @Test
//    public void checkAssetIdentifiersForEvent(){
//        final String eventIDForTest = "0x36fe3d701297e0ede30456241594f19b60c07ae4e629f5a11a944d46567efafe";
//
//        AMBNetworkCall<Event> networkCall = ambNetwork.getEvent(eventIDForTest);
//
//        try {
//            Event result = networkCall.execute();
//            List<Identifier> assetIdentifiers = result.getAssetIdentifiers();
//            assertTrue(assetIdentifiers.contains(new Identifier(Identifier.GTIN, "1043345532")));
//            assertTrue(assetIdentifiers.contains(new Identifier(Identifier.EAN13, "6942507312009")));
//        } catch (Throwable t) {
//            throw new RuntimeException(t);
//        }
//    }

    @Test
    public void getEventById_notFoundException(){
        final String eventID = "notPossible";

        NetworkCall<Event> networkCall = network.getEvent(eventID);

        try {
            networkCall.execute();
            fail("got some event but should get an error");
        } catch (EntityNotFoundException t) {
            //it's expected
            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void pushEvent() {
        JsonObject testData = new JsonObject();
        testData.addProperty("testKey", "testValue");
        testData.addProperty("anotherKey", "anotherValue");

        Event.Builder builder = new Event.Builder()
                .setAssetId("0x4f3cb3aafe426a045714fc55e1166cfc003091c2780e6855af75a8209d3c1333")
                .addData("custom", testData);
        Event event = builder.createEvent("0x864ba4c99a04dc9adeaa06d1621855849aaa37c70012d544475a9862c9460514");

        try {
            Event result = network.pushEvent(event).execute();
            System.out.println();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
