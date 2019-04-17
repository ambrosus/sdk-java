package com.ambrosus.sdk.utils;

import com.ambrosus.sdk.model.Identifier;

import org.junit.Test;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AmbrosusLinksParserTest {

    @Test
    public void ExtractAmbrosusIdentifiersTest() throws URISyntaxException {
        List<Identifier> identifiers = AmbrosusLinkParser.extractIdentifiers("http://amb.to/ean8/29932933/batchId/0043342");
        assertEquals(new Identifier("ean8", "29932933"), identifiers.get(0));
        assertEquals(new Identifier("batchId", "0043342"), identifiers.get(1));
    }

    @Test
    public void ExtractGS1IdentifiersTest() throws URISyntaxException {
        List<Identifier> identifiers = AmbrosusLinkParser.extractIdentifiers("https://roche.amb.to/01/09501101020917/17/190508/10/ABCD1234/21/10");
        assertEquals(new Identifier(Identifier.GTIN, "09501101020917"), identifiers.get(0));
        assertEquals(new Identifier(Identifier.LOT, "ABCD1234"), identifiers.get(1));
        assertEquals(new Identifier(Identifier.SERIAL, "10"), identifiers.get(2));
    }
}
