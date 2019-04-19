package com.ambrosus.sdk;

import com.ambrosus.sdk.utils.Assert;
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

public class AssetTest {

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, RestrictedDataAccessException {
        Asset.Builder builder = new Asset.Builder();

        Asset expectedAsset = builder.createAsset(TestData.UNREGISTERED_PRIVATE_KEY);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(output);
        outputStream.writeObject(expectedAsset);
        outputStream.close();

        InputStream input = new ByteArrayInputStream(output.toByteArray());
        ObjectInputStream inputStream = new ObjectInputStream(input);

        Asset deserializedAsset = (Asset) inputStream.readObject();

        assertEquals(expectedAsset, deserializedAsset);
        assertEquals(expectedAsset.getTimestamp(), deserializedAsset.getTimestamp());
        assertEquals(expectedAsset.getAccountAddress(), deserializedAsset.getAccountAddress());
        assertEquals(expectedAsset.getSequenceNumber(), deserializedAsset.getSequenceNumber(), 0.00001);
    }
}
