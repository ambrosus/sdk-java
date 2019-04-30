package com.ambrosus.ambviewer

import android.util.Base64
import com.ambrosus.ambviewer.utils.deserializeFromString
import com.ambrosus.ambviewer.utils.serializeToString
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.Serializable
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
class SerializeTest {

    @Test
    fun testGetItems() {
        Base64Mock.mockAndroidBase64Encoding()

        val actual = TestItem("test", 5)
        val actualAsString = serializeToString(actual)
        assertEquals(actual, deserializeFromString(actualAsString))
    }

    data class TestItem(val str: String, val int: Int) : Serializable
}