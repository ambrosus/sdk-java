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
import com.ambrosus.sdk.utils.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  Network is a core class responsible for communication with AMB-Net.
 *  It contains a number of get*(...), find*(...) and push*(...) methods which can be used to retrieve/push data from/to AMB-Net e.g.:
 *
 *  <code>
 *  String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";
 *  Network network = new Network();
 *  NetworkCall<Asset> networkCall = network.getAsset(assetId);
 *  </code>
 */

//TODO it would be nice to add toJson() method
public class Network {

    static final Gson GSON;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
            DecimalFormat df = new DecimalFormat("#.#########");
            df.setRoundingMode(RoundingMode.CEILING);
            return new JsonPrimitive(df.format(src));
        });
        GSON = gsonBuilder.create();
    }

    private final Service service;

    private AuthToken authToken;

    public Network(){
        this(new Configuration());
    }

    public Network(Configuration conf){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                System.out.println(message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(conf.readTimeOut, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(conf.url)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .client(client)
                .build();

        service = retrofit.create(Service.class);
    }

    @NonNull
    public NetworkCall<Asset> getAsset(@NonNull String assetId) {
        return new NetworkCallWrapper<>(service.getAsset(assetId), MissingEntityErrorHandler.INSTANCE);
    }

    /**
     * Creates a <code>NetworkCall&lt;Event&gt;</code> instance which can be used to fetch event with specific <code>eventId</code> from the Ambrosus Network
     * {@link EntityNotFoundException} will be thrown during execution of this <code>NetworkCall</code> if event with such <code>eventId</code> doesn't exist
     *
     * @param eventId - unique event identifier
     * @return <code>NetworkCall&lt;Event&gt;</code> which can be used to fetch event with specific <code>eventId</code> from the network
     */
    @NonNull
    public NetworkCall<Event> getEvent(@NonNull String eventId) {
        return new NetworkCallWrapper<>(service.getEvent(eventId, getOptionalAMBTokenAuthHeader()), MissingEntityErrorHandler.INSTANCE);
    }

    @NonNull
    public NetworkCall<SearchResult<Asset>> findAssets(@NonNull Query<Asset> query) {
        return new BasicSearchRequestWrapper<>(
                new NetworkCallWrapper<>(service.findAssets(query.asMap())),
                query
        );
    }

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

    public NetworkCall<SearchResult<? extends Entity>> find(Query query) {
        if(Event.class.isAssignableFrom(query.resultType)) {
            NetworkCall<SearchResult<Event>> eventsRequest = findEvents(query);
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
        return Ethereum.computeHashString(GsonUtil.getLexNormalizedJsonStr(object, GSON));
    }

    static String getObjectSignature(Object object, String privateKey){
        return Ethereum.computeSignature(GsonUtil.getLexNormalizedJsonStr(object, GSON), Ethereum.getEcKeyPair(privateKey));
    }

}
