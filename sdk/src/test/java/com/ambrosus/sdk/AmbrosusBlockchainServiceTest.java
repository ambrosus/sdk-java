package com.ambrosus.sdk;

import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AmbrosusBlockchainServiceTest {

    private static AmbrosusBlockchainService service;

    @BeforeClass
    public static void setupService(){
        service = new AmbrosusBlockchainService();
    }

    @Test
    public void getBalanceTest() throws ExecutionException, InterruptedException {
        Future<BigInteger> futureBalance = service.getBalance("0xFF1E60D7e4fe21C1817B8249C8cB8E52D1912665");
        BigInteger balance = futureBalance.get();
        System.out.println(balance);
    }

    public void sendAmberTest() throws ExecutionException, InterruptedException {
        String privateKey = "PutYourKey";
        String hash = service.send(
                privateKey,
                "0x8105455eab3616fed1e3fe1c2d006efa52e98c3a",
                BigInteger.valueOf(1000000000000L)
        ).get();
        System.out.println(hash);
    }
}
