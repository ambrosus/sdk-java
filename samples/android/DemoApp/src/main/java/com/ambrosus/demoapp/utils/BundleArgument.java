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

package com.ambrosus.demoapp.utils;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    public @NonNull T get(Bundle args){
        return convertStoredValue(BundleUtils.extractBundleValue(args, key, getClassForStoredValue()));
    }

    public @NonNull T get(Fragment fragment){
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

    @NonNull
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
