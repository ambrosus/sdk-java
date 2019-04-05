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

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.ambrosus.ambviewer.BuildConfig;

public class FragmentSwitchHelper {

    private static final String TAG = FragmentSwitchHelper.class.getName();

    public static String getFragmentTag(Fragment fragment) {
        return getFragmentTag(fragment.getClass());
    }

    public static String getFragmentTag(Class<? extends Fragment> clazz) {
        return clazz.getName();
    }

    public static void replaceFragment(Fragment current, Fragment next, boolean allowStateLoss){
        FragmentTransaction ft = current.getFragmentManager().beginTransaction();
        ft.replace(current.getId(), next, getFragmentTag(next));
        if(allowStateLoss)
            ft.commitAllowingStateLoss();
        else
            ft.commit();
    }

    /**
     *
     * Deprecated - use addChild instead when possible
     *
     * @param fragmentManager
     * @param fragment
     * @param containerID
     *
     * @throws NullPointerException if fragment is null
     */
    @Deprecated
    public static void showFragment(FragmentManager fragmentManager, Fragment fragment, int containerID){
        addChild(fragmentManager, fragment, containerID, true);
    }

    public static boolean addChild(Fragment root, Fragment child, int containerID){
        return addChild(root.getChildFragmentManager(), child, containerID, true);
    }

    public static void addChild(FragmentActivity root, Fragment child, int containerID){
        addChild(root.getSupportFragmentManager(), child, containerID, true);
    }

    public static boolean addChild(Fragment root, Fragment fragment, int containerID, boolean exclusive, boolean addToBackStack) {
        return addChild(root.getChildFragmentManager(), fragment, containerID, exclusive, addToBackStack);
    }

    private static boolean addChild(FragmentManager fragmentManager, Fragment fragment, int containerID, boolean exclusive) {
        return addChild(fragmentManager, fragment, containerID, exclusive, false);
    }

    private static boolean addChild(FragmentManager fragmentManager, Fragment fragment, int containerID, boolean exclusive, boolean addToBackStack){
        if(exclusive && fragmentManager.findFragmentByTag(getFragmentTag(fragment)) != null)
            return false;

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(containerID, fragment, getFragmentTag(fragment));
        if(addToBackStack)
            ft.addToBackStack(getFragmentTag(fragment));
        ft.commit();
        return true;
    }

    public static void replaceChild(Fragment root, int childContainerID, Fragment withFragment) {
        FragmentTransaction ft = root.getChildFragmentManager().beginTransaction();
        ft.replace(childContainerID, withFragment, getFragmentTag(withFragment));
        ft.commit();
    }

    public static void removeChild(Fragment root, Fragment child, boolean allowStateLoss){
        removeFragment(root.getChildFragmentManager(), child, allowStateLoss);
    }

    public static void removeChild(Fragment root, Class<? extends Fragment> childClass, boolean allowStateLoss){
        Fragment child = getChild(root, childClass);
        removeFragment(root.getChildFragmentManager(), child, allowStateLoss);
    }

    //TODO (instance of)  check may be better for this case
    public static <T extends Fragment> T getChild(Fragment root, Class<T> childClass){
        return (T) root.getChildFragmentManager().findFragmentByTag(getFragmentTag(childClass));
    }

    @Deprecated
    /* TODO-2x

     This method should look like:
     public static void showAsDialog(FragmentManager hostFragmentManager, DialogFragment fragment) {
        fragment.show(hostFragmentManager, getFragmentTag(fragment));
     }

     */
    public static void showDialogFragment(Fragment hostFragment, Fragment fragment){
        if(hostFragment.isDetached())
            throw new IllegalStateException("host fragment is de-attached, it might be a mistake in arguments order.");

        FragmentTransaction fragmentTransaction = hostFragment.getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void removeFragment(FragmentManager fragmentManager, Fragment fragment, boolean allowStateLoss) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(fragment);

        if(!allowStateLoss)
            ft.commit();
        else {
            if(fragment.getFragmentManager().isStateSaved()) {
                FragmentSwitchHelperException exception = new FragmentSwitchHelperException("removing fragment and loosing state!");
                if(BuildConfig.DEBUG) {
                    throw exception;
                } else
                    Log.d(TAG, "Commiting allowing state loss", exception);
            }
            ft.commitAllowingStateLoss();
        }
    }

    public static void showNextFragment(Fragment fromFragment, Fragment fragmentToShow){
        FragmentManager fragmentManager = fromFragment.getFragmentManager();
        int containerID = fromFragment.getId();

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(containerID, fragmentToShow, getFragmentTag(fragmentToShow));
        ft.addToBackStack(getFragmentTag(fromFragment));

        ft.commit();
    }

    public static void throwOut(Fragment startFrom, Class<? extends Fragment> upTo) {
        startFrom.getFragmentManager().popBackStack(getFragmentTag(upTo), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private static final class FragmentSwitchHelperException extends RuntimeException {
        private FragmentSwitchHelperException(String message) {
            super(message);
        }
    }


    public interface BackListener {

        /**
         * @return true if back was handled false otherwise
         */
        boolean handleBackKey();

    }

    public static boolean goBack(FragmentManager fragmentManager, int containerID ){
        return handleBack(fragmentManager.findFragmentById(containerID))
                || FragmentSwitchHelper.popBackStack(fragmentManager);
    }


    public static boolean goBack(Fragment root, int containerID ){
        return goBack(root.getChildFragmentManager(), containerID);
    }

    /**
     *
     * @param fragmentManager
     * @return true if was able to pop back stack for specified fragmentManager
     */
    private static boolean popBackStack(FragmentManager fragmentManager){
        if(fragmentManager.getBackStackEntryCount() != 0) {
            final boolean isStateSaved = fragmentManager.isStateSaved();
            if (!isStateSaved) {
                fragmentManager.popBackStack();
            }
            return true;
        }
        return false;
    }


    public static boolean handleBack(Fragment fragment) {
        return fragment instanceof BackListener && ((BackListener) fragment).handleBackKey();
    }

    public static void forceBackStep(Fragment from){
        from.getFragmentManager().popBackStack();
    }

    public static void goBackFromLoaderCallBack(View someView, final Activity activity){
        someView.post(new Runnable() {
            @Override
            public void run() {
                activity.onBackPressed();
            }
        });
    }

}
