package com.ambrosus.sdk.utils;

import com.ambrosus.sdk.utils.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

class GS1ElementsReader {

    private static final Map<String, Integer> FIXED_SIZE_ELEMENTS_PREFIX = new HashMap<String, Integer>(){
        {
            put("00", 20);
            put("01", 16);
            put("02", 16);
            put("03", 16);
            put("04", 16);
            put("11", 8);
            put("12", 8);
            put("13", 8);
            put("14", 8);
            put("15", 8);
            put("16", 8);
            put("17", 8);
            put("18", 6);
            put("19", 8);
            put("20", 4);
            put("31", 10);
            put("32", 10);
            put("33", 10);
            put("34", 10);
            put("35", 10);
            put("36", 10);
            put("41", 16);
        }
    };

    private static final char GS_CONTROL_CHAR = 29; //0x1D
    private static final char FNC1_CHAR = 232;

    private static final List<Character> CONTROL_CHARS = Arrays.asList(GS_CONTROL_CHAR, FNC1_CHAR);

    private final String sequence;
    private int position = 0;

    static class IllegalDataFormatException extends Exception {
        IllegalDataFormatException(String s) {
            super(s);
        }
    }

    GS1ElementsReader(String sequence) throws IllegalDataFormatException {
        Assert.assertNotNull(sequence, "dmContent == null");

        if(getControlCharacterIndex(sequence, 0) != 0)
            throw new GS1ElementsReader.IllegalDataFormatException("Missing FNC1 or GS controls character at the beginning of the sequence.");
        this.sequence = sequence;

        Assert.assertTrue(hasNextElement(), IllegalDataFormatException.class, "Sequence doesn't contain any elements");
    }

    boolean hasNextElement() {
        skipControlCharacters();
        return position < sequence.length();
    }

    String getElement() throws IllegalDataFormatException {
        Assert.assertTrue(hasNextElement(), NoSuchElementException.class, "No more elements");

        skipControlCharacters();

        String element = getPreDefinedLengthElement(sequence, position);

        if(element == null) {
            element = getUndefinedLengthElement(sequence, position);
        }

        return element;
    }

    String nextElement() throws IllegalDataFormatException {
        String element = getElement();
        position += element.length();
        return element;
    }

    private void skipControlCharacters(){
        while (position < sequence.length()
                && (CONTROL_CHARS.indexOf(sequence.charAt(position)) != -1))
            position++;
    }

    private static String getPreDefinedLengthElement(String sequence, int position) throws IllegalDataFormatException {
        for (String fixedSizeElementPrefix : FIXED_SIZE_ELEMENTS_PREFIX.keySet()) {
            if(sequence.startsWith(fixedSizeElementPrefix, position)) {
                int elementLength = FIXED_SIZE_ELEMENTS_PREFIX.get(fixedSizeElementPrefix);
                if(sequence.length() - position >= elementLength) {
                    String elementString = sequence.substring(position, position + elementLength);
                    if(elementString.indexOf(GS_CONTROL_CHAR) != -1)
                        throw new GS1ElementsReader.IllegalDataFormatException(
                                String.format(
                                        "Element at position %d has pre-defined length but contains <GS> control character",
                                        position
                                )
                        );
                    return elementString;
                }
                else
                    throw new GS1ElementsReader.IllegalDataFormatException(
                            String.format(
                                    "Can't read \"%s\" element at position %d. Sequence string doesn't have enough characters.",
                                    fixedSizeElementPrefix,
                                    position
                            )
                    );
            }
        }
        return null;
    }

    private static String getUndefinedLengthElement(String sequence, int position){
        return sequence.substring(position, getControlCharacterIndex(sequence, position));
    }

    private static int getControlCharacterIndex(String sequence, int from) {
        int controlCharIndex = sequence.length();
        for (Character controlChar : CONTROL_CHARS) {
            int charIndex = sequence.indexOf(controlChar, from);
            if(charIndex != -1)
                controlCharIndex = Math.min(controlCharIndex, charIndex);
        }
        return controlCharIndex;
    }

}
