package com.ambrosus.ambviewer.utils;

import android.view.View;

public interface SelfRepresentingItem {

    int getLayoutResID();
    void updateView(View view);

}
