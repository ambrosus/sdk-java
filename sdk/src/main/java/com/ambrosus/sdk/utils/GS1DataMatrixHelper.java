package com.ambrosus.sdk.utils;


import com.ambrosus.sdk.model.Identifier;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;

public class GS1DataMatrixHelper {

    private static final Map<String, String> GS1_APP_IDENTIFIERS_MAP = new HashMap<String, String>() {
        {
            put("01", Identifier.GTIN);
            put("10", Identifier.LOT);
            put("21", Identifier.SERIAL);
        }
    };

    @NonNull
    public static List<Identifier> extractIdentifiers(@NonNull String sequence) throws IllegalDataFormatException {
        List<Identifier> result = new ArrayList<>();

        try {
            GS1ElementsReader reader = new GS1ElementsReader(sequence);
            while(reader.hasNextElement()) {
                String gs1Element = reader.nextElement();
                Identifier identifier = convertToAmbrosusIdentifier(gs1Element);
                if(identifier != null)
                    result.add(identifier);
            }
        } catch (GS1ElementsReader.IllegalDataFormatException e) {
            if(result.size() == 0) throw new IllegalDataFormatException(e);
        }
        return result;
    }
    
    static Identifier convertToAmbrosusIdentifier(String gs1Element) {
        for (String gsAppIdentifierKey : GS1_APP_IDENTIFIERS_MAP.keySet()) {
            if(gs1Element.startsWith(gsAppIdentifierKey) && gs1Element.length() > gsAppIdentifierKey.length()) {
                return new Identifier(
                        GS1_APP_IDENTIFIERS_MAP.get(gsAppIdentifierKey),
                        gs1Element.substring(gsAppIdentifierKey.length())
                );
            }
        }
        return null;
    }

    public static class IllegalDataFormatException extends Exception {
        IllegalDataFormatException(Throwable throwable) {
            super(throwable);
        }
    }

}
