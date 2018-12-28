package com.ambrosus.sdk.models;

import java.io.Serializable;

public class Asset implements Serializable {

    private String assetId;
    private String name;

    public String getSystemId() {
        return assetId;
    }
}
