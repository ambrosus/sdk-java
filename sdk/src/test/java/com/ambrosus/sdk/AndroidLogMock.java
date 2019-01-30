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

import android.util.Log;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 * Usage:
 *
 *     @PrepareForTest({Log.class})
 *
 *     @Before //not BeforeClass
        public void setUp() throws Exception {
            AndroidLogMock.init();
        }

 *
 */
public class AndroidLogMock {

    private static final ConsoleLogger INFO_CONSOLE_LOGGER = new ConsoleLogger("I");
    private static final ConsoleLogger DEBUG_CONSOLE_LOGGER = new ConsoleLogger("D");
    private static final ConsoleLogger WARNING_CONSOLE_LOGGER = new ConsoleLogger("W");
    private static final ConsoleLogger ERROR_CONSOLE_LOGGER = new ConsoleLogger("E");

    public static void init(){
        PowerMockito.mockStatic(Log.class);


        when(Log.i(anyString(), anyString())).then(INFO_CONSOLE_LOGGER);
        when(Log.i(anyString(), anyString(), any(Throwable.class))).then(INFO_CONSOLE_LOGGER);


        when(Log.d(anyString(), anyString())).then(DEBUG_CONSOLE_LOGGER);
        when(Log.d(anyString(), anyString(), any(Throwable.class))).then(DEBUG_CONSOLE_LOGGER);

        when(Log.w(anyString(), anyString())).then(WARNING_CONSOLE_LOGGER);
        when(Log.w(anyString(), anyString(), any(Throwable.class))).then(WARNING_CONSOLE_LOGGER);
        when(Log.w(anyString(), any(Throwable.class))).then(WARNING_CONSOLE_LOGGER);

        when(Log.e(anyString(), anyString())).then(ERROR_CONSOLE_LOGGER);
        when(Log.e(anyString(), anyString(), any(Throwable.class))).then(ERROR_CONSOLE_LOGGER);

        System.out.println("--- new log ---");
    }

    private static class ConsoleLogger implements Answer<Integer> {

        private final String logLevel;

        public ConsoleLogger(String logLevel) {
            this.logLevel = logLevel;
        }

        @Override
        public Integer answer(InvocationOnMock invocation) throws Throwable {
            System.out.print(logLevel);
            System.out.print('/');
            Object[] args = invocation.getArguments();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if(!(arg instanceof Throwable)) {
                    System.out.print(arg);
                    if(i != args.length-1)
                        System.out.print(": ");

                } else {
                    ((Throwable) arg).printStackTrace();
                }

            }
            System.out.println();
            return -1;
        }
    }

}
