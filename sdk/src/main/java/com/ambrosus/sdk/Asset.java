package com.ambrosus.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class Asset implements Serializable {

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
