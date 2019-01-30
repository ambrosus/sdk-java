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

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.NetworkResultAdapter;
import com.ambrosus.sdk.RestrictedDataAccessException;
import com.ambrosus.sdk.SearchResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class EventAdapter implements NetworkResultAdapter<SearchResult<Event>, List<AMBEvent>> {

    private static final HashSet<String> AMBROSUS_SERVICE_EVENT_TYPES = new HashSet<String>(){
        {
            add("ambrosus.asset.redirection");
            add("ambrosus.asset.info");
            add("ambrosus.asset.identifiers");
        }
    };

    @Override
    public List<AMBEvent> convert(SearchResult<Event> source) throws RestrictedDataAccessException {
        List<AMBEvent> result = new ArrayList<>(source.getValues().size());
        for (Event sourceEvent : source.getValues()) {
            if(isValidSourceEvent(sourceEvent)) {
                result.add(new AMBEvent(sourceEvent));
            }
        }
        return result;
    }

    private static boolean isValidSourceEvent(Event event) throws RestrictedDataAccessException {
        //limiting output to non-service event
        List<String> dataTypes = event.getDataTypes();
        dataTypes.removeAll(AMBROSUS_SERVICE_EVENT_TYPES);
        //check if we have at least one data object of non-service ambrosus type
        return AmbrosusData.hasAmbosusData(dataTypes);
    }

}
