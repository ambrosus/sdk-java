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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.FontRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ViewUtils {

    public static <T extends View> T getView(Fragment fragment, int id){
        return fragment.getView().findViewById(id);
    }

    public static <T extends View> T getView(Activity activity, int id){
        return activity.findViewById(id);
    }

    public static void setImageResource(View holder, int imageViewID, int drawableID){
        ((ImageView) holder.findViewById(imageViewID)).setImageResource(drawableID);
    }
    public static void setImageDrawable(View holder, int imageViewID, Drawable drawable){
        ((ImageView) holder.findViewById(imageViewID)).setImageDrawable(drawable);
    }

    public static void setText(View view, int textViewID, CharSequence text){
        View target = view.findViewById(textViewID);
        if(target instanceof TextView)
            ((TextView) target).setText(text);
        else
            ((TextField) target).setText(text);
    }

    public static void setText(View view, int textViewID, int textResID){
        //there is a difference in implementation between TextView.setText(int) and TextView.setText(String)
        //so it's better not to use ViewUtils.setText(View view, int textViewID, String text) method there
        View target = view.findViewById(textViewID);
        if(target instanceof TextView)
            ((TextView) target).setText(textResID);
        else
            ((TextField) target).setText(textResID);
    }


    public static void setText(View view, int textViewID, int textResID, Object ...args){
        Context context = view.getContext();
        String text = context.getString(textResID, args);
        setText(view, textViewID, text);
    }


    public static CharSequence getText(View view, int textViewID) {
        View source = view.findViewById(textViewID);
        if(source instanceof TextView)
            return ((TextView) source).getText();
        else
            return ((TextField) source).getText();
    }

    public static void changeVisibilityInvisible(View view, int viewId, boolean isVisible){

        View source = view.findViewById(viewId);

        if(source != null)
            source.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public static void changeVisibilityVisible(View view, int viewId, boolean isVisible){
        view = getTargetView(view, viewId);
        view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public static void changeVisibility(View view, int viewId, boolean isVisible) {
        view = getTargetView(view, viewId);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private static View getTargetView(View root, int viewId){
        if(root.getId() != viewId)
            root = root.findViewById(viewId);
        return root;
    }
    public static void switchVisibilityToGone(View view, int viewId, boolean isVisible){
        if(view.getId() != viewId) //TODO-2x looks like this check is extra, view.findViewById returns this if id matches the view id, at least for API 27 (need to check for API 18)
            view = view.findViewById(viewId);

        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public static void remove(View view, int viewId){
        view.findViewById(viewId).setVisibility(View.GONE);
    }

    public static void display(View view, int viewId){
        view.findViewById(viewId).setVisibility(View.VISIBLE);
    }

    public static void setBitmapResource(View view, int imageViewID, Bitmap bitmap){
        ((ImageView) view.findViewById(imageViewID)).setImageBitmap(bitmap);
    }

    public static String getString(View view, int textViewID) {
        return getText(view, textViewID).toString().trim();
    }

    public static void setDate(View parent, int textViewID, Date date){
        setText(parent, textViewID, DateAdapter.dateToText(date));
    }

    public static Date getDate(View parent, int textViewID){
        return DateAdapter.dateFromText(getString(parent, textViewID));
    }

    public static int getPixels(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static void setFont(View view, int viewId, @FontRes int fontId) {
        TextView target = (TextView) getTargetView(view, viewId);
        target.setTypeface(ResourcesCompat.getFont(view.getContext(), fontId));
    }

    /**
     No sure if it works correctly. Doesn't have enough time for tests.
     **/
    public static View findViewByID(Fragment fragment, @IdRes int id){
        List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();

        View fragmentRoot = fragment.getView();

        if(childFragments.size() > 0) {
            List<Integer> childFragmentContainerIDs = new ArrayList<>();
            for (Fragment childFragment : childFragments) {
                int childContainerID = childFragment.getId();
                if(childContainerID != 0) {
                    childFragmentContainerIDs.add(childContainerID);
                }
            }

            if(childFragmentContainerIDs.size() > 0 ) {
                int[] excludeGroupsWithIDs = new int[childFragmentContainerIDs.size()];
                int index = 0;
                for (Integer idToExclude : childFragmentContainerIDs) {
                    excludeGroupsWithIDs[index++] = idToExclude;
                }
                Arrays.sort(excludeGroupsWithIDs);
                return findViewTraversal(fragmentRoot, id, excludeGroupsWithIDs);
            }
        }
        return fragmentRoot.findViewById(id);
    }

    private static View findViewTraversal(View view, @IdRes int id, int[] excludeGroups) {
        if (id == view.getId()) {
            return view;
        }

        if(view instanceof ViewGroup) {

            ViewGroup group = (ViewGroup) view;

            for (int childIndex = 0; childIndex < group.getChildCount(); childIndex++) {
                View child = group.getChildAt(childIndex);

                if(Arrays.binarySearch(excludeGroups, child.getId()) < 0) { // we don't have to exclude this viewGroup
                    View result = findViewTraversal(child, id, excludeGroups);
                    if(result != null )
                        return result;
                }
            }
        }

        return null;
    }



    public interface TextField {

        void setText(CharSequence text);
        void setText(int textResId);
        CharSequence getText();

    }

}
