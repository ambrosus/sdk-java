package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.model.AMBAssetInfo;
import com.ambrosus.sdk.model.AMBNetwork;
import com.ambrosus.sdk.model.AssetInfoQueryBuilder;
import com.ambrosus.sdk.model.Identifier;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReadMeSamplesTest {

    private Network network;

    @Before
    public void setUpNetwork(){
        network = new Network();
    }

    @Test
    public void NetworkOverview() throws Throwable {
        String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";
        Network network = new Network();
        NetworkCall<Asset> networkCall = network.getAsset(assetId);

        try {
            Asset asset = networkCall.execute();
            System.out.println(asset.getSystemId());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        networkCall.clone().enqueue(new NetworkCallback<Asset>() {
            @Override
            public void onSuccess(@NonNull NetworkCall<Asset> call, @NonNull Asset asset) {
                //request was performed successfully
                System.out.println(asset.getSystemId());
            }

            @Override
            public void onFailure(@NonNull NetworkCall<Asset> call, @NonNull Throwable error) {
                //request failed because of (Throwable error)
            }
        });
    }

    @Test
    public void fetchAssetEventById() throws Throwable {
        String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";

        Asset asset = network.getAsset(assetId).execute();
        System.out.println(asset.getSystemId());

        final String eventId = "0x36fe3d701297e0ede30456241594f19b60c07ae4e629f5a11a944d46567efafe";

        Event event = network.getEvent(eventId).execute();
        System.out.println(event.getSystemId());
    }

    @Test
    public void searchForEvents() throws Throwable {
        SearchResult<Event> searchResult
                = network.findEvents(new EventQueryBuilder().build()).execute();
        List<Event> values = searchResult.getItems();

        Query<Event> anotherQuery = new EventQueryBuilder()
                .createdBy("0xFF1E60D7e4fe21C1817B8249C8cB8E52D1912665")
                .byDataObjectType("ambrosus.asset.harvested")
                .build();

        searchResult = network.findEvents(anotherQuery).execute();

        values = searchResult.getItems();
        System.out.println(values);
    }

    @Test
    public void pagination() throws Throwable {
        Query<Event> query = new EventQueryBuilder().createdBy("0x9A3Db936c94523ceb1CcC6C90461bc34a46E9dfE").build();

        SearchResult<Event> firstPage = network.findEvents(query).execute();
        if(firstPage.getTotalPages() > 1) {
            PageQueryBuilder<Event> pageQueryBuilder = new PageQueryBuilder<>(firstPage);
            Query<? extends Event> secondPageQuery = pageQueryBuilder.getQueryForPage(firstPage.getPageIndex() + 1);
            SearchResult<Event> secondPage = network.findEvents(secondPageQuery).execute();
            System.out.println(secondPage);
        }
    }



    @Test
    public void createAssetEvent() throws Throwable {
        String privateKey = "0x864ba4c99a04dc9adeaa06d1621855849aaa37c70012d544475a9862c9460514";

        Asset asset = new Asset.Builder().createAsset(privateKey);

        network.pushAsset(asset).execute();

        JsonObject testData = new JsonObject();
        testData.addProperty("testKey", "testValue");
        testData.addProperty("anotherKey", "anotherValue");

        Event.Builder builder = new Event.Builder(asset.getSystemId())
                .addData("custom", testData);

        Event event = builder.createEvent(privateKey);
        network.pushEvent(event).execute();
    }

    //TODO try to get access to event data with access level = 1 using another account access token
    //TODO we have to add a unit test for querying private event by ID
    @Test
    public void createAndGetPrivateEvent() throws Throwable {
        //TODO make it possible to use real private key for Integration Tests
        String privateKey = TestData.UNREGISTERED_PRIVATE_KEY;

        Asset asset = new Asset.Builder().createAsset(privateKey);

        network.pushAsset(asset).execute();

        JsonObject testData = new JsonObject();
        testData.addProperty("testKey", "testValue");
        testData.addProperty("anotherKey", "anotherValue");

        Event.Builder builder = new Event.Builder(asset.getSystemId())
                .setAccessLevel(1)
                .addData("custom", testData);

        Event event = builder.createEvent(privateKey);
        network.pushEvent(event).execute();

        Event privateEvent = network.getEvent(event.getSystemId()).execute();

        try {
            List<JsonObject> data = privateEvent.getUserData();
        } catch (RestrictedDataAccessException e) {
            //we get this exception because
            //of querying event with accessLevel > 0
            //without providing correct AuthToken to the network

            AuthToken authToken = AuthToken.create(privateKey, 1, TimeUnit.DAYS);
            network.authorize(authToken);

            privateEvent = network.getEvent(event.getSystemId()).execute();
            //now you can get access to event data
            System.out.println(privateEvent.getUserData());
        }
    }

    @Test
    public void useAnotherAPIEndpoint() throws Throwable {
        Configuration configuration = new Configuration().url("https://hermes.ambrosus.com");
        Network network = new Network(configuration);
        SearchResult<Event> result = network.findEvents(new EventQueryBuilder().build()).execute();
    }

    @Test
    public void searchForItemByEAN13() throws Throwable {
        //Using generic Event model

        String ean13barcode = "3451080000324";

        //according to assumptions Event with information about item:
        Query<Event> query = new EventQueryBuilder()
                //1. should contain data object of "ambrosus.asset.info" type
                .byDataObjectType("ambrosus.asset.info")
                //2. should contain data object with "identifiers.ean13" array
                //so querying for events which have "identifiers.ean13" array with ean13barcode value
                .byDataObjectField("identifiers.ean13", ean13barcode)
                .build();

        SearchResult<Event> eventSearchResult = network.findEvents(query).execute();
        Event item = eventSearchResult.getItems().get(0);

        //Using generic Event model + AssetInfoQueryBuilder and Identifier classes which contain constants from the code above
        Query<AMBAssetInfo> assetInfoQuery = new AssetInfoQueryBuilder()
                .byIdentifier(new Identifier(Identifier.EAN13, ean13barcode))
                .build();

        eventSearchResult = network.findEvents(query).execute();
        item = eventSearchResult.getItems().get(0);

        //Using an instance of AMBNetwork class which you can use to query AssetInfo model
        AMBNetwork ambNetwork = new AMBNetwork();
        SearchResult<AMBAssetInfo> assetInfoSearchResult = ambNetwork.findAssetInfo(assetInfoQuery).execute();
        AMBAssetInfo assetInfo = assetInfoSearchResult.getItems().get(0);
    }

}
