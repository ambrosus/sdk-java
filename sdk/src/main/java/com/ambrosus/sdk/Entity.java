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

import java.io.Serializable;
import java.util.Date;

/**
 * Super class for data models supported by a {@link Network} implementation.
 */
public abstract class Entity implements Serializable {

    /**
     * @return Content-addressable identifier of the Entity.
     */
    @NonNull
    public abstract String getSystemId();

    /**
     * @return timestamp which was set by the user when creating this Entity. This timestamp is accurate to seconds because Ambrosus Network uses UnixTime format to keep this value
     */
    @NonNull
    public abstract Date getTimestamp();


    /**
     * @return address of the account which was used to create this Entity
     */
    @NonNull
    public abstract String getAccountAddress();

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Entity)) return false;
        return getSystemId().equals(((Entity)obj).getSystemId());
    }
}
