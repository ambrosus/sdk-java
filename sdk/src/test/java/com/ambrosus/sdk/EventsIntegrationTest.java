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

import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EventsIntegrationTest {

    private static Network network;

    @Before
    public void setUpNetwork(){
        network = new Network(new Configuration().readTimeOut(0, TimeUnit.SECONDS));
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
    public void getEventById() throws Throwable {
        final String eventId = "0x36fe3d701297e0ede30456241594f19b60c07ae4e629f5a11a944d46567efafe";
        Event event = network.getEvent(eventId).execute();
        System.out.println(event.getSystemId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void getEventById_notFoundException() throws Throwable {
        final String eventID = "notPossible";
        NetworkCall<Event> networkCall = network.getEvent(eventID);
        networkCall.execute();
    }


    //TODO for some reason I got incorrect error from server for the "0x58e2e95dc3c1367ec3b849cbdf89a6a9a1b11848484561e79a5c9cfd5c9b771b\n" assetID (with \n" at the end). Need to check whats going wrong.
    @Test(expected = PermissionDeniedException.class)
    public void pushEvent() throws Throwable {
        JsonObject testData = new JsonObject();
        testData.addProperty("testKey", "testValue");
        testData.addProperty("anotherKey", "anotherValue");

        Event.Builder builder = new Event.Builder("0x4f3cb3aafe426a045714fc55e1166cfc003091c2780e6855af75a8209d3c1333")
                .addData("custom", testData);
        Event event = builder.createEvent(TestData.UNREGISTERED_PRIVATE_KEY);

        network.pushEvent(event).execute();
    }

    @Test(expected = RestrictedDataAccessException.class)
    public void checkRestrictedDataAccessException() throws RestrictedDataAccessException {
        NetworkCall<Event> networkCall = network.getEvent("0x4a2f4b6db79fba46a9a99e486498f1ce44d4e8714db7ccae4fdd1cb33bc5ef89");

        Event result;
        try {
            result = networkCall.execute();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        result.getUserData();
    }

    @Test
    public void checkLimitedAccessEvents() throws RestrictedDataAccessException {
        Event result;
        try {
            network.authorize(TestData.getAuthToken());
            NetworkCall<Event> networkCall = network.getEvent("0x4a2f4b6db79fba46a9a99e486498f1ce44d4e8714db7ccae4fdd1cb33bc5ef89");
            result = networkCall.execute();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        result.getUserData();
    }

    @Test
    public void ensureAPIDoesntAllowEmptyDataEvents() throws Throwable{
        Event event = TestData.getFromJson(getClass(), Event.class, "EmptyDataEvent.json");

        try {
            network.pushEvent(event).execute();
        } catch (RequestFailedException requestFailedException) {
            assertEquals(requestFailedException.code, 400);
        }
    }
}
