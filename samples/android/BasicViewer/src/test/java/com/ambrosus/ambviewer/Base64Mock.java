package com.ambrosus.ambviewer;

import android.util.Base64;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

public class Base64Mock {

    public static void mockAndroidBase64Encoding(){
        PowerMockito.mockStatic(Base64.class);
        when(Base64.encodeToString(any(byte[].class), anyInt())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return java.util.Base64.getEncoder().encodeToString((byte[]) invocation.getArguments()[0]);
                    }
                }
        );
        when(Base64.decode(anyString(), anyInt())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return java.util.Base64.getDecoder().decode((String) invocation.getArguments()[0]);
                    }
                }
        );
    }

}
