package com.ambrosus.sdk;

import com.ambrosus.sdk.utils.UnixTime;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;

public class EventTest {

    @Test
    public void testSerialization() throws IOException, RestrictedDataAccessException, ClassNotFoundException {
        Event.Builder builder = new Event.Builder("");

        JsonObject data = new JsonObject();
        data.addProperty("testKey", "testValue");
        builder.addData("test", data);

        Event expectedEvent = builder.createEvent(TestData.UNREGISTERED_PRIVATE_KEY);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(output);
        outputStream.writeObject(expectedEvent);
        outputStream.close();

        InputStream input = new ByteArrayInputStream(output.toByteArray());
        ObjectInputStream inputStream = new ObjectInputStream(input);

        Event deserializedEvent = (Event) inputStream.readObject();

        Event.Builder actualEventBuilder
                = new Event.Builder(deserializedEvent.getAssetId())
                .setUnixTimeStamp(UnixTime.get(deserializedEvent.getTimestamp().getTime()));

        for (String dataType : deserializedEvent.getDataTypes()) {
            actualEventBuilder.addData(dataType, deserializedEvent.getDataObject(dataType));
        }

        assertEquals(expectedEvent, actualEventBuilder.createEvent(TestData.UNREGISTERED_PRIVATE_KEY));
    }

}
