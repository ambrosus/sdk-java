package com.ambrosus.sdk.model;

import com.ambrosus.sdk.utils.GsonUtil;
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

            String name = GsonUtil.getStringValue(locationJson, "name");
            String city = GsonUtil.getStringValue(locationJson, "city");
            String country = GsonUtil.getStringValue(locationJson, "country");

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
}
