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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GenericEventSearchParamsBuilder<T extends GenericEventSearchParamsBuilder> {

    private final Map<String, String> queryParams = new HashMap<>();

    @NonNull
    public T from(long timestamp) {
        QueryParamsHelper.addFrom(queryParams, timestamp);
        return (T) this;
    }

    @NonNull
    public T to(long timestamp) {
        QueryParamsHelper.addTo(queryParams, timestamp);
        return (T) this;
    }

    @NonNull
    public T createdBy(@NonNull String accountAddress) {
        QueryParamsHelper.addCreatedBy(queryParams, Assert.assertNotNull(accountAddress, "accountAddress == null"));
        return (T) this;
    }

    @NonNull
    public T forAsset(@NonNull String assetId) {
        queryParams.put("assetId", Assert.assertNotNull(assetId, "assetId == null"));
        return (T) this;
    }


    @NonNull
    public T byDataObjectField(@NonNull String fieldName, @NonNull String fieldValue) {
        
        String queryKey = String.format(Locale.US, "data[%s]", Assert.assertNotNull(fieldName, "fieldName == null"));
        
        //TODO add integration unit test to ensure that sever still doesn't allow to search for several values in the same field name
        String existingValue = queryParams.get(queryKey);
        if(existingValue != null)             
            throw new IllegalStateException(String.format(Locale.US, "You have already specified value for field %s (%s)", fieldName, existingValue));
        
        queryParams.put(queryKey, Assert.assertNotNull(fieldValue, "fieldValue == null"));
        return (T) this;
    }

    @NonNull
    public T byDataObjectType(@NonNull String type) {
        byDataObjectField(Event.DATA_OBJECT_ATTR_TYPE, type);
        return (T) this;
    }

    @NonNull
    public EventSearchParams build(){
        return new EventSearchParams(queryParams);
    }

}
