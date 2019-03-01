/*
 * Copyright: Ambrosus Technologies GmbH
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

@SuppressWarnings("unchecked")
abstract class AbstractQueryBuilder<BuilderType extends AbstractQueryBuilder<BuilderType, QueryType>, QueryType extends Entity> {

    public static final int MAX_PAGE_SIZE = 100;

    private static final String PAGE_SIZE_KEY = "perPage";
    private static final String PAGE_KEY = "page";

    private final Class<QueryType> queryType;
    final Query.Params params;

    AbstractQueryBuilder(Class<QueryType> queryType) {
        this.queryType = queryType;
        params = new Query.Params();
    }

    AbstractQueryBuilder(Query query) {
        this.queryType = query.resultType;
        this.params = query.getParams();
        //TODO ensure that it's still required!!!
        params.remove(PAGE_KEY);//TODO need a test for this case
    }

    @NonNull
    public BuilderType from(@NonNull Date date) {
        params.set("fromTimestamp", date);
        return (BuilderType) this;
    }

    @NonNull
    public BuilderType to(@NonNull Date date) {
        params.set("toTimestamp", date);
        return (BuilderType) this;
    }

    public BuilderType page(int page) {
        params.set(PAGE_KEY, page);
        return (BuilderType) this;
    }

    public BuilderType perPage(int perPage) {
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

    @NonNull
    public BuilderType createdBy(@NonNull String accountAddress) {
        params.set("createdBy", Assert.assertNotNull(accountAddress, "accountAddress == null"));
        return (BuilderType) this;
    }


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
