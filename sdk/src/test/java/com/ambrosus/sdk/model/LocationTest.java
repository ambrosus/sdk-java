package com.ambrosus.sdk.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

public class LocationTest {

    @Test
    public void deserializeLocationTest(){
        try {
            try (InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("LocationData.json"))) {
                JsonObject locationDataJson = new Gson().fromJson(in, JsonObject.class);
                Location location = Location.createFrom(locationDataJson);
                //TODO: check fields
                System.out.println();
            }
        } catch (IOException e) {}

    }

}
