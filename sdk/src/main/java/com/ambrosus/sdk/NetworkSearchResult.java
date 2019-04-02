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

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Date;
import java.util.List;

class NetworkSearchResult<T extends Entity> {

    @SerializedName("results")
    private List<T> values;

    @SerializedName("resultCount")
    private int totalCount;

    //no-args constructor for GSON
    private NetworkSearchResult(){}

    NetworkSearchResult(NetworkSearchResult<T> source) {
        this(source.values, source.totalCount);
    }

    NetworkSearchResult(List<T> values, int totalCount) {
        this.values = Collections.unmodifiableList(values);
        this.totalCount = totalCount;
    }

    Date getFirstItemTimestamp() {
        return values.size() > 0 ? values.get(0).getTimestamp() : null;
    }

    /**
     * @return items of a {@linkplain SearchResult#getPageIndex() page} contained in this <code>SearchResult</code>
     */
    @NonNull
    public List<T> getItems() {
        return values;
    }

    /**
     * @return total number of entities which match search criteria specified with the {@link Query}
     */
    public int getTotalCount() {
        return totalCount;
    }
}
