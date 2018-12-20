package com.ambrosus.ambrosussdk

import com.ambrosus.ambrosussdk.model.AMBAsset
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambrosussdk.utils.SectionFormatter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

class AndroidModelTest {

    @Test
    fun testsWork() {
        assertTrue(true)
    }

    fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }

    @Test
    @Throws(IOException::class)
    fun parsingAsset_isCorrect() {

        val fileText = this.javaClass.getResource("/" + "CowAsset"
                + ".json")
                .readText()

        println("Start Test")
        assertTrue("File wasn't read.", fileText.length > 0)

        val gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, Any>>() {

        }.rawType

        val jsonMap = gson.fromJson(fileText, type) as? Map<String, Any>

        assertTrue("JSON wasn't parsed", jsonMap != null)
        if (jsonMap != null) {
            val asset = AMBAsset(jsonMap)
            assertEquals("hello",
                    "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2",
                    asset.id)
        }
    }


    @Test
    @Throws(IOException::class)
    fun parsingEvents_isCorrect() {

        val fileText = this.javaClass.getResource("/" + "CowEvents"
                + ".json")
                .readText()

        assertTrue("File wasn't read.", fileText.length > 0)

        val gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, Any>>() {

        }.rawType

        val jsonMap = gson.fromJson(fileText, type) as? Map<String, Any>

        assertTrue("JSON wasn't parsed", jsonMap != null)
        if (jsonMap != null) {
            val events = AMBEvent.fetchEvents(jsonMap)
            assertEquals("Wrong number of events",
                    3,
                    events.size)
        }
    }


    @Test
    @Throws(IOException::class)
    fun parsingLatestEvents1_isCorrect() {

        val fileText = this.javaClass.getResource("/" + "LatestEventsResponse"
                + ".json")
                .readText()

        assertTrue("File wasn't read.", fileText.length > 0)

        val gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, Any>>() {

        }.rawType

        val jsonMap = gson.fromJson(fileText, type) as? Map<String, Any>

        assertTrue("JSON wasn't parsed", jsonMap != null)
        if (jsonMap != null) {
            val events = AMBEvent.fetchEvents(jsonMap)
            val info = events.first { it.assetInfo != null }
            assertTrue(events.size > 3)
        }
    }

    fun hexToByteArray(hex: String): ByteArray {
        var hex = hex
        hex = if (hex.length % 2 != 0) "0$hex" else hex

        val b = ByteArray(hex.length / 2)

        for (i in b.indices) {
            val index = i * 2
            val v = Integer.parseInt(hex.substring(index, index + 2), 16)
            b[i] = v.toByte()
        }
        return b
    }


    @Test
    @Throws(IOException::class)
    fun parsingSections_isCorrect() {

        val fileText = this.javaClass.getResource("/" + "FlattenEventsResponse"
                + ".json")
                .readText()

        assertTrue("File wasn't read.", fileText.length > 0)

        val gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, Any>>() {

        }.rawType

        val jsonMap = gson.fromJson(fileText, type) as? Map<String, Any>

        assertTrue("JSON wasn't parsed", jsonMap != null)
        if (jsonMap != null) {
            val formattedSections = SectionFormatter.getFormattedSections(jsonMap)
            println(formattedSections.size)
            assertTrue(formattedSections.size > 3)
        }
    }


    @Test
    @Throws(IOException::class)
    fun parsingLocation_isCorrect() {

        val fileText = this.javaClass.getResource("/" + "LocationEventsResponse"
                + ".json")
                .readText()

        assertTrue("File wasn't read.", fileText.length > 0)

        val gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, Any>>() {

        }.rawType

        val jsonMap = gson.fromJson(fileText, type) as? Map<String, Any>

        assertTrue("JSON wasn't parsed", jsonMap != null)
        if (jsonMap != null) {
            val event = AMBEvent(jsonMap)

            assertTrue("Unable to parse the event!", event.locationName !=
                    null)

        }
    }
}