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

import com.ambrosus.sdk.utils.UnixTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a query which can be used to search for entities of <code>T</code> type.
 * For example you can {@linkplain Network#findEvents(Query) search for Events} using a <code>Query</code> instance parameterized with {@link Event} type ({@code Query<Event>}).
 * You can also use {@link Network#find(Query)} method to search for any entities supported by a {@link Network} implementation.
 * <p>
 * Each <code>Query</code> encapsulates search criteria and index of the page of overall search result which you can get using it.
 * You can use any subclass of {@link AbstractQueryBuilder} class to build a query.
 * As soon as you get a {@link SearchResult} instance with content of the first page
 * you can use {@link PageQueryBuilder} to build queries which will give you content of other pages.
 *
 * @param <T> type of the entities which can be found with this <code>Query</code> instance
 * @see AssetQueryBuilder
 * @see EventQueryBuilder
 * @see Network#findEvents(Query)
 * @see Network#findAssets(Query)
 * @see Network#find(Query)
 * @see GenericEventQueryBuilder
 * @see SearchResult
 * @see PageQueryBuilder
 */
public final class Query<T extends Entity> implements Serializable {

    /**
     * A {@link Class} instance for type of the data model which can be found with this <code>Query</code>
     */
    public final Class<T> resultType;
    private final Query.Params params;

    Query(Class<T> resultType, Params params) {
        this.resultType = resultType;
        this.params = params.copy();
    }

    Params getParams() {
        return params.copy();
    }

    @Nullable Integer getPageSize() {
        return AbstractQueryBuilder.getPageSize(params);
    }

    int getPage() {
        return AbstractQueryBuilder.getPage(params);
    }

    Map<String, String> asMap() {
        return params.asMap();
    }

    static class Params implements Serializable {

        private final Map<String, String> map;

        private Params(Map<String, String> map) {
            this.map = map;
        }

        Params() {
            this(new HashMap<>());
        }

        void set(String key, String value) {
            map.put(key, value);
        }

        void set(String key, Integer value) {
            map.put(key, Integer.toString(value));
        }

        void set(String key, Date date) {
            set(key, Long.toString(UnixTime.get(date)));
        }

        void remove(String key) {
            map.remove(key);
        }

        String getString(String key) {
            return map.get(key);
        }

        Integer getInt(String key) {
            return getInt(key, null);
        }

        Integer getInt(String key, Integer defaultValue) {
            String value = map.get(key);
            return value != null ? Integer.valueOf(value) : defaultValue;
        }

        public Params copy() {
            return new Params(new HashMap<>(map));
        }

        Map<String, String> asMap() {
            return Collections.unmodifiableMap(map);
        }

    }
}
