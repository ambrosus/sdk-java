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

import com.ambrosus.sdk.utils.Assert;
import com.ambrosus.sdk.utils.UnixTime;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okio.ByteString;

public class AuthToken extends SignedContent<AuthToken.AuthTokenIdData> implements Serializable {

    public String getAccountAddress() {
        return idData.getAccountAddress();
    }

    public Date getExpiration() {
        return UnixTime.toDate(idData.validUntil);
    }

    private AuthToken(AuthTokenIdData idData, String privateKey) {
        super(idData, privateKey);
    }

    public String getAsString() {
        return ByteString.encodeUtf8(Json.getLexNormalizedJsonStr(this)).base64();
    }

    public static AuthToken create(String privateKey, long duration, TimeUnit durationUnit) throws NumberFormatException {
        return create(
                privateKey,
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                        +  durationUnit.toSeconds(duration)
        );
    }

    public static AuthToken create(String tokenString) {
        ByteString byteString = ByteString.decodeBase64(tokenString);
        Assert.assertNotNull(byteString, IllegalArgumentException.class, "tokenString is not a valid Base64 encoded string");
        String s = byteString.utf8();
        try {
            AuthToken authToken = Json.fromJson(s, AuthToken.class);
            Assert.assertTrue(authToken.matchesSignature(), IllegalArgumentException.class, "authToken content doesn't matches it's signature");
            return authToken;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("tokenString is not a valid Base64 encoded AuthToken", e);
        }
    }

    static class AuthTokenIdData extends AccountData implements Serializable {

        private long validUntil;

        private AuthTokenIdData(String createdBy, long validUntil) {
            super(createdBy);
            this.validUntil = validUntil;
        }
    }

    /**
     *
     * @param privateKey private key as a hex string, can contain '0x' prefix
     * @param validUntil - token expiration date (integer in Unix Time format)
     * @return AMB_TOKEN
     */
    //package local for tests
    static AuthToken create(String privateKey, long validUntil) throws NumberFormatException {
        AuthTokenIdData idData = new AuthTokenIdData(Ethereum.getAddress(privateKey), validUntil);
        return new AuthToken(idData, privateKey);
    }
}
