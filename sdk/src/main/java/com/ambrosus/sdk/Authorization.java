/*
 * Copyright: Ambrosus Technologies GmbH
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

import android.util.Base64;

import com.ambrosus.sdk.utils.Strings;
import com.google.gson.JsonObject;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

class Authorization {

    private String authToken;

    void authorize(String privateKey, long durationMillis) {
        authToken = createAMBToken(privateKey, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + durationMillis));
    }

    String getAMBToken(){
        if(authToken == null)
            throw new IllegalStateException("You have to authorize first");
        return "AMB_TOKEN " + authToken;
    }

    static String getABMAuthHeader(String privateKey){
        return "AMB " + Strings.getWithHexPrefix(privateKey);
    }

     /**
     *
     * @param privateKeyStr private key as a hex string, can contain '0x' prefix
     * @param validUntil - token expiration date (integer in Unix Time format)
     * @return AMB_TOKEN
     */
    //package local for tests
    static String createAMBToken(String privateKeyStr, long validUntil) {
        BigInteger privateKey = Numeric.toBigInt(privateKeyStr/*can contain 0x prefix*/);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        String address = Keys.toChecksumAddress(Keys.getAddress(keyPair)); // account address associated with private key

        JsonObject idData = new JsonObject();
        idData.addProperty("createdBy", address);
        idData.addProperty("validUntil", validUntil);

        String signature = Ethereum.computeSignature(idData.toString(), keyPair);

        JsonObject token = new JsonObject();
        token.add("idData", idData);
        token.addProperty("signature", signature);

        String tokenString = token.toString();
        //TODO use different encoder for pure java version
        return Base64.encodeToString(tokenString.getBytes(), Base64.DEFAULT);
    }
}
