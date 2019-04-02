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

import java.util.Date;

/**
 * Creates {@linkplain Query queries} to search for {@linkplain Event events} which meet specific search criteria.
 * In order to get a search result you need to:
 * <ol>
 * <li> specify your search criteria with public methods of this builder
 * <li> create a query instance with {@link #build()} method
 * <li> pass this query instance to {@link Network#findEvents(Query)} or {@link Network#find(Query)} methods
 * </ol>
 * @see #createdBy(String)
 * @see #from(Date)
 * @see #to(Date)
 * @see #page(int)
 * @see #perPage(int)
 * @see #forAsset(String)
 * @see #byDataObjectType(String)
 * @see #byDataObjectField(String, String)
 * @see Network#findEvents(Query)
 * @see Network#find(Query)
 */
public class EventQueryBuilder extends GenericEventQueryBuilder<EventQueryBuilder, Event> {

    public EventQueryBuilder() {
        super(Event.class);
    }
}
