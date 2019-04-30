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

package com.ambrosus.apps

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ambrosus.ambviewer.R
import com.ambrosus.ambviewer.utils.Representation
import com.ambrosus.ambviewer.utils.RepresentationFactory
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.SearchResult
import java.lang.IllegalArgumentException
import java.util.Objects


class SearchResultsListAdapter(
        context: Context,
        private var pages: List<SearchResult<out Entity>>,
        private val representationFactory: RepresentationFactory<Entity>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return when(viewType) {
            0 -> representationFactory.createRepresentation(inflater, parent)
            1 -> object: androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.item_loading_indicator, parent, false)){}
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val objectIndex = getObjectIndex(position)
        return if(objectIndex != -1) 0 else 1
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val objectIndex = getObjectIndex(position)
        if(objectIndex != -1)
            (holder as Representation<Entity>).display(getObject(objectIndex))
    }

    override fun getItemCount(): Int {
        var itemsCount: Int = 0;

        if(!hasFirstPage())
            itemsCount++


        if(!hasLastPage())
            itemsCount++

        itemsCount += getObjectsCount()

        return itemsCount
    }

    private fun hasLastPage() = pages.last().pageIndex == pages.last().totalPages - 1

    private fun hasFirstPage() = pages.first().pageIndex == 0

    private fun getObjectsCount(): Int {
        var itemsCount = 0
        for (page in pages) {
            itemsCount += page.items.size
        }
        return itemsCount
    }

    private fun getObjectIndex(position: Int): Int {
        if(position == 0 && !hasFirstPage())
            return -1

        if(position == itemCount - 1 && !hasLastPage())
            return -1;

        return if(hasFirstPage()) position else position-1
    }

    private fun getObject(index: Int): Entity {
        var inPageIndex = index

        for (page in pages) {
            if(inPageIndex < page.items.size)
                return page.items[inPageIndex]
            else inPageIndex -= page.items.size
        }
        throw IllegalArgumentException("we don't have enough items for $index")
    }

    fun update(pages: List<SearchResult<out Entity>>) {
        val objects = getObjects()
        this.pages = pages
        val updatedObjects = getObjects()
        DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return Objects.equals(objects[oldItemPosition], updatedObjects[newItemPosition])
            }

            override fun getOldListSize(): Int {
                return objects.size
            }

            override fun getNewListSize(): Int {
                return updatedObjects.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return Objects.equals(objects[oldItemPosition], updatedObjects[newItemPosition])
            }
        }).dispatchUpdatesTo(this)
    }

    private fun getObjects(): ArrayList<Entity?> {
        val objects = ArrayList<Entity?>()

        if(!hasFirstPage()) objects.add(null)

        for (page in pages) {
            objects.addAll(page.items)
        }

        if(!hasLastPage()) objects.add(null)
        return objects
    }
}