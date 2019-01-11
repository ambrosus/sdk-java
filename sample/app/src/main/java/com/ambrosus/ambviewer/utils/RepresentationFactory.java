package com.ambrosus.ambviewer.utils;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class RepresentationFactory<T> {

    protected abstract Representation<T> createRepresentation(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

}
