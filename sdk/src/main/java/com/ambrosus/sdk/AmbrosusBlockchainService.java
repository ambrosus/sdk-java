package com.ambrosus.sdk;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
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

    public Future<String> send(final String privateKey, final String toAddress, final BigInteger value){
        return perform(
                () -> {
                    BigInteger nonce = getNonce(Ethereum.getAddress(privateKey)).get();
                    BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                    gasPrice = gasPrice.add(gasPrice.divide(BigInteger.valueOf(10)));

                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                            nonce, gasPrice, Transfer.GAS_LIMIT, toAddress, value);

                    return sendTransaction(rawTransaction, privateKey).get();
                }
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

    public Future<BigInteger> getNonce(String address) {
        return perform(
                () -> web3j
                        .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .send()
                        .getTransactionCount()
        );
    }

    public Future<Transaction> loadContract(final String address, BigInteger value, String abi) {
        return perform(
                () -> {
                    BigInteger nonce = getNonce(address).get();
                    return Transaction.createContractTransaction(
                            address,
                            nonce,
                            BigInteger.valueOf(4700000),
                            Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger(),
                            value,
                            abi);
                }
        );
    }

    private <T> Future<T> perform(Callable<T> task) {
        return executor.submit(task);
    }
}
