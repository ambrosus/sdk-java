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

import java.lang.Exception;
import java.util.Locale;

/**
 * Indicates that request to Ambrosus Node API had failed with some HTTP error.
 * SDK is designed to handle all possible error responses which can be caused by client-side errors
 * and throw an instance of @{link AmbrosusException} in that case. So an instance of NetworkException can be thrown only
 * in the case of some error in SDK/Backend implementation.
 *
 * Detailed error message should be available with {@link #getMessage()} method. You can also check HTTP error code with {@link #code} field.
 */
public class RequestFailedException extends RuntimeException {

    /**
     * HTTP response status code
     */
    public final int code;

    public RequestFailedException(int code, String message) {
        super(message != null ? message : String.format(Locale.US, "Request failed with %d status code.", code));
        this.code = code;
    }

}
