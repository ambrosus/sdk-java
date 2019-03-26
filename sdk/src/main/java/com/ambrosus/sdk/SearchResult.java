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

import android.support.annotation.Nullable;

import com.ambrosus.sdk.utils.Assert;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class represents results of some search request for data models of <code>T</code> type (or some subtype of <code>T</code> type).
 * <p>
 * Each <code>SearchResult</code> instance includes content of one page of overall search result.
 * You can get this content with {@link #getItems()} method.
 * It will give you items of the first page
 * if you didn't get this search result using a query created by {@link PageQueryBuilder} for another page
 * and you didn't specify an index of the page for this query directly with {@link AbstractQueryBuilder#page(int)} method.
 *
 * @param <T> type or supertype of the data which were requested from the Network.
 * Each item which you can get with {@link #getItems()} method will be an instance of this type.
 * But all that items can be an instance of the same subtype of <code>T</code>
 * if you used generic {@link Network#find(Query)} method to get this <code>SearchResult</code>.
 * In this case you can check {@link Query#resultType resultType} field of the query
 * {@linkplain #getQuery() which was used to get this SearchResult} to get a real type for these items.
 */
public class SearchResult<T extends Entity> extends NetworkSearchResult<T> {

    private Query<? extends T> query;
    private Date firstItemTimestamp;
    private Integer defaultPageSize;

    SearchResult(Query<? extends T> query, NetworkSearchResult<T> source) {
        super(source);
        this.query = query;
        firstItemTimestamp = source.getFirstItemTimestamp();
        if(query.getPageSize() == null && getTotalCount() > getItems().size())
            defaultPageSize = getItems().size();
    }

    private SearchResult(List<T> values, SearchResult<?> source) {
        super(values, source.getTotalCount());
        this.query = (Query<? extends T>) source.query;
        this.firstItemTimestamp = source.getFirstItemTimestamp();
        this.defaultPageSize = source.defaultPageSize;
    }

    @Override
    Date getFirstItemTimestamp() {
        return firstItemTimestamp;
    }

    /**
     * Returns a {@link Query} which was used to get this <code>SearchResult</code>.
     * You can pass this {@link Query} to one of the {@link Network}{@code .find*(Query<?>)} methods
     * to repeat this search request and get a fresh version of its search result.
     *
     * @return a {@link Query} which was used to get this <code>SearchResult</code>
     * @see Network#findEvents(Query)
     * @see Network#findAssets(Query)
     * @see Network#find(Query)
     *
     */
    public Query<? extends T> getQuery() {
        return query;
    }

    /**
     * @return a zero-based index of the page which {@link #getItems() items} this <code>SearchResult</code> contains.
     * You can use {@link PageQueryBuilder} to build a query for <code>SearchResult</code> which contains items for other page.
     */
    public int getPageIndex() {
        return query.getPage();
    }

    @Nullable Integer getPageSize(){
        Integer queryPageSize = query.getPageSize();
        return queryPageSize != null ? queryPageSize : defaultPageSize;
    }

    /**
     * @return total number of the pages which content meets search criteria specified by the {@link #getQuery() Query}.
     * Even if there are no entities which meets your search criteria you can get a <code>SearchResult</code>
     * with one empty page. So this method never returns a value which is less than 1.
     */
    public int getTotalPages() {
        Integer pageSize = getPageSize();
        return pageSize != null ? (getTotalCount()-1)/pageSize + 1 : 1;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s, page: %d/%d, query: %s", super.toString(), getPageIndex()+1, getTotalPages(), query.asMap());
    }

    static <OutputType extends Entity, InputType extends Entity> SearchResult<OutputType> create(SearchResult<InputType> source, Class<OutputType> resultType, DataConverter<List<InputType>, List<OutputType>> adapter) throws Throwable {

        //TODO we have to cover case like this: ambNetwork.findAMBEvents(new AssetInfoQueryBuilder().build()).execute() with unit-tests
        Assert.assertTrue(
                resultType.isAssignableFrom(source.getQuery().resultType),
                IllegalArgumentException.class,
                "resultType must be a super type source.getQuery().resultType"
        );

        return new SearchResult<>(adapter.convert(source.getItems()), source);
    }
}
