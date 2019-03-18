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


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class AccountsIntegrationTest {

    private static Network network;

    @Before
    public void setUpNetwork(){
        network = new Network();
    }

    @Test
    public void getAccountTest() throws Throwable {
        AuthToken authToken = TestData.getAuthToken();
        network.authorize(authToken);
        Account account = network.getAccount(authToken.getAccountAddress()).execute();
        Assert.assertEquals(authToken.getAccountAddress(), account.getAddress());
    }

    @Test(expected = EntityNotFoundException.class)
    public void getAccountTest_for_not_existing_account() throws Throwable {
        network.authorize(TestData.getAuthToken());
        network.getAccount(TestData.UNREGISTERED_ACCOUNT_ADDRESS).execute();
    }

    @Test(expected = PermissionDeniedException.class)
    public void getAccountTest_authorized_with_not_existing_account() throws Throwable {
        AuthToken authToken = AuthToken.create(TestData.UNREGISTERED_PRIVATE_KEY, 5, TimeUnit.DAYS);
        network.authorize(authToken);
        network.getAccount(TestData.getAuthToken().getAccountAddress()).execute();
    }


}
