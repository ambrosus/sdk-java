package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventsSearchParamsBuilder;
import com.ambrosus.sdk.Network;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.SearchResult;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class AMBEventIntegrationTest {

    private static AMBNetwork network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new AMBNetwork();
    }

    @Test
    public void findEventsSimpleTest(){
        final String expectedAssetID = "0xfe7d80686adf18b2259a60d836104ab866a20b6eabf3bc4cde75ffc4aa8015a0";

        AMBEventSearchParamsBuilder searchParamsBuilder = new AMBEventSearchParamsBuilder().byDataObjectIdentifier(Identifier.EAN13, "6942507312009");

        NetworkCall<SearchResult<Event>> networkCall = network.findEvents(searchParamsBuilder.build());

        try {
            SearchResult<Event> result = networkCall.execute();
            for (Event event : result.getValues()) {
                if(expectedAssetID.equals(event.getAssetId()))
                    return;
            }
            fail("Wasn't able to find event which has specified identifier and belongs to asset with ID: " + expectedAssetID);
            System.out.println();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
