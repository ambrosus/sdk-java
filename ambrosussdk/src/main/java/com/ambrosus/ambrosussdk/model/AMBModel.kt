package com.ambrosus.ambrosussdk.model

import com.ambrosus.ambrosussdk.utils.Section
import com.ambrosus.ambrosussdk.utils.SectionFormatter
import com.google.gson.GsonBuilder
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

sealed class AMBModel : Any, Serializable {
    //(val signature:String = "", val creator:String = "", var date:String = "", val timestamp:Double = 0.0)

    protected val contentKey = "content"
    protected val metadataKey = "metadata"
    protected val signatureKey = "signature"
    protected val idDataKey = "idData"
    protected val dataKey = "data"
    protected val createdByKey = "createdBy"
    protected val timestampKey = "timestamp"
    protected val assetIdKey = "assetId"
    protected val entriesKey = "entries"
    protected val productImageKey = "productImage"
    protected val nameKey = "name"
    protected val typeKey = "type"
    protected val imagesKey = "images"

    /// Location Keys
    protected val locationKey = "location"
    protected val geometryKey = "geometry"
    protected val propertiesKey = "properties"
    protected val coordinatesKey = "coordinates"

    /// A signature unique to this Asset, used to verify authenticity
    var signature: String? = null

    /// A formatted date converted from the creation timestamp (e.g. Sep 17, 2017)
    var date: String? = null

    /// The address of the creator of this Object
    var creator: String? = null

    /// A timestamp in seconds of when this Object was created
    var timestamp: Double = 0.0
    var imageUrl: String? = null

    var formattedSections: List<Section>? = null


    constructor(signature: String = "", date: String = "", creator: String =
            "", timestamp: Double = 0.0) {
        this.signature = signature
        this.creator = creator
        this.date = date
        this.timestamp = timestamp
    }

    constructor(map: Map<String, Any>?) {

        if (map == null) return

        val content = map[contentKey] as? Map<String, Any>

        val signature = content?.get(signatureKey) as? String
        val idData = content?.get(idDataKey) as? Map<String, Any>
        val creator = idData?.get(createdByKey) as? String
        val timestamp = idData?.get(timestampKey) as? Double

        this.signature = signature
        this.creator = creator
        this.timestamp = timestamp ?: 0.0
        this.date = "no-date"

        //we need a copy here to preserve the data
        val gson = GsonBuilder().create()
        val text = gson.toJson(map)
        this.formattedSections = SectionFormatter.getFormattedSections(gson
                .fromJson(text, HashMap::class.java) as HashMap<String, Any>)

        this.date = timestamp?.toDateString()
    }

    inner class AssetInfo : Serializable {
        var name: String? = null
        var images: Map<String, Any>? = null

        constructor(map: Map<String, Any>?) {
            if (map == null) return

            name = map[nameKey] as? String
            images = map[imagesKey] as? Map<String, Any>
        }

        fun getDefaultImageUrl(): String? {
            val myImages = images
            if (myImages != null && myImages.containsKey("default")) {
                val item = myImages["default"] as? Map<String, Any>
                if (item != null) {
                    if (item.containsKey("url")) {
                        return item["url"] as? String
                    }
                }
            }
            return null
        }
    }

}


fun Double.toDateString(): String {
    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.US)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    val toMillis = if (this > 1000000000000) this.toLong() else this.toLong() * 1000
//    return getDate(toMillis, "yyyy-MM-dd hh:mm:ss")
    return getDate(toMillis, "MMM dd, yyyy")
}

/// Assets are any item that can flow through a supply chain
open class AMBAsset : AMBModel {

    /// The unique identifier associated with only this Asset, used to map an asset to its associated Events
    var id: String? = null

    /// A descriptive name of the asset, optional
    var name: String? = null

    var events: List<AMBEvent>? = null
        set(value) {
            if (value != null) {
                for (event in value) {
                    if (event.imageURLString != null) {
                        imageUrl = event.imageURLString
                        break
                    }
                }
            }
            field = value // parses the string and assigns values to other
            // properties
        }

