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

package com.ambrosus.sdk.utils;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UnixTime {

    public static long get(){
        return get(System.currentTimeMillis());
    }

    public static long get(long millis){
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    public static long get(@NonNull Date date) {
        return get(date.getTime());
    }

    public static long getMillis(long unixTime) {
        return  TimeUnit.SECONDS.toMillis(unixTime);
    }

    public static Date toDate(long unixTime) {
        return new Date(getMillis(unixTime));
    }

}
