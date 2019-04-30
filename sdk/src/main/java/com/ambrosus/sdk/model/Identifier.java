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

package com.ambrosus.sdk.model;

import java.io.Serializable;
import java.util.Objects;

public class Identifier implements Serializable {

    static final String DATA_OBJECT_TYPE_ASSET_IDENTIFIERS = "ambrosus.asset.identifiers";
    //static final String TYPE_EVENTS = "ambrosus.event.identifiers";

    public static final String GTIN = "gtin";
    public static final String EAN13 = "ean13";
    public static final String EAN8 = "ean8";
    public static final String LOT = "lot";
    public static final String SERIAL = "ser";
    public static final String BATCH_ID = "batchId";

    public final String type;
    public final String value;

    public Identifier(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return type + ": " + value;
    }
}
