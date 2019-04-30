package com.ambrosus.sdk.utils;

import com.ambrosus.sdk.utils.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class GS1ElementsReaderTest {

    @Test
    public void test() throws GS1ElementsReader.IllegalDataFormatException {
        String dm1 = "\u00e801000123456799951010JA28A\u00e817121231211234567890180";

        GS1ElementsReader parser = new GS1ElementsReader(dm1);

        assertEquals("0100012345679995", parser.nextElement());
        assertEquals("1010JA28A", parser.nextElement());
        assertEquals("17121231", parser.nextElement());
        assertEquals("211234567890180", parser.nextElement());

    }
}
