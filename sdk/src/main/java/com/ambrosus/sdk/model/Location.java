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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Location {

    private final double latitude;
    private final double longitude;

    private final String name;
    private final String city;
    private final String country;

    Location(double latitude, double longitude, String name, String city, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    static Location createFrom(JsonObject dataObject) throws JsonParseException {
        try{
            JsonObject locationJson = dataObject.getAsJsonObject();

            JsonObject geoJson = locationJson.getAsJsonObject("location");
            JsonArray coords = geoJson.getAsJsonObject("geometry").getAsJsonArray("coordinates");

            String name = getStringValue(locationJson, "name");
            String city = getStringValue(locationJson, "city");
            String country = getStringValue(locationJson, "country");

            return new Location(
                    coords.get(0).getAsDouble(),
                    coords.get(1).getAsDouble(),
                    name,
                    city,
                    country
            );
        } catch(RuntimeException e) {
            throw new JsonParseException("Can't deserialize event", e);
        }
    }

    private static String getStringValue(JsonObject jsonObject, String key) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }
}
