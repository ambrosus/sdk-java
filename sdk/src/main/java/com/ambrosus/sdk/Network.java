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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.utils.Assert;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//TODO Add package-level description
/**
 *  Network is a core class responsible for communication with Ambrosus Network.
 *  It contains a number of get*(...), find*(...) and push*(...) methods which can be used to retrieve/push data from/to AMB-Net.
 *  This <code>Network</code> implementation supports only generic {@link Event} and {@link Asset} data models.
 *  But it can be extended in order to support custom data models.
 *  You can use {@link com.ambrosus.sdk.model.AMBNetwork} as a sample of such implementation.
 *  <p>
 *  Usage example:
 *  <pre>{@code
 *  String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";
 *  Network network = new Network();
 *  NetworkCall<Asset> networkCall = network.getAsset(assetId);
 *  Asset asset = networkCall.execute();
 *  }</pre>
 *
 */

public class Network {

    private final Service service;

    private AuthToken authToken;

    public Network(){
        this(new Configuration());
    }

    public Network(Configuration conf){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(conf.readTimeOut, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(conf.url)
                .addConverterFactory(GsonConverterFactory.create(Json.GSON))
                .client(client)
                .build();

        service = retrofit.create(Service.class);
    }

    /**
     * This method is designed to fetch {@link Asset} with specified {@link Asset#getSystemId() assetId} from the network
     * @param assetId unique {@linkplain Asset#getSystemId() asset identifier}
     * @return {@link NetworkCall NetworkCall&lt;Asset&gt;} instance which can be used to fetch {@link Asset} with specified {@link Asset#getSystemId() assetId}
     * <br>{@link EntityNotFoundException} will be thrown during execution of this {@link NetworkCall}
     * if Network would not be able to find an {@link Asset} with {@linkplain Asset#getSystemId() systemId} which matches <code>assetId</code> parameter
     */
    @NonNull
    public NetworkCall<Asset> getAsset(@NonNull String assetId) {
        return new NetworkCallWrapper<>(service.getAsset(assetId), MissingEntityErrorHandler.INSTANCE);
    }

    /**
     * This method is designed to fetch {@link Event} with specified {@link Event#getSystemId() eventId} from the network
     * @param eventId unique {@linkplain Event#getSystemId() event identifier}
     * @return {@link NetworkCall NetworkCall&lt;Event&gt;} instance which can be used to fetch {@link Event} with specified {@link Event#getSystemId() eventId}
     * <br>{@link EntityNotFoundException} will be thrown during execution of this {@link NetworkCall}
     * if Network would not be able to find an {@link Event} with {@linkplain Event#getSystemId() systemId} which matches <code>eventId</code> parameter
     */
    @NonNull
    public NetworkCall<Event> getEvent(@NonNull String eventId) {
        return new NetworkCallWrapper<>(service.getEvent(eventId, getOptionalAMBTokenAuthHeader()), MissingEntityErrorHandler.INSTANCE);
    }

    /**
     * This method is designed to search for {@linkplain Asset assets} which meets specified criteria.
     *
     * @param query specifies search criteria
     * @return {@link NetworkCall NetworkCall&lt;SearchResult&lt;Asset&gt;&gt;} instance which {@linkplain NetworkCall#execute() can be used} to get a {@linkplain SearchResult search result}
     * @see AssetQueryBuilder
     * @see Query
     */
    @NonNull
    public NetworkCall<SearchResult<Asset>> findAssets(@NonNull Query<Asset> query) {
        return new BasicSearchRequestWrapper<>(
                new NetworkCallWrapper<>(service.findAssets(query.asMap())),
                query
        );
    }

    /**
     * This method is designed to search for {@linkplain Event events} which meets specified criteria.
     *
     * @param query specifies search criteria
     * @return {@link NetworkCall NetworkCall&lt;SearchResult&lt;Event&gt;&gt;} instance which {@linkplain NetworkCall#execute() can be used} to get a {@linkplain SearchResult search result}
     * @see EventQueryBuilder
     * @see Query
     */
    @NonNull
    public NetworkCall<SearchResult<Event>> findEvents(@NonNull Query<? extends Event> query) {
        return new BasicSearchRequestWrapper<>(
                new NetworkCallWrapper<>(
                        service.findEvents(
                                query.asMap(),
                                getOptionalAMBTokenAuthHeader()
                        )
                ),
                query
        );
    }

    /**
     * This method represents single endpoint to search for any {@link Entity} supported by this {@link Network} implementation.
     *
     * @param query a {@link Query} instance parametrized with a subclass of any {@link Entity} supported by this {@link Network} implementation.
     *
     * @return a {@link NetworkCall} instance parametrized with generalized {@link SearchResult} type.
     *
     * <br>Each {@link SearchResult#getItems() item} of this {@link SearchResult} would be a subclass of {@link Query#resultType}
     * supported by this {@link Network} implementation.
     *
     * <br>E.g. each item of <code>assetSearchResult.getItems()</code> list is an instance of {@link Asset} class:
     *
     * <pre>{@code
     * Network network = new Network();
     * Query<Asset> assetQuery = new AssetQueryBuilder().build();
     * SearchResult<? extends Entity> assetSearchResult = network.find(assetQuery).execute();}</pre>
     *
     * And each item of <code>ambEventSearchResult.getItems()</code> list is an instance of {@link Event} class
     * (because {@link Event} is the only subclass of {@link com.ambrosus.sdk.model.AMBEvent AMBEvent} supported by this {@link Network} implementation):
     *
     * <pre>{@code
     * Query<AMBEvent> ambEventQuery = new AMBEventQueryBuilder().build();
     * SearchResult<? extends Entity> ambEventSearchResult = network.find(assetQuery).execute();}</pre>
     *
     * @throws IllegalArgumentException if {@link Query#resultType} is not a subclass of {@link Event} or {@link Asset} classes
     */
    public NetworkCall<SearchResult<? extends Entity>> find(Query<? extends Entity> query) throws IllegalArgumentException {
        if(Event.class.isAssignableFrom(query.resultType)) {
            NetworkCall<SearchResult<Event>> eventsRequest = findEvents((Query<Event>)query);
            return (NetworkCall) eventsRequest;
        } else if(Asset.class.isAssignableFrom(query.resultType)) {
            NetworkCall<SearchResult<Asset>> assetsRequest = findAssets((Query<Asset>) query);
            return (NetworkCall) assetsRequest;
        }
        throw new IllegalArgumentException("Unknown query type: " + query.resultType);
    }


    @NonNull
    public NetworkCall<Asset> pushAsset(Asset asset) {
        return new NetworkCallWrapper<>(service.createAsset(asset), PermissionDeniedErrorHandler.INSTANCE);
    }

    @NonNull
    public NetworkCall<Event> pushEvent(Event event) {
        return new NetworkCallWrapper<>(service.createEvent(event.getAssetId(), new Event(event)), PermissionDeniedErrorHandler.INSTANCE);
    }


    /**
     * It will get you an Account instance for account with specified address
     * if you have "manage_accounts" permissions (at least for the case when account which you have used for authorization
     * and specified account have the same access level)
     *
     * result.execute() will throw
     *  - {@link PermissionDeniedException} - if you authorized with private key which is not registered on Ambrosus network
     *  - {@link EntityNotFoundException} - if you are asking for an account which is not registered on Ambrosus network
     *
     * @param address - address for Account instance which you want to get
     * @return
     * @throws IllegalStateException if you haven't authorized this network instance with some non-null AuthToken before (by calling {@link #authorize(AuthToken)})
     */
    //TODO check what it returns in the case when you don't have "manage_account" permissions
    //TODO check what it returns when you ask for account with greater access level than you currently have (authorized with)
    @NonNull
    public NetworkCall<Account> getAccount(String address) throws IllegalStateException {
        Assert.assertNotNull(authToken, IllegalStateException.class, "You have to authorize first");
        return new NetworkCallWrapper<>(
                service.getAccount(
                        address,
                        getAMBTokenAuthHeader(authToken)
                ),
                PermissionDeniedErrorHandler.INSTANCE, MissingEntityErrorHandler.INSTANCE
        );
    }

    public void authorize(@Nullable AuthToken authToken) {
        this.authToken = authToken;
    }

    public @Nullable AuthToken getAuthToken() {
        return authToken;
    }

    private String getOptionalAMBTokenAuthHeader() {
        return authToken != null ? getAMBTokenAuthHeader(authToken) : null;
    }

    static String getAMBTokenAuthHeader(AuthToken authToken) {
        return "AMB_TOKEN " + authToken.getAsString();
    }

    static String getObjectHash(Object object){
        return Ethereum.computeHashString(Json.getLexNormalizedJsonStr(object));
    }

    static String getObjectSignature(Object object, String privateKey){
        return Ethereum.computeSignature(Json.getLexNormalizedJsonStr(object), Ethereum.getEcKeyPair(privateKey));
    }

}
