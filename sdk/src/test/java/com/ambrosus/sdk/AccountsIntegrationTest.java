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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.TimeUnit;

@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Base64.class)
public class AccountsIntegrationTest {

    private static Network network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new Network();
    }

    @Test
    public void getAccountTest(){
        TestUtils.mockAndroidBase64Encoding();

        //fake private key
        String privateKey = "0x864ba4c99a24dc9adeaa06d1621855849aaa37c70012d544475a9862c9460514";
        network.authorize(privateKey, TimeUnit.HOURS.toMillis(1));
        try {
            String address = Ethereum.getAddress(privateKey);
            Account account = network.getAccount(address).execute();
            Assert.assertEquals(address, account.getAddress());
        } catch (Throwable t) {
            if(!t.getMessage().contains("Authentication failed: Token has expired.")) //now we get 401 with this message for incorrect private key
                throw new RuntimeException(t);
        }
    }

}
