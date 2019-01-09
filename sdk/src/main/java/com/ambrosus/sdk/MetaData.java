package com.ambrosus.sdk;

import android.support.annotation.NonNull;

public class MetaData {

    private String bundleTransactionHash;
    private String bundleUploadTimestamp;
    private String bundleId;

    private MetaData(){}

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
