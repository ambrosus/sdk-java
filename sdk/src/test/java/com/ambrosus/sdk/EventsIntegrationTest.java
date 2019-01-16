package com.ambrosus.sdk;

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



}
