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

package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.Event;

import java.util.ArrayList;
import java.util.List;

class AmbrosusData {

    private static final String AMBROSUS_OBJECT_TYPE_PREFIX = "ambrosus.asset.";

    static boolean hasAmbosusData(List<String> dataTypes){
        return !getAmbrosusDataTypes(dataTypes).isEmpty();
    }

    @NonNull
    static List<String> getAmbrosusDataTypes(List<String> dataTypes){
        List<String> result = new ArrayList<>();
        for (String dataType : dataTypes) {
            if(dataType.startsWith(AMBROSUS_OBJECT_TYPE_PREFIX))
                result.add(dataType);
        }
        return result;
    }
}
