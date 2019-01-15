package com.ambrosus.ambviewer.utils;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.ambrosus.sdk.utils.Assert;

import java.io.Serializable;

public class BundleArgument<T> {

    private final String key;
    private final Class<T> valueClass;

    public BundleArgument(String bundleKey, Class<T> valueClass) {
        Assert.assertTrue(
                Serializable.class.isAssignableFrom(valueClass) || Parcelable.class.isAssignableFrom(valueClass),
                IllegalArgumentException.class,
                "you can use only serializable or parcelable type for bundle arguments"
        );
        this.key = bundleKey;
        this.valueClass = valueClass;
    }

    public T get(Bundle args, T defaultValue) {
        return BundleUtils.extractBundleValueIfExists(args, key, valueClass, defaultValue);
    }

    public T get(Bundle args){
        return BundleUtils.extractBundleValue(args, key, valueClass);
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

    public void put(Bundle args, T value){
        if(value != null) {
            //TODO-2x It should be better try Parcelable in the first order, but don't have time to check it
            if(value instanceof Serializable) {
                args.putSerializable(key, (Serializable) value);
            } else {
                args.putParcelable(key, (Parcelable) value);
            }
        }
    }

}
