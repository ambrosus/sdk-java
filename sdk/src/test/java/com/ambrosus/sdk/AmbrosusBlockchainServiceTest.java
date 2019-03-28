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
        Future<BigInteger> futureBalance = service.getBalance("0x377d71a0a2e044c4fce7e3a59ebeb736da055be3");
        BigInteger balance = futureBalance.get();
        System.out.println(balance);
    }
}
