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

import com.ambrosus.sdk.utils.GsonUtil;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AuthToken implements Serializable {

    private String signature;
    private IdData idData;

    //no-args constructor for GSON
    private AuthToken() {}

    private AuthToken(String signature, IdData idData) {
        this.signature = signature;
        this.idData = idData;
    }

    public String getAccount() {
        return idData.createdBy;
    }

    public Date getExpiration() {
        return new Date(TimeUnit.SECONDS.toMillis(idData.validUntil));
    }

    public static AuthToken create(String privateKey, long duration, TimeUnit durationUnit) throws NumberFormatException {
        return create(
                privateKey,
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                        +  durationUnit.toSeconds(duration)
        );
    }

    private static class IdData implements Serializable {

        private String createdBy;
        private long validUntil;

        //no-args constructor for GSON
        private IdData() {}

        private IdData(String createdBy, long validUntil) {
            this.createdBy = createdBy;
            this.validUntil = validUntil;
        }
    }

    /**
     *
     * @param privateKeyStr private key as a hex string, can contain '0x' prefix
     * @param validUntil - token expiration date (integer in Unix Time format)
     * @return AMB_TOKEN
     */
    //package local for tests
    static AuthToken create(String privateKeyStr, long validUntil) throws NumberFormatException {

        BigInteger privateKey;

        try {
            privateKey = Numeric.toBigInt(privateKeyStr/*can contain 0x prefix*/);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("private key isn't a valid hex string");
        }

        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        String address = Keys.toChecksumAddress(Keys.getAddress(keyPair)); // account address associated with private key

        IdData idData = new IdData(address, validUntil);
        return new AuthToken(Network.getObjectSignature(idData, privateKeyStr), idData);
    }
}
