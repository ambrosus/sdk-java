package com.ambrosus.ambrosussdk.model

import android.media.Image

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

class AMBDataStore {

    companion object {
        lateinit var sharedInstance: AMBDataStore
    }

    init {
        sharedInstance = this
    }

    /// A singleton of AMBDataSource, use to store all Assets and Events
//    public val sharedInstance = AMBDataStore()

    /// A data store for Assets, allows for inserting, fetch by id, and returning all assets
    val assetStore = AssetStore()

    /// A data store for Events, all events are associated with an Asset, and one asset maps to many Events
    val eventStore = EventStore()

    val imageCache = HashMap<String, Image>()
}

class AssetStore {

    /// A mapping between an Asset ID (String) and an Asset
    private var assets: Map<String, AMBAsset> = HashMap()

    /// Insert an asset into the store
    ///
    /// - Parameter asset: The asset to insert
    fun insert(asset: AMBAsset) {
        if (assets[asset.id] != null) {
            return
        }

        assets.plus(Pair(asset.id, asset))
    }

    /// Fetches a specific asset based on the assetId from the data store
    ///
    /// - Parameter assetId: The id for the asset to return
    /// - Returns: The asset if available in the store, nil otherwise
    fun fetch(withAssetId: String): AMBAsset? {
        return assets[withAssetId]
    }

    /// Get all assets saved in the asset store
    ///
    /// - Returns: The array of assets, can be empty if no assets are stored
    fun allAssets(): List<AMBAsset> {
        val sortedAssets = assets.values.sortedWith(Comparator { asset1,
                                                                 asset2 ->
            asset1.timestamp.compareTo(asset2.timestamp)
        })
        return sortedAssets
    }

}

class EventStore {

    /// A mapping between an Asset ID (String) and all of its events
    private var eventsForAssetId: Map<String, List<AMBEvent>> = HashMap()

    /// Insert an array of events into the store
    ///
    /// - Parameter events: The events to insert
    fun insert(events: List<AMBEvent>) {
        if (events.size > 0) {
            val first = events[0]

            val sortedEvents = events.sortedWith(Comparator { event1, event2
                ->
                event1.timestamp.compareTo(event2.timestamp)
            })

            eventsForAssetId.plus(Pair(first.assetId, sortedEvents))
        }
    }

    /// Fetches a specific asset based on the assetId from the data store
    ///
    /// - Parameter assetId: The id for the asset associated with these events
    /// - Returns: The events if available in the store, nil otherwise
    fun fetchEvents(forAssetId: String): List<AMBEvent>? {
        return eventsForAssetId[forAssetId]
    }

}