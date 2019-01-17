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
