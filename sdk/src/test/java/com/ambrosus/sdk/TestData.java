/*
 * Copyright: Ambrosus Inc.
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.sdk;

import com.google.gson.Gson;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

public class TestData {

    public static final String UNREGISTERED_PRIVATE_KEY = "0x864ab5c99a14dc9adeaa06d1621855849aaa37c70012d544475a9862c9460515";
    public static final String UNREGISTERED_ACCOUNT_ADDRESS = Ethereum.getAddress(UNREGISTERED_PRIVATE_KEY);

    private static InputStream getTestResource(Class callerClass, String resourceName){
        return callerClass.getResourceAsStream(resourceName);
    }

    static AuthToken getAuthToken(){
        return getFromJson(TestData.class, AuthToken.class, "AuthToken.json");
    }

    static <T> T getFromJson(Class callerClass, Class<T> resultType, String jsonResource) {
        try(InputStreamReader in = getTestResourceReader(callerClass, jsonResource)) {
            return Json.fromJson(in, resultType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static InputStreamReader getTestResourceReader(Class callerClass, String resourceName) {
        return new InputStreamReader(getTestResource(callerClass, resourceName));
    }
}
