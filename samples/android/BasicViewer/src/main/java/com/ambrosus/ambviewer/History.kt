package com.ambrosus.ambviewer

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.ambrosus.ambviewer.utils.deserializeFromString
import com.ambrosus.ambviewer.utils.serializeToString
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

import java.util.LinkedList

private const val MAX_ITEMS = 20;

class History(context: Context) {

    private val storage = context.getSharedPreferences("HistoryStorage", Context.MODE_PRIVATE)

    fun addAsset(asset: Asset, identifiers: List<Identifier>) {
        add(asset, identifiers)
    }

    fun addAssetInfo(asset: AMBAssetInfo, identifiers: List<Identifier>) {
        add(asset, identifiers)
    }

    private fun add(asset: Serializable, identifiers: List<Identifier>) {
        val editor = storage.edit()
        removeExtraItems(editor)
        editor.putString(
                getItemKey(),
                serializeToString(
                        HistoryItem(
                                identifiers.toTypedArray(),
                                System.currentTimeMillis(),
                                asset
                        )
                )
        )
        editor.apply()
    }

    private fun removeExtraItems(editor: SharedPreferences.Editor) {
        val keys = storage.all.keys
        if(keys.size >= MAX_ITEMS) {
            val sortedItems = ArrayList(keys)
            sortedItems.sort()
            do {
                editor.remove(sortedItems.removeAt(0))
            } while (sortedItems.size >= MAX_ITEMS)
        }
    }

    fun getItems() : List<HistoryItem> {
        val result = ArrayList<HistoryItem>()
        try {
            val keys = storage.all.keys
            for (key in keys) {
                val model = deserializeFromString(storage.getString(key, null))
                result.add(model as HistoryItem)
            }
        } catch (e: Exception) {
            Log.e("History", "Can't restore history, cleaning up storage", e);
            storage.edit().clear().apply()
        }
        result.sortBy { it.scanTimeStamp }
        result.reverse()
        return result
    }


    private fun getItemKey(): String {
        return System.currentTimeMillis().toString()
    }

    class HistoryItem (
            val identifiers: Array<Identifier>,
            val scanTimeStamp: Long,
            val asset: Serializable
    ) : Serializable
}

