package com.ambrosus.ambrosussdk;

import com.ambrosus.ambrosussdk.model.AMBAsset;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parsingAsset_isCorrect() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("CowAsset" +
                ".json");

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        String fileText = result.toString("UTF-8");

        System.out.println("Start Test");
        assertTrue("File wasn't read.", fileText.length() > 0);


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        Gson gson = gsonBuilder.create();

        AMBAsset asset = gson.fromJson(fileText, AMBAsset.class);

        assertTrue("Asset wasn't parsed correctly.", asset != null);

        assertEquals
                ("0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2",
                        asset.getId());
    }
}