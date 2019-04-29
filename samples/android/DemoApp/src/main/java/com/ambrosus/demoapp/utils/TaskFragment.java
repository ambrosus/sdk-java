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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambrosus.demoapp.R;

public class TaskFragment extends Fragment implements FragmentSwitchHelper.BackListener {

    private final static BundleArgument<String> ROOT_FRAGMENT_CLASS_NAME = new BundleArgument<>("KEY_ROOT_FRAGMENT_CLASS_NAME", String.class);
    private final static BundleArgument<Bundle> ROOT_FRAGMENT_ARGS = new BundleArgument<>("KEY_ROOT_FRAGMENT_ARGS", Bundle.class);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FragmentSwitchHelper.addChild(
                this,
                Fragment.instantiate(getContext(), ROOT_FRAGMENT_CLASS_NAME.get(this), ROOT_FRAGMENT_ARGS.get(this, null)),
                R.id.task_fragment_container);
    }

    protected Fragment getCurrentPage(){
        return getChildFragmentManager().findFragmentById(R.id.task_fragment_container);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment, container, false);
    }

    @NonNull
    public static Fragment create(Class<? extends Fragment> rootClass){
        return create(rootClass, null);
    }

    @NonNull
    public static Fragment create(@NonNull Class<? extends Fragment> rootClass, @Nullable Bundle args){
        TaskFragment result = ROOT_FRAGMENT_CLASS_NAME.putTo(new TaskFragment(), rootClass.getName());
        ROOT_FRAGMENT_ARGS.putTo(result, args);
        return result;
    }


    @NonNull
    public static Bundle getArguments(@NonNull Class<? extends Fragment> rootClass){
        return ROOT_FRAGMENT_CLASS_NAME.getBundle(rootClass.getName());
    }

    @Override
    public boolean handleBackKey() {
        return FragmentSwitchHelper.goBack(this, R.id.task_fragment_container);
    }

    protected boolean isOnPage(Class<? extends Fragment> pageClass){
        return pageClass.isInstance(getCurrentPage());
    }

    public <T extends Fragment> T immediateGoBackTo(Class<T> pageClass){
        if(!isOnPage(pageClass)) {
            FragmentSwitchHelper.throwOut(getCurrentPage(), pageClass);
            getChildFragmentManager().executePendingTransactions();
            if(!isOnPage(pageClass))
                throw new IllegalStateException("Wasn't able to go back to page: " + pageClass.getName());
        }
        return (T) getCurrentPage();
    }
}
