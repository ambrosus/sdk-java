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

package com.ambrosus.ambviewer.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ParcelableAdapter<T> implements Parcelable {

    private static final Gson gson = new Gson();

    private final T data;

    public ParcelableAdapter(T data) {
        this.data = data;
    }

    private ParcelableAdapter(Parcel in){
        this(deserialize((Class<T>) in.readSerializable(), in.readString()));
    }

    public T getData() {
        return data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(data.getClass());
        dest.writeString(serialize(data));
    }

    private static String serialize(Object data){
        return gson.toJson(data);
    }

    private static <T> T deserialize(Class<T> clazz, String jsonString){
        return gson.fromJson(jsonString, clazz);
    }

    public static final Parcelable.Creator<ParcelableAdapter> CREATOR
            = new Parcelable.Creator<ParcelableAdapter>() {
        public ParcelableAdapter createFromParcel(Parcel in) {
            return new ParcelableAdapter(in);
        }

        public ParcelableAdapter[] newArray(int size) {
            return new ParcelableAdapter[size];
        }
    };
}
