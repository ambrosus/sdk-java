package com.ambrosus.sdk;

import com.google.gson.Gson;

import org.junit.Test;

public class EventDeserializationTest {

    @Test
    public void deserializeEvent(){
        String eventJSON = "{\n" +
                "      \"eventId\": \"0xc5cfd04.....30755ed65\",\n" +
                "      \"content\": {\n" +
                "        \"signature\": \"0x30755ed65396facf86c53e6...65c5cfd04be400\",\n" +
                "        \"idData\": {\n" +
                "          \"assetId\": \"0xc5cfd04.....30755ed65\",\n" +
                "          \"createdBy\": \"0x162a44701727a31f457a53801cd181cd38eb5bbd\",\n" +
                "          \"timestamp\": 1503424923,\n" +
                "          \"dataHash\": \"0x01cd181cd38eb5bbd162a44701727a31f457a538\"\n" +
                "        },\n" +
                "        \"data\": [\n" +
                "          {\n" +
                "            \"type\": \"ambrosus.event.customevent\",\n" +
                "            \"customField\": \"customValue\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    }";

        Gson gson = new Gson();
        Event event = gson.fromJson(eventJSON, Event.class);
        System.out.println();
    }

}
