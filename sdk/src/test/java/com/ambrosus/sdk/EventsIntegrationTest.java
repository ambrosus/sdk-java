package com.ambrosus.sdk;

import com.ambrosus.sdk.models.Event;

import junit.framework.Assert;

import org.junit.Test;

public class EventsIntegrationTest {

    @Test
    public void findEventsSimpleTest(){
        final String expectedAssetID = "0xfe7d80686adf18b2259a60d836104ab866a20b6eabf3bc4cde75ffc4aa8015a0";

        AMBNetwork ambNetwork = new AMBNetwork();

        EventsSearchParamsBuilder searchParamsBuilder = new EventsSearchParamsBuilder().byDataObjectIdentifier(Identifiers.EAN13, "6942507312009");

        AMBNetworkCall<SearchResult<Event>> networkCall = ambNetwork.findEvents(searchParamsBuilder.build());

        try {
            SearchResult<Event> result = networkCall.execute();
            for (Event event : result.values) {
                if(expectedAssetID.equals(event.getAssetId()))
                    return;
            }
            Assert.fail("Wasn't able to find event which has specified identifier and belongs to asset with ID: " + expectedAssetID);
            System.out.println();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
