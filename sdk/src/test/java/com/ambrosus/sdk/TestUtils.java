package com.ambrosus.sdk;

import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtils {

    public static InputStream getTestResource(Object caller, String resourceName){
        return caller.getClass().getResourceAsStream(resourceName);
    }

    public static InputStreamReader getTestResourceReader(Object caller, String resourceName) {
        return new InputStreamReader(getTestResource(caller, resourceName));
    }

}
