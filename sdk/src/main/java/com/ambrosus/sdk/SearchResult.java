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

public class SearchResult<T extends Entity> extends NetworkSearchResult<T> {

    private Query<? extends T> query;
    private Date firstItemTimestamp;
    private Integer defaultPageSize;

    SearchResult(Query<? extends T> query, NetworkSearchResult<T> source) {
        super(source);
        this.query = query;
        firstItemTimestamp = source.getFirstItemTimestamp();
        if(query.getPageSize() == null && getTotalCount() > getValues().size())
            defaultPageSize = getValues().size();
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


    public Query<? extends T> getQuery() {
        return query;
    }

    /** Zero based page number**/
    public int getPage() {
        return query.getPage();
    }

    public @Nullable Integer getPageSize(){
        Integer queryPageSize = query.getPageSize();
        return queryPageSize != null ? queryPageSize : defaultPageSize;
    }

    /**
     * @return Total number of search result pages. Even if there are no results at all it will return 1 which means "one empty page"
     */
    public int getTotalPages() {
        Integer pageSize = getPageSize();
        return pageSize != null ? (getTotalCount()-1)/pageSize + 1 : 1;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s, page: %d/%d, query: %s", super.toString(), getPage()+1, getTotalPages(), query.asMap());

    }

    static <OutputType extends Entity, InputType extends Entity> SearchResult<OutputType> create(SearchResult<InputType> source, Class<OutputType> resultType, DataConverter<List<InputType>, List<OutputType>> adapter) throws Throwable {

        //TODO we have to cover case like this: ambNetwork.findAMBEvents(new AssetInfoQueryBuilder().build()).execute() with unit-tests
        Assert.assertTrue(
                resultType.isAssignableFrom(source.getQuery().resultType),
                IllegalArgumentException.class,
                "resultType must be a super type source.getQuery().resultType"
        );

        return new SearchResult<>(adapter.convert(source.getValues()), source);
    }
}
