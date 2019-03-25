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

import com.ambrosus.sdk.utils.Assert;

import java.util.Date;
import java.util.Locale;

/**
 * A set of *QueryBuilder classes is designed to create search {@linkplain Query queries}
 * for {@linkplain Asset Assets}, {@linkplain Event Events} and subclasses of these data models.
 * <br>AbstractQueryBuilder class is a root class of this hierarchy. It's responsible for configuring search criteria
 * which is common for events and assets {@linkplain Query queries}.
 *
 * @param <BuilderType> type of the builder instance which would be returned by methods used to specify search criteria.
 * <br>This parameter makes it possible to override this type with a subclass type for subclass implementation.
 * <br>E.g:
 * <pre>{@code
 * class MyBuilder extends AbstractQueryBuilder<MyBuilder, Event> {
 *     MyBuilder() {
 *         super(Event.class);
 *     }
 * }
 *
 * //MyBuilder.from(Date) method below returns an instance of MyBuilder type
 * //because we specified MyBuilder as BuilderType type parameter above
 * MyBuilder instance = new MyBuilder().from(new Date());}</pre>
 *
 * @param <QueryType> type of the data model for which this builder (or subclass) specifies search criteria and builds {@linkplain Query queries}
 * <br>For example if you want to define a builder for {@linkplain Query queries} configured to search for events you have to use Event type as a QueryType:
 * <pre>{@code
 * class MyBuilder extends AbstractQueryBuilder<MyBuilder, Event> {
 *     MyBuilder() {
 *         super(Event.class);
 *     }
 * }
 *
 * MyBuilder myBuilder = new MyBuilder();
 * //build() method belows returns a query configured to search for Events:
 * Query<Event> eventQuery = myBuilder.build();}</pre>
 * See description of type parameter for {@link Query} class for details
 * @see Query
 *
 */
@SuppressWarnings("unchecked")
abstract class AbstractQueryBuilder<BuilderType extends AbstractQueryBuilder<BuilderType, QueryType>, QueryType extends Entity> {

    public static final int MAX_PAGE_SIZE = 100;

    private static final String PAGE_SIZE_KEY = "perPage";
    private static final String PAGE_KEY = "page";

    private final Class<QueryType> queryType;
    final Query.Params params;

    /**
     * This constructor is designed to use from subclasses implementation
     * @param queryResultType an {@link Class} instance for <code>QueryType</code> type parameter
     */
    AbstractQueryBuilder(Class<QueryType> queryResultType) {
        this.queryType = queryResultType;
        params = new Query.Params();
    }

    AbstractQueryBuilder(Query query) {
        this.queryType = query.resultType;
        this.params = query.getParams();
    }

    /**
     * Configures query to search for entities which {@link Entity#getTimestamp() timestamp} value
     * is greater than or equal to the number of seconds in the provided <code>date</code> value.
     * <p>
     * So if you always use current time value as a timestamp for new entities (default behaviour)
     * this method will configure a query to search for entities which where created after specified <code>date</code> (inclusive)
     *
     * @param date specifies minimum number of seconds for {@link Entity#getTimestamp() timestamp} field value of entities which you want to query
     * @return this builder instance
     */
    @NonNull
    public BuilderType from(@NonNull Date date) {
        params.set("fromTimestamp", date);
        return (BuilderType) this;
    }

    /**
     * Configures query to search for entities which {@link Entity#getTimestamp() timestamp} value
     * is less than or equal to the number of seconds in the provided <code>date</code> value.
     * <p>
     * So if you always use current time value as a timestamp for new entities (default behaviour)
     * this method will configure a query to search for entities which where created before specified <code>date</code> (inclusive)
     *
     * @param date specifies maximum number of seconds for {@link Entity#getTimestamp() timestamp} field value of entities which you want to query
     * @return this builder instance
     */
    @NonNull
    public BuilderType to(@NonNull Date date) {
        params.set("toTimestamp", date);
        return (BuilderType) this;
    }

    /**
     * By default SDK provides only first page from overall search result.
     * With this method you can configure a query to fetch a page with a <code>pageIndex</code> index
     * We recommend you do not use this method directly but use an instance of {@link PageQueryBuilder}
     * to create {@linkplain Query queries} for other pages instead.
     *
     * @param pageIndex zero-based index of the page which you want to query
     * @return this builder instance
     */
    public BuilderType page(int pageIndex) {
        params.set(PAGE_KEY, pageIndex);
        return (BuilderType) this;
    }

    /**
     * Specifies maximum number of entities which you can get with new query.
     * By default query relies on the server configuration for this parameter.
     * For now server doesn't allow to query more than 100 entities at once and returns 100 entities by default.
     * If you want to reset to default behaviour you can pass 0 as <code>perPage</code> value.
     *
     * @param perPage specifies maximum number of entities which you will get with new query (0 means use server configuration for this value)
     * @return this builder instance
     *
     * @throws IllegalArgumentException if <code>perPage</code> value doesn't belong to the [0, 100] range
     */
    public BuilderType perPage(int perPage) throws IllegalArgumentException {
        Assert.assertTrue(
                perPage >= 0 && perPage <= MAX_PAGE_SIZE,
                IllegalArgumentException.class,
                String.format(Locale.US, "Page size has to be in range [0, %d]", MAX_PAGE_SIZE)
        );
        if(perPage != 0) {
            params.set(PAGE_SIZE_KEY, perPage);
        } else {
            params.remove(PAGE_SIZE_KEY);
        }

        return (BuilderType) this;
    }


    /**
     * Configures query to search for entities which where created by account with <code>accountAddress</code> {@linkplain Entity#getAccountAddress() address}
     * @param accountAddress specifies the address of account which was used to create entities
     * @return this builder instance
     */
    @NonNull
    public BuilderType createdBy(@NonNull String accountAddress) {
        params.set("createdBy", Assert.assertNotNull(accountAddress, "accountAddress == null"));
        return (BuilderType) this;
    }


    /**
     * Creates a {@link Query} with a number of search criteria which match current configuration of the builder instance.
     * This {@link Query} can be used to search for entities of <code>QueryType</code> type and its subtypes.
     *
     * @return {@link Query} with search criteria which match current configuration of the builder instance
     */
    public Query<QueryType> build() {
        return new Query<>(queryType, params);
    }

    static Integer getPageSize(Query.Params params) {
        return params.getInt(PAGE_SIZE_KEY);
    }

    static int getPage(Query.Params params) {
        return params.getInt(PAGE_KEY, 0);
    }


}
