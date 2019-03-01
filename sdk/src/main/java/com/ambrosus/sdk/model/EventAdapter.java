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

package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.DataConverter;
import com.ambrosus.sdk.RestrictedDataAccessException;

import java.util.ArrayList;
import java.util.List;

class EventAdapter implements DataConverter<List<Event>, List<AMBEvent>> {

    private final boolean ignoreRestrictedEvents;

    EventAdapter(boolean ignoreRestrictedEvents) {
        this.ignoreRestrictedEvents = ignoreRestrictedEvents;
    }

    @Override
    public List<AMBEvent> convert(List<Event> source) throws RestrictedDataAccessException {
        List<AMBEvent> result = new ArrayList<>(source.size());
        for (Event sourceEvent : source) {
            try {
                if (isValidSourceEvent(sourceEvent)) {
                    result.add(new AMBEvent(sourceEvent));
                }
            } catch(RestrictedDataAccessException e) {
                if(!ignoreRestrictedEvents) throw e;
            }
        }
        return result;
    }

    private static boolean isValidSourceEvent(Event event) throws RestrictedDataAccessException {
        List<String> ambrosusDataTypes = AMBEvent.getAmbrosusDataTypes(event);
        ambrosusDataTypes.remove(AMBAssetInfo.DATA_OBJECT_TYPE_ASSET_INFO);
        return !ambrosusDataTypes.isEmpty();
    }

}
