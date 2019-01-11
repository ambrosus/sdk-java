package com.ambrosus.ambviewer.utils;

public class RepresentationItem<T> {

    final T data;
    final RepresentationFactory<T> representationFactory;

    public RepresentationItem(T data, RepresentationFactory<T> representationFactory) {
        this.data = data;
        this.representationFactory = representationFactory;
    }
}
