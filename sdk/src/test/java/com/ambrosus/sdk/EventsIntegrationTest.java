package com.ambrosus.sdk;

import com.ambrosus.sdk.models.Asset;
import com.ambrosus.sdk.models.Event;
import com.ambrosus.sdk.models.Identifier;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EventsIntegrationTest {

    private static AMBNetwork ambNetwork;

    @BeforeClass
    public static void setUpNetwork(){
        ambNetwork = new AMBNetwork();
    }

    @Test
    public void findEventsSimpleTest(){
        final String expectedAssetID = "0xfe7d80686adf18b2259a60d836104ab866a20b6eabf3bc4cde75ffc4aa8015a0";

        EventsSearchParamsBuilder searchParamsBuilder = new EventsSearchParamsBuilder().byDataObjectIdentifier(Identifier.EAN13, "6942507312009");

        AMBNetworkCall<SearchResult<Event>> networkCall = ambNetwork.findEvents(searchParamsBuilder.build());

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

    @Test
    public void checkAssetIdentifiersForEvent(){
        final String eventIDForTest = "0x36fe3d701297e0ede30456241594f19b60c07ae4e629f5a11a944d46567efafe";

        AMBNetworkCall<Event> networkCall = ambNetwork.getEvent(eventIDForTest);

        try {
            Event result = networkCall.execute();
            List<Identifier> assetIdentifiers = result.getAssetIdentifiers();
            assertTrue(assetIdentifiers.contains(new Identifier(Identifier.GTIN, "1043345532")));
            assertTrue(assetIdentifiers.contains(new Identifier(Identifier.EAN13, "6942507312009")));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Test
    public void getEventById_notFoundException(){
        final String eventID = "notPossible";

        AMBNetworkCall<Event> networkCall = ambNetwork.getEvent(eventID);

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

}
