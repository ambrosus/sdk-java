package com.ambrosus.sdk.utils;

import com.ambrosus.sdk.model.Identifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import io.reactivex.annotations.NonNull;

public class AmbrosusLinkParser {

    private static final Set<String> IDENTIFIERS_SET = new HashSet<String>() {
        {
            add(Identifier.EAN8);
            add(Identifier.EAN13);
            add(Identifier.GTIN);
            add(Identifier.LOT);
            add(Identifier.SERIAL);
            add(Identifier.BATCH_ID);
        }
    };

    @NonNull
    public static String getAssetID(@NonNull String url) throws URISyntaxException {
        URI uri = ensureUrl(url);

        String path = uri.getPath();
        if(!path.startsWith("/0x"))
            throw new URISyntaxException(url, "URL doesn't contain asset ID", url.indexOf(path)+1);

        return path.substring(1);
    }

    @NonNull
    public static List<Identifier> extractIdentifiers(@NonNull String url) throws URISyntaxException {
        URI uri = ensureUrl(url);

        String[] rawPathElements = uri.getPath().split("/");
        List<String> pathElements = new ArrayList<>();
        for (String rawPathElement : rawPathElements) {
            if(!rawPathElement.isEmpty())
                pathElements.add(rawPathElement);
        }

        if(pathElements.size() % 2 != 0) pathElements.remove(pathElements.size()-1);

        List<Identifier> result = new ArrayList<>();

        Iterator<String> elementsIterator = pathElements.iterator();
        while (elementsIterator.hasNext()) {
            String identifierType = elementsIterator.next();
            String identifierValue = elementsIterator.next();
            Identifier identifier = IDENTIFIERS_SET.contains(identifierType) ? new Identifier(identifierType, identifierValue)
                    : GS1DataMatrixHelper.convertToAmbrosusIdentifier(identifierType + identifierValue);
            if(identifier != null)
                result.add(identifier);
        }

        return result;
    }

    private static URI ensureUrl(String url) throws URISyntaxException {
        if(!(url.startsWith("http://") || url.startsWith("https://")))
            throw new URISyntaxException(url, "url must have http(s) SCHEME prefix", 0);

        URI uri = new URI(url);
        String host = uri.getHost();

        if(!host.endsWith("amb.to"))
            throw new URISyntaxException(url, "this host is not supported: " + host);

        return uri;
    }

}
