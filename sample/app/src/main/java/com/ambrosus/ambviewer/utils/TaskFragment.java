package com.ambrosus.ambviewer.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambrosus.ambviewer.R;

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
