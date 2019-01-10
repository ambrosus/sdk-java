package com.ambrosus.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EventDeserializationTest {

    @Test
    public void deserializeEvent(){
        try(InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("SingleEvent.json"))) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Event.class, new EventDeserializer());
            Gson gson = builder.create();
            Event event = gson.fromJson(in, Event.class);
            //TODO: check event fields values
            System.out.println();
        } catch (IOException e) {}

    }

}
