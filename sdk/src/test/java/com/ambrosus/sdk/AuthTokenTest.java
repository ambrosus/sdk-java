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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class AuthTokenTest {

    @Test
    public void getAsStringTest(){
        //fake private key
        final String privateKey = "0xc104ec10ff80d8111b972470fe2e61fa960149a16c01a4214d15167c29ff4e4c";
        final String validTokenString = "eyJpZERhdGEiOnsiY3JlYXRlZEJ5IjoiMHhmRGJmQjJENTc1NTBkN0QwNTQxNzA0NzkyRjJhNzc4N0ZhOUZGMkFhIiwidmFsaWRVbnRpbCI6MTU3NzgzNjgwMH0sInNpZ25hdHVyZSI6IjB4MjI1NGM0MDlhZTg0MTg0OTViYmUyYWNjNjY1MmY4OTQ2OGMwNGIyYmE4OTM2OTJmMWNmOGVhZjE2NTZkMmQxZDQ1MjUwZDFmMThjYWQzZTM2NGFlNGRjNTYwZjIxNjk2NGVlZTNhMGNmMGIxYmQ4MDliMjIwYThkZjNhMTZkMTkxYyJ9";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(2020, 0, 1);
        long validUntil = calendar.getTimeInMillis() / 1000;

        AuthToken authToken = AuthToken.create(privateKey, validUntil);

        assertEquals(validTokenString, authToken.getAsString());
    }

    @Test
    public void createAuthTokenFromString(){
        String tokenString = "eyJpZERhdGEiOnsiY3JlYXRlZEJ5IjoiMHhmRGJmQjJENTc1NTBkN0QwNTQxNzA0NzkyRjJhNzc4N0ZhOUZGMkFhIiwidmFsaWRVbnRpbCI6MTU3NzgzNjgwMH0sInNpZ25hdHVyZSI6IjB4MjI1NGM0MDlhZTg0MTg0OTViYmUyYWNjNjY1MmY4OTQ2OGMwNGIyYmE4OTM2OTJmMWNmOGVhZjE2NTZkMmQxZDQ1MjUwZDFmMThjYWQzZTM2NGFlNGRjNTYwZjIxNjk2NGVlZTNhMGNmMGIxYmQ4MDliMjIwYThkZjNhMTZkMTkxYyJ9";
        AuthToken authToken = AuthToken.create(tokenString);
        Assert.assertEquals(tokenString, authToken.getAsString());
    }

}
