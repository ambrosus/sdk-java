package com.ambrosus.ambviewer.utils;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;

public class BundleArgument<T> {

    private final String key;
    private final Class valueClass;

    public BundleArgument(String bundleKey, Class<T> valueClass) {
        this.key = bundleKey;
        this.valueClass = valueClass;
    }

    private boolean needToUseWrapper(){
        return !(Serializable.class.isAssignableFrom(valueClass) || Parcelable.class.isAssignableFrom(valueClass));
    }

    private Class<?> getClassForStoredValue(){
        return needToUseWrapper() ? ParcelableAdapter.class : valueClass;
    }

    private T convertStoredValue(Object storedValue){
        return storedValue instanceof ParcelableAdapter ? ((ParcelableAdapter<T>) storedValue).getData() : (T) storedValue;
    }

    public T get(Bundle args, T defaultValue) {
        return convertStoredValue(BundleUtils.extractBundleValueIfExists(args, key, Object.class, defaultValue));
    }

    public T get(Bundle args){
        return convertStoredValue(BundleUtils.extractBundleValue(args, key, getClassForStoredValue()));
    }

    public T get(Fragment fragment){
        return get(fragment.getArguments());
    }

    public T get(Fragment fragment, T defaultValue){
        return get(fragment.getArguments(), defaultValue);
    }

    public <Type> Type getTyped(Fragment fragment){
        return (Type) get(fragment.getArguments());
    }

    public <Type extends T> Type getTyped(Fragment fragment, Type defaultValue){
        return (Type) get(fragment.getArguments(), defaultValue);
    }

    public Bundle getBundle(T value){
        Bundle args = new Bundle();
        put(args, value);
        return args;
    }

    public <FragmentType extends Fragment> FragmentType putTo(FragmentType fragment, T value){
        Bundle args = fragment.getArguments();

        if(args == null) fragment.setArguments(getBundle(value));
        else put(args, value);

        return fragment;
    }

    public Bundle put(Bundle args, T value){
        if(value != null) {
            if(value instanceof Parcelable) {
                args.putParcelable(key, (Parcelable) value);
            } else if(value instanceof Serializable){
                args.putSerializable(key, (Serializable) value);
            } else {
                args.putParcelable(key, new ParcelableAdapter<T>(value));
            }
        }
        return args;
    }

}
