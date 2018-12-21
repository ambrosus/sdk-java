package com.ambrosus.sdk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult<T> {

    @SerializedName("results")
    public List<T> values;

    @SerializedName("resultCount")
    public int totalCount;
}
