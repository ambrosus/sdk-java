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

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Asset {

    private String assetId;

    private Content content;
    private MetaData metadata;

    //no-args constructor for Gson
    Asset(){}

    Asset(Content content) {
        this.assetId = Network.getObjectHash(content);
        this.content = content;
    }

    @NonNull
    public String getSystemId() {
        return assetId;
    }

    @NonNull
    public String getAccount() {
        return content.idData.getCreatedBy();
    }

    public long getTimestamp() {
        return content.idData.getTimestamp();
    }

    public long getSequenceNumber() {
        return content.idData.sequenceNumber;
    }

    @NonNull
    public MetaData getMetaData() {
        return metadata;
    }

    static class AssetIdData extends IdData {

        private long sequenceNumber;

        //no-argument contructor for GSON
        private AssetIdData(){
            super();
        }

        AssetIdData(String createdBy, long timeStamp, long sequenceNumber) {
            super(createdBy, timeStamp);
            this.sequenceNumber = sequenceNumber;
        }
    }

    static class Content {

        private AssetIdData idData;
        private String signature;

        //no-args constructor for Gson
        private Content() {}

        private Content(AssetIdData idData, String signature) {
            this.idData = idData;
            this.signature = signature;
        }

        public AssetIdData getIdData() {
            return idData;
        }

        static Content create(AssetIdData idData, String privateKey) {
            return new Content(idData, Network.getObjectSignature(idData, privateKey));
        }
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


    public static class Builder {

        private long timeStamp;
        private long sequenceNumber;

        public Builder() {
            setTimeStamp(System.currentTimeMillis());
        }

        public void setUnixTimeStamp(long timeStamp, long sequenceNumber) {
            this.timeStamp = timeStamp;
            this.sequenceNumber = sequenceNumber;
        }

        public void setTimeStamp(long timeMillis) {
            setUnixTimeStamp(TimeUnit.MILLISECONDS.toSeconds(timeMillis), System.nanoTime());
        }

        public Asset createAsset(String privateKey) {
            String address = Ethereum.getPublicKey(privateKey);
            AssetIdData assetIdData = new AssetIdData(address, timeStamp, sequenceNumber);
            Content content = Content.create(assetIdData, privateKey);
            return new Asset(content);
        }
    }
}
