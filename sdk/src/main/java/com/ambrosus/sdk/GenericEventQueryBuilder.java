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
 * This is a generic implementation of event {@linkplain Query queries} builder.
 * You can extend it in order to create a builder of {@linkplain Query queries} for you custom data model.
 * {@link com.ambrosus.sdk.model.AMBEventQueryBuilder AMBEventQueryBuilder} can be used as a sample of such implementation.
 */
@SuppressWarnings("unchecked")
public class GenericEventQueryBuilder<BuilderType extends GenericEventQueryBuilder<BuilderType, QueryType>, QueryType extends Event> extends AbstractQueryBuilder<BuilderType, QueryType> {

    /**
     * @see AbstractQueryBuilder#AbstractQueryBuilder(Class)
     */
    protected GenericEventQueryBuilder(Class<QueryType> queryType) {
        super(queryType);
    }

    /**
     * Configures query to search for {@linkplain Event events} with certain {@linkplain Event#getAssetId() asset identifier}
     * @param assetId specifies {@linkplain Asset#getSystemId() unique identifier} of the {@link Asset} to which events have to be connected
     * @return this builder instance
     */
    @NonNull
    public BuilderType forAsset(@NonNull String assetId) {
        params.set("assetId", Assert.assertNotNull(assetId, "assetId == null"));
        return (BuilderType) this;
    }

    /**
     * You can query {@linkplain Event events} by value of the field inside {@linkplain Event#getUserData() user data list}.
     * Let's assume that there is an {@link Event} which contains following JSON object in the {@linkplain Event#getUserData() user data list}:
     * <pre>{@code
     * {
     *   "type": "com.user.custom",
     *   "dataField": "dataFieldValue",
     *   "nestedObject": {
     *     "nestedField": "nestedFieldValue"
     *   }
     * }}</pre>
     * In order to create a {@link Query} for all events containing
     * an object with "dataField" field which value is equal to "dataFieldValue"
     * you need to configure EventQueryBuilder in the following way:
     * <pre>{@code
     * Query<Event> query = new EventQueryBuilder().byDataObjectField("dataField", "dataFieldValue").build(); }</pre>
     * It's also possible to create a {@link Query} for events by the value of "nestedField" of this data object:
     * <pre>{@code
     * Query<Event> query = new EventQueryBuilder().byDataObjectField("nestedObject.nestedField", "nestedFieldValue").build();}</pre>
     *
     * @param fieldName name of the field of a data object inside {@linkplain Event#getUserData() user data list}.
     * @param fieldValue value which must contain field specified by <code>fieldName</code> parameter in order to match this search criteria
     * @return this builder instance
     */
    @NonNull
    public BuilderType byDataObjectField(@NonNull String fieldName, @NonNull String fieldValue) {
        
        String queryKey = String.format(Locale.US, "data[%s]", Assert.assertNotNull(fieldName, "fieldName == null"));
        
        //TODO add integration unit test to ensure that sever still doesn't allow to search for several values in the same field name
        String existingValue = params.getString(queryKey);
        if(existingValue != null)             
            throw new IllegalStateException(String.format(Locale.US, "You have already specified value for field %s (%s)", fieldName, existingValue));
        
        params.set(queryKey, Assert.assertNotNull(fieldValue, "fieldValue == null"));
        return (BuilderType) this;
    }

    @NonNull
    public BuilderType byDataObjectType(@NonNull String type) {
        byDataObjectField(Event.DATA_OBJECT_ATTR_TYPE, type);
        return (BuilderType) this;
    }
}