    constructor(map: Map<String, Any>?) : super(map) {
        if (map == null) return

        val id = map[assetIdKey] as? String ?: return
        this.id = id

        val content = map[contentKey] as? Map<String, Any>
        if (content != null) {
            val idData = content[idDataKey] as? Map<String, Any>
            if (idData != null) {
                this.name = idData[nameKey] as? String
            }
        }
    }

    /// Searches through all events for this asset for an image
//    public var imageURLString: String? {
//        val events = AMBDataStore.sharedInstance.eventStore.fetchEvents
//                (forAssetId: id)
//        return events?.flatMap { $0.imageURLString }.first
//    }

}


/// Events are any item that can flow through a supply chain
open class AMBEvent : AMBModel {

    private val eventIdKey = "eventId"
    private val locationEventType = "ambrosus.event.location"

    var id: String? = null
    var name: String? = null

    /// The unique identifier associated with only this Asset, used to map an asset to its associated Events
//    var id: String? = null

    /// The unique identifier for the asset this event happened to
    var assetId: String? = null

    /// The type describes the category of event that occurs, can be used as a name for the event as well
    var type: String? = null

    /// Finds an image if one is available for the event
    var imageURLString: String? = null

    /// The lattitude coordinates for this events location
    var lattitude: Double? = null

    /// The longitude coordinates for this events location
    var longitude: Double? = null

    /// A name describing where this event occured
    var locationName: String? = null

    var assetInfo: AssetInfo? = null

    constructor(id: String = "", assetId: String = "", type: String = "") : super() {
        this.id = id
        this.assetId = assetId
        this.type = type
    }


    constructor(map: Map<String, Any>?) : super(map) {
        if (map == null) return

        val id = map[eventIdKey] as? String
        val content = map[contentKey] as? Map<String, Any>
        val idData = content?.get(idDataKey) as? Map<String, Any>
        val dataList = (content?.get(dataKey) as? List<Map<String, Any>>)
        val data = dataList?.first()

        this.id = id
        this.type = data?.get(typeKey)?.toString()

        if (this.type != null) {
            if (this.type!!.contains("asset.info")) {
                assetInfo = AssetInfo(data)
                imageURLString = assetInfo?.getDefaultImageUrl()
                this.name
            }
        }

        val assetId = idData?.get(assetIdKey) as? String
        this.assetId = assetId

        if (dataList != null) {
            for (item in dataList) {
                if (locationEventType == item[typeKey]?.toString()) {
                    val location = item[locationKey] as? Map<String, Any>
                    if (location != null) {
                        val geometry = location[geometryKey] as? Map<String,
                                Any>
                        if (geometry != null) {
                            val coordinates = geometry[coordinatesKey] as
                                    List<Double>
                            lattitude = coordinates[0]
                            longitude = coordinates[1]
                        }
                    }
                    locationName = item[nameKey]?.toString()
                }
            }
        }

//        val geometryDictionary = formattedSections?.firstOrNull {
//            it.sectionName ==
//                    geometryKey
//        }
//        val lattitudeLongitude = geometryDictionary?.items?.get(coordinatesKey)
//                as? List<Double>
//        if (lattitudeLongitude != null && lattitudeLongitude.size > 1) {
//            lattitude = lattitudeLongitude[0]
//            longitude = lattitudeLongitude[1]
//        }
    }

    companion object {
        fun fetchEvents(map: Map<String, Any>?): List<AMBEvent> {
            if (map == null) return ArrayList()
            val results = map["results"] as? List<Map<String, Any>> ?: return ArrayList()
            val events = results.map { AMBEvent(it) }
            //events should be sorted from most recent to oldest
            events.sortedWith(kotlin.Comparator({ left, right ->
                left
                        .timestamp.compareTo(right.timestamp)
            }))
            return events
        }
    }

    val isLocationEvent: Boolean
        get() = locationEventType == type
}