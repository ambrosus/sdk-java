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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class Asset {

    private String assetId;

    private Content content;
    private MetaData metadata;

    @NonNull
    public String getSystemId() {
        return assetId;
    }

    @NonNull
    public String getAccount() {
        return content.idData.createdBy;
    }

    @NonNull
    public long getSequenceNumber() {
        return content.idData.sequenceNumber;
    }

    @NonNull
    public long getTimestamp() {
        return content.idData.timestamp;
    }

    @NonNull
    public MetaData getMetaData() {
        return metadata;
    }


    private static class Content implements Serializable {
        private IDData idData;
    }

    private static class IDData implements Serializable {

        private String createdBy;
        private long timestamp;
        private long sequenceNumber;

    }

    public static class MetaData implements Serializable {
        private String bundleTransactionHash;
        private String bundleUploadTimestamp;
        private String bundleId;

        @NonNull
        public String getBundleTransactionHash() {
            return bundleTransactionHash;
        }

        @NonNull
        public String getBundleUploadTimestamp() {
            return bundleUploadTimestamp;
        }

        @NonNull
        public String getBundleId() {
            return bundleId;
        }
    }
}
