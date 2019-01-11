package com.ambrosus.ambviewer.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Representation<T> extends RecyclerView.ViewHolder {

    public Representation(int layoutResID, @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        super(inflateLayout(layoutResID, inflater, parent));
    }

    protected abstract void display(T data);

    static View inflateLayout(int layoutResID, LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(layoutResID, parent, false);
    }
}
