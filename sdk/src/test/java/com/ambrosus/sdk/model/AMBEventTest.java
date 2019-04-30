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
import com.ambrosus.sdk.RestrictedDataAccessException;
import com.ambrosus.sdk.TestData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class AMBEventTest {

    private static AMBEvent ambEvent;

    @BeforeClass
    public static void setupInput() throws RestrictedDataAccessException {
        InputStreamReader in = new InputStreamReader(AMBEventTest.class.getResourceAsStream("AMBEventMainDataObject.json"));
        if(in != null) {
            try {
                Event basicEvent = new Event.Builder("")
                        .addData(
                                "ambrosus.asset.shipped",
                                new Gson().fromJson(in, JsonObject.class)
                        ).createEvent(TestData.UNREGISTERED_PRIVATE_KEY);
                ambEvent = new AMBEvent(basicEvent);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }
        else throw new IllegalStateException("Missing input data");
    }

    @Test
    public void getEntityMapTest(){
        Map<String, JsonObject> images = ambEvent.getImages();
        System.out.println();
    }

    @Test
    public void getAttributesMapTest(){
        Map<String, JsonElement> attributesMap = ambEvent.getAttributes();
        System.out.println();
    }

}
