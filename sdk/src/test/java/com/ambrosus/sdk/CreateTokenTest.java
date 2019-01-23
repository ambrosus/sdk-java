package com.ambrosus.sdk;


import com.google.gson.JsonObject;

import org.junit.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CreateTokenTest {

    @Test
    public void createToken(){
        final String validToken = "eyJpZERhdGEiOnsiY3JlYXRlZEJ5IjoiMHg5QTNEYjkzNmM5NDUyM2NlYjFDY0M2QzkwNDYxYmMzNGE0NkU5ZGZFIiwidmFsaWRVbnRpbCI6MTU3NzgzNjgwMH0sInNpZ25hdHVyZSI6IjB4NTEzYmNmNzMxZTgwOTY1NzY4MTA4OTg1ZWE2YzZlM2ViMzRjMGUxMGM5MWJiZjM3N2M4ZGFjZTM1NWIyZWUyMDU3MjdmMWU3Yzc4ZWU4ZjMyMGZhZTgwMTYwMTViMTBlMDJiOTNmNTJkNmZjNWNiYTlmNWE1Y2UyM2QzNGYwYzIxYyJ9";

        //stole this from sdk-ios-apps\AmbrosusViewer\App\SampleFetcher.swift
        BigInteger privateKey = Numeric.toBigInt("0xc104ec10ff80d8011b972470fe2e60fa968149a16c61a42b4d15067c29ff4e4c"/*can contain 0x prefix*/);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        String address = Keys.toChecksumAddress(Keys.getAddress(keyPair)); // account address associated with private key

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(2020, 0, 1);
        long validUntil = calendar.getTimeInMillis() / 1000;

        JsonObject idData = new JsonObject();
        idData.addProperty("createdBy", address);
        idData.addProperty("validUntil", validUntil);

        String signature = CryptoUtils.computeSignature(idData.toString(), keyPair);

        JsonObject token = new JsonObject();
        token.add("idData", idData);
        token.addProperty("signature", signature);

        String resultToken = Base64.getEncoder().encodeToString(token.toString().getBytes());

        assertEquals(validToken, resultToken);

        //result - {"idData":{"createdBy":"0x9A3Db936c94523ceb1CcC6C90461bc34a46E9dfE","validUntil":1560514367},"signature":"0x68a9fc832c3311cd6b637cca9b615ad5f1b2ca1456bdbb78bcd71131bc4c087e2b7fd2d85bd8a363a900a93ff833b4aa79bc85a0b4bcb8f276134e19dc1f3e8b1c"}

        System.out.println();
    }
}
