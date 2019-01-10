package com.ambrosus.sdk.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class AMBEventTest {

    private static JsonObject dataObject;

    @BeforeClass
    public static void setupInput(){
        InputStreamReader in = new InputStreamReader(AMBEventTest.class.getResourceAsStream("AMBEventMainDataObject.json"));
        if(in != null) {
            try {
                dataObject = new Gson().fromJson(in, JsonObject.class);
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
        Map<String, JsonObject> images = AMBEvent.getEntityMap("images", dataObject);
        System.out.println();
    }

    @Test
    public void getAttributesMapTest(){
        Map<String, JsonElement> attributesMap = AMBEvent.getAttributesMap(dataObject);
        System.out.println();
    }

}
