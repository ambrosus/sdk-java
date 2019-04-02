package com.ambrosus.sdk;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AmbrosusBlockchainService {

    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final Web3j web3j = Web3j.build(new HttpService("https://network.ambrosus-test.com"));

    private final ExecutorService executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,  new LinkedBlockingQueue<Runnable>());


    public Future<BigInteger> getBalance(final String address) {
        return perform(
                () -> web3j
                        .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                        .send()
                        .getBalance()
        );
    }

    public static String signTransaction(RawTransaction rawTransaction, final String privateKey) {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(privateKey));
        return Numeric.toHexString(signedMessage);
    }

    public Future<String> sendTransaction(RawTransaction rawTransaction, final String privateKey) {
        String signedTransaction = signTransaction(rawTransaction, privateKey);
        return sendSignedTransaction(signedTransaction);
    }

    public Future<String> sendSignedTransaction(String signedTransactionHex) {
        return perform(
                () -> web3j
                        .ethSendRawTransaction(signedTransactionHex)
                        .send()
                        .getTransactionHash()
        );
    }

    public Future<BigInteger> getNonce(String privateKey) {
        return perform(
                () -> web3j
                        .ethGetTransactionCount(Ethereum.getAddress(privateKey), DefaultBlockParameterName.LATEST)
                        .send()
                        .getTransactionCount()
        );
    }

    private <T> Future<T> perform(Callable<T> task) {
        return executor.submit(task);
    }
}
