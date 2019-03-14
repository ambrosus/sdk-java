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

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

abstract class Ethereum {

    private static final String ETH_PREAMBLE = "\u0019Ethereum Signed Message:\n";
    private static final int ECDSA_OUTPUT_LENGTH = 32;


    /**
     * Computes the signature of a string message with the provided key pair.
     *
     * @param message The message to be signed.
     * @param keyPair The key pair to use for signing.
     * @return A string containing the hexadecimal representation of the signature
     */
    public static String computeSignature(final String message, final ECKeyPair keyPair) {

        Sign.SignatureData data = Sign.signMessage(
                computeHash(message),
                keyPair,
                false); // set to false because there is no need to hash the message

        byte[] r = data.getR();
        byte[] s = data.getS();
        byte v = data.getV();

        // Concatenate byte arrays
        byte[] rsv = new byte[2 * ECDSA_OUTPUT_LENGTH + 1];
        System.arraycopy(r, 0, rsv, 0, ECDSA_OUTPUT_LENGTH);
        System.arraycopy(s, 0, rsv, ECDSA_OUTPUT_LENGTH, ECDSA_OUTPUT_LENGTH);
        rsv[rsv.length - 1] = v;

        return Numeric.toHexString(rsv);
    }


    /**
     * Computes the Ethereum hash of the provided message string. It consists of a Keccak256 hash of the
     * message string prefixed with "\u0019Ethereum Signed Message:\n" and the length of the message.
     *
     * @param message The message to be hashed.
     * @return A byte array representing the hash.
     */
    private static byte[] computeHash(final String message) {

        return Hash.sha3((ETH_PREAMBLE + message.length() + message).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Computes the Ethereum hash of the provided message string. It consists of a Keccak256 hash of the
     * message string prefixed with "\u0019Ethereum Signed Message:\n" and the length of the message.
     *
     * @param message The message to be hashed.
     * @return A string with the hexadecimal representation of the hash.
     */
    public static String computeHashString(final String message) {

        return Numeric.toHexString(computeHash(message));
    }


    /**
     * Recovers the address of the message signatory and compares it to a given address. The address is recovered by
     * computing the hash of the message and using the signature provided in the method parameters.
     *
     * @param message          The message that was hashed and signed.
     * @param candidateAddress The address to be verified.
     * @param hexSignature     The signature of the message, as a hexadecimal string.
     * @return True if the address recovered from the message and the signature matches the candidate address. Returns
     * false otherwise.
     */
    public static boolean signatureMatches(final String message, final String candidateAddress, final String
            hexSignature) {

        byte[] hash = computeHash(message);
        byte[] signature = Numeric.hexStringToByteArray(hexSignature);

        BigInteger r = Numeric.toBigInt(Arrays.copyOfRange(signature, 0, ECDSA_OUTPUT_LENGTH));
        BigInteger s = Numeric.toBigInt(Arrays.copyOfRange(signature, ECDSA_OUTPUT_LENGTH, 2 * ECDSA_OUTPUT_LENGTH));
        int v = signature[signature.length - 1] - 27;

        return candidateAddress.equals(
                Keys.toChecksumAddress(
                        Keys.getAddress(
                                Sign.recoverFromSignature(v, new ECDSASignature(r, s), hash)
                        )
                )
        );
    }


    /**
     * Compute the Ethereum Keccak256 hash of the given message and compares it to a given hash.
     *
     * @param message       The message to be hashed.
     * @param candidateHash The hash to be verified, as a hexadecimal string.
     * @return True if hashing the message with the Ethereum Keccak256 hash function yields the same value as the
     * hash given in parameter. Returns false otherwise.
     */
    public static boolean hashMatches(final String message, final String candidateHash) {

        return computeHashString(message).equals(candidateHash);
    }

    /**
     *
     * @param privateKey private key hex string, can contain 0x prefix
     * @return
     *
     * @throws IllegalArgumentException if {@code privateKey} is not a valid hex string
     */
    public static String getAddress(String privateKey) throws IllegalArgumentException {
        ECKeyPair keyPair = getEcKeyPair(privateKey);
        return Keys.toChecksumAddress(Keys.getAddress(keyPair));
    }

    @NonNull
    static ECKeyPair getEcKeyPair(String privateKey) throws IllegalArgumentException {
        try {
            BigInteger privateKeyNumber = Numeric.toBigInt(privateKey/*can contain 0x prefix*/);
            return ECKeyPair.create(privateKeyNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("privateKey isn't a valid hex string");
        }
    }


}
