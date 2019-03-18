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

import com.ambrosus.sdk.utils.UnixTime;

import java.util.Date;

public final class Asset extends Entity {

    private String assetId;

    private SignedContent<AssetIdData> content;
    private MetaData metadata;

    //no-args constructor for Gson
    private Asset(){}

    private Asset(SignedContent<AssetIdData> content) {
        this.assetId = Network.getObjectHash(content);
        this.content = content;
    }

    @NonNull
    @Override
    final public String getSystemId() {
        return assetId;
    }

    @NonNull
    @Override
    public String getAccountAddress() {
        return content.idData.getAccountAddress();
    }

    @NonNull
    @Override
    final public Date getTimestamp() {
        return content.idData.getTimestamp();
    }

    public double getSequenceNumber() {
        return content.idData.sequenceNumber;
    }

    @NonNull
    public MetaData getMetaData() {
        return metadata;
    }

    private static class AssetIdData extends CreationData {

        private double sequenceNumber;

        //no-args constructor for GSON
        private AssetIdData(){
            super();
        }

        private AssetIdData(String createdBy, long timeStamp, long sequenceNumber) {
            super(createdBy, timeStamp);
            this.sequenceNumber = sequenceNumber;
        }
    }

    public static class Builder {

        private long timeStamp;
        private long sequenceNumber;

        public Builder() {
            setTimeStamp(new Date());
        }

        /**
         *
         * @param timeStamp - the number of seconds that have elapsed since 00:00:00 Thursday, 1 January 1970
         * @param sequenceNumber - any value, it's used to make possible to create different assets with the same timeStamp
         */
        public void setUnixTimeStamp(long timeStamp, long sequenceNumber) {
            this.timeStamp = timeStamp;
            this.sequenceNumber = sequenceNumber;
        }

        /**
         * TimeStamp precision is limited to seconds, milliseconds value will be truncated. {@link Asset#getTimestamp()}
         * will return just a date value with the same amount of seconds as original {@code date} plus 0 milliseconds
         *
         * @param date
         */
        public void setTimeStamp(Date date) {
            setUnixTimeStamp(UnixTime.get(date), System.nanoTime());
        }

        public Asset createAsset(String privateKey) {
            AssetIdData assetIdData = new AssetIdData(
                    Ethereum.getAddress(privateKey),
                    timeStamp,
                    sequenceNumber
            );
            return new Asset(new SignedContent<>(assetIdData, privateKey));
        }
    }
}
