package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult<T> {

    @SerializedName("results")
    private List<T> values;

    @SerializedName("resultCount")
    private int totalCount;

    @NonNull
    public List<T> getValues() {
        return values;
    }

    @NonNull
    public int getTotalCount() {
        return totalCount;
    }
}
