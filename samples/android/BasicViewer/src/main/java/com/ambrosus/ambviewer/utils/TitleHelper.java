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

package com.ambrosus.ambviewer.utils;

import androidx.fragment.app.Fragment;

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
