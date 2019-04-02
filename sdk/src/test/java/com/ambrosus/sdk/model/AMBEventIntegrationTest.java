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

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.SearchResult;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AMBEventIntegrationTest {

    private static AMBNetwork network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new AMBNetwork();
    }

    @Test
    public void findEventsSimpleTest() throws Throwable {
        final String expectedAssetID = "0xfe7d80686adf18b2259a60d836104ab866a20b6eabf3bc4cde75ffc4aa8015a0";

        AMBEventQueryBuilder searchParamsBuilder = new AMBEventQueryBuilder().byDataObjectIdentifier(Identifier.EAN13, "6942507312009");

        NetworkCall<SearchResult<Event>> networkCall = network.findEvents(searchParamsBuilder.build());

        SearchResult<Event> result = networkCall.execute();
        for (Event event : result.getItems()) {
            if(expectedAssetID.equals(event.getAssetId()))
                return;
        }
        fail("Wasn't able to find event which has specified identifier and belongs to asset with ID: " + expectedAssetID);
        System.out.println();
    }

    @Test
    public void findAmbrosusEventsTest(){

        AMBEventQueryBuilder eventQueryBuilder = new AMBEventQueryBuilder();
        eventQueryBuilder.forAsset("0x602023f73ab25f0c95a3cf4e92c9cb2f4c9c09dbd3ca6e167d362de6e7f1eeae");
        NetworkCall<SearchResult<AMBEvent>> networkCall = network.findAMBEvents(eventQueryBuilder.build(), false);

        try {
            SearchResult<AMBEvent> ambEvents = networkCall.execute();
            assertEquals(9, ambEvents.getItems().size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
