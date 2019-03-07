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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepresentationAdapter extends RecyclerView.Adapter {

    private final ArrayList<RepresentationItem> items = new ArrayList<>();
    private final ArrayList<RepresentationFactory> representationFactories = new ArrayList<>();

    private final LayoutInflater inflater;

    public RepresentationAdapter(Context context, List<RepresentationItem> items) {
        inflater = LayoutInflater.from(context);

        this.items.addAll(items);
        for (RepresentationItem obj : items) {
            if (!representationFactories.contains(obj.representationFactory)) {
                representationFactories.add(obj.representationFactory);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return representationFactories.get(viewType).createRepresentation(inflater, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RepresentationItem item = getItem(position);
        ((Representation) holder).display(item.data);
    }

    private RepresentationItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return representationFactories.indexOf(items.get(position).representationFactory);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static <T> RepresentationAdapter create(Context context, List<T> items, RepresentationFactory<T> representationFactory) {
        RepresentationAdapter.DataSetBuilder builder = new RepresentationAdapter.DataSetBuilder();
        builder.addAll(items, representationFactory);
        return builder.createAdapter(context);
    }

    public static RepresentationAdapter create(Context context, List<SelfRepresentingItem> items){
        return new RepresentationAdapter(context, DataSetBuilder.getItems(items));
    }


    public static class DataSetBuilder {

        private final List<RepresentationItem> items = new ArrayList<>();

        public RepresentationAdapter createAdapter(Context context){
            return new RepresentationAdapter(context, getItems());
        }

        public <T> DataSetBuilder addAll(List<T> dataItems, RepresentationFactory<T> representationFactory){
            for (T dataItem : dataItems) {
                add(dataItem, representationFactory);
            }
            return this;
        }

        public <T> DataSetBuilder add(T dataItem, RepresentationFactory<T> representationFactory) {
            items.add(new RepresentationItem<T>(dataItem, representationFactory));
            return this;
        }

        public DataSetBuilder add(SelfRepresentingItem item){
           items.addAll(getItems(item));
           return this;
        }

        public List<RepresentationItem> getItems() {
            return items;
        }

        public static List<RepresentationItem> getItems(List<SelfRepresentingItem> items){
            List<RepresentationItem> result = new ArrayList<>();
            Map<Integer, DelegatedRepresentationFactory> representations = new HashMap<>();
            for (SelfRepresentingItem item : items) {
                int currentItemLayout = item.getLayoutResID();
                RepresentationAdapter.DelegatedRepresentationFactory representation = representations.get(currentItemLayout);
                if(representation == null) {
                    representation = new RepresentationAdapter.DelegatedRepresentationFactory(currentItemLayout);
                    representations.put(currentItemLayout, representation);
                }
                result.add(new RepresentationItem<>(item, representation));
            }
            return result;
        }

        public static List<RepresentationItem> getItems(SelfRepresentingItem... items){
            return getItems(Arrays.asList(items));
        }
    }

    private static class DelegatedRepresentationFactory extends RepresentationFactory<SelfRepresentingItem> {

        private final int layoutResID;

        private DelegatedRepresentationFactory(int layoutResID) {
            this.layoutResID = layoutResID;
        }

        @Override
        public Representation<SelfRepresentingItem> createRepresentation(LayoutInflater inflater, ViewGroup parent) {
            return new DelegatedRepresentation(layoutResID, inflater, parent);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof RepresentationAdapter.DelegatedRepresentationFactory && ((RepresentationAdapter.DelegatedRepresentationFactory) obj).layoutResID == layoutResID;
        }

        private static class DelegatedRepresentation extends Representation<SelfRepresentingItem> {

            public DelegatedRepresentation(int layoutResID, LayoutInflater inflater, ViewGroup parent) {
                super(layoutResID, inflater, parent);
            }

            @Override
            public void display(SelfRepresentingItem data) {
                data.updateView(itemView);
            }
        }

    }
}
