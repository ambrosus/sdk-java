package com.ambrosus.ambviewer.utils;

import android.support.v4.app.Fragment;

public class TitleHelper {

    private final int dynamicTitleFallBackResID;
    private final Fragment fragment;
    private CharSequence title;


    public TitleHelper(Fragment fragment, int dynamicTitleFallBackResID) {
        this.dynamicTitleFallBackResID = dynamicTitleFallBackResID;
        this.fragment = fragment;
    }

    public void setTitle() {
        ensureTitle(fragment, title, dynamicTitleFallBackResID);
    }

    public void setTitle(CharSequence title){
        this.title = title;
        setTitle();
    }

    public static void ensureTitle(Fragment fragment, int titleResID) {
        if(titleResID != 0)
            fragment.getActivity().setTitle(titleResID);
        else
            ensureTitle(fragment, null);
    }

    public static void ensureTitle(Fragment fragment, CharSequence title) {
        fragment.getActivity().setTitle(title);
    }

    public static void ensureTitle(Fragment fragment, String title) {
        fragment.getActivity().setTitle(title);
    }

    public static void ensureTitle(Fragment fragment, CharSequence dynamicTitle, int dynamicTitleFallBackResID) {
        if(dynamicTitle == null)
            dynamicTitle = fragment.getResources().getText(dynamicTitleFallBackResID);
        ensureTitle(fragment, dynamicTitle);
    }

}
