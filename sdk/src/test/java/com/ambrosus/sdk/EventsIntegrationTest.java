package com.ambrosus.sdk;

import com.ambrosus.sdk.model.AMBEvent;
import com.ambrosus.sdk.model.AMBEventFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void findAmbrosusEventsTest(){

        EventSearchParamsBuilder searchParamsBuilder = new EventSearchParamsBuilder();
        searchParamsBuilder.forAsset("0x602023f73ab25f0c95a3cf4e92c9cb2f4c9c09dbd3ca6e167d362de6e7f1eeae");

        NetworkCall<List<AMBEvent>> networkCall = network.findEvents(searchParamsBuilder.build(), new AMBEventFactory());

        try {
            List<AMBEvent> ambEvents = networkCall.execute();
            assertEquals(9, ambEvents.size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
