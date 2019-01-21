/*
 * Copyright: Ambrosus Technologies GmbH
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

package com.ambrosus.ambviewer

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambrosussdk.utils.Section
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.events_map_item.view.*


class EventRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TAG: String = EventRecyclerAdapter.javaClass.simpleName
        private const val DEFAULT_TYPE: Int = 1
        private const val EVENT_TYPE: Int = 2
        private const val MAP_TYPE: Int = 3

    }

    var dataset: List<Section>? = null

//    private var context: Context? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = viewGroup.context;
        var v: View? = null
        when (viewType) {
            DEFAULT_TYPE -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.item_asset, viewGroup, false)
            }
            EVENT_TYPE -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.item_asset_event, viewGroup, false)
                return EventViewHolder(v)
            }
            MAP_TYPE -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.events_map_item, viewGroup, false)
                return MapViewHolder(v)

            }
            else -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.item_asset, viewGroup, false)
            }
        }
        return DefaultViewHolder(v)
    }


    override fun getItemViewType(position: Int): Int {
        if (dataset?.get(position)?.sectionName == "events") {
            return EVENT_TYPE
        }
        if (dataset?.get(position)?.sectionName == "map") {
            return MAP_TYPE
        }
        return DEFAULT_TYPE
    }

    override fun onViewRecycled(viewHolder: RecyclerView.ViewHolder) {
        super.onViewRecycled(viewHolder)
        when (viewHolder.itemViewType) {
            MAP_TYPE -> {
                val holder = viewHolder as MapViewHolder
                if (holder.map != null) {
                    holder.map?.clear()
                    holder.map?.mapType = GoogleMap.MAP_TYPE_NONE
                }
            }
            else -> {

            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val context = viewHolder.itemView.context
        when (viewHolder.itemViewType) {
            DEFAULT_TYPE -> {
                val defaultViewHolder = viewHolder as DefaultViewHolder
                defaultViewHolder.itemTitle.text = dataset?.get(i)?.sectionName
                var tempTitleTv: TextView
                var tempSubtitleTv: TextView
                for ((key, value) in dataset?.get(i)?.items.orEmpty()) {
                    tempTitleTv = TextView(context)
                    val sb = StringBuilder(key)
                    sb.setCharAt(0, Character.toUpperCase(sb.get(0)))
                    tempTitleTv.text = sb.toString()
                    tempTitleTv.setTypeface(null, Typeface.BOLD)
                    context?.resources?.getColor(R.color.colorTextPrimary)?.let {
                        tempTitleTv.setTextColor(it)
                    }
                    defaultViewHolder.cardViewLayout.addView(tempTitleTv)

                    tempSubtitleTv = TextView(context)
                    if (key.toLowerCase() == "timestamp") {
                        tempSubtitleTv.text = (value as Double).toLong().toString()
                    } else {
                        tempSubtitleTv.setText(value.toString())
                    }
                    context?.resources?.getColor(R.color.colorTextSecondary)?.let {
                        tempSubtitleTv.setTextColor(it)
                    }
                    defaultViewHolder.cardViewLayout.addView(tempSubtitleTv)
                    tempSubtitleTv.setOnClickListener { context?.let { it1 -> (it as TextView).text.setClipboard(it1, key) } }
                }
            }
            EVENT_TYPE -> {
                val eventViewHolder = viewHolder as EventViewHolder
                eventViewHolder.itemTitle.text = dataset?.get(i)?.sectionName

                var tempCard: View
                var eventTitle: TextView
                var eventSubtitle: TextView
                var eventDate: TextView
                var eventVisibility: TextView
                var line1: View
                var line2: View
                var event: AMBEvent
                val eventList: List<AMBEvent> = (dataset?.get(i)?.items?.get
                ("events") as List<AMBEvent>)
                for (j in eventList.indices) {
                    tempCard = LayoutInflater.from(context).inflate(R.layout.item_event, null)
                    eventTitle = tempCard.findViewById(R.id.eventTitle)
                    eventSubtitle = tempCard.findViewById(R.id.eventSubTitle)
                    eventDate = tempCard.findViewById(R.id.eventDate)
                    eventVisibility = tempCard.findViewById(R.id.eventVisibility)
                    event = eventList[j]
                    eventTitle.text = event.type ?: event.id
                    eventDate.text = event.date
                    eventVisibility.text = generatePrivacy()
                    eventSubtitle.text = event.locationName ?: generateLocation()
                    eventViewHolder.eventsContainer.addView(tempCard)
                }

            }
            MAP_TYPE -> {
                val mapViewHolder = viewHolder as MapViewHolder
                val mapSection = dataset?.get(i) as Section
                val lat = mapSection.items["lat"] as Double
                val long = mapSection.items["long"] as Double
                mapViewHolder.onBind(lat, long)
//                mapViewHolder.mapViewOnResume()
//                mapViewHolder.mapView?.addMarker(MarkerOptions()
//                        .position(LatLng(0.0, 0.0))
//                        .title("Hello world"))
            }
            else -> {
            }
        }

    }

    private fun generateLocation(): String {
        val random = (0..3).shuffled().last()

        return when (random) {
            0 -> "Wisconsin, United States"
            1 -> "New York, NY"
            else -> ""
        }
    }

    private fun generatePrivacy(): String {
        val random = (0..2).shuffled().last()

        return when (random) {
            0 -> "Public"
            else -> "Private"
        }
    }

    inner class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView = itemView.findViewById(R.id.title)
        var cardViewLayout: LinearLayout = itemView.findViewById(R.id.cardViewLayout)

    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder
    (itemView) {

        var itemTitle: TextView = itemView.findViewById(R.id.title)
        var eventsContainer: LinearLayout = itemView.findViewById(R.id.eventsContainer)

    }

    inner class MapViewHolder(itemView: View) : RecyclerView.ViewHolder
    (itemView), OnMapReadyCallback {
        override fun onMapReady(map: GoogleMap?) {
            MapsInitializer.initialize(itemView.context.applicationContext)
            this.map = map

            if (lat != null && long != null) {
                map?.clear()
                val point = LatLng(lat!!, long!!)
                map?.addMarker(MarkerOptions().position(point))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, zoomLevel)
                map?.moveCamera(cameraUpdate)
                map?.uiSettings?.isZoomControlsEnabled = false
                map?.uiSettings?.isScrollGesturesEnabled = false

            }
        }

        val zoomLevel = 14f

        var mapView: MapView? = null
        var map: GoogleMap? = null

        var lat: Double? = null
        var long: Double? = null

        init {
            mapView = itemView.listItemMapView
        }

        fun onBind(lat: Double, long: Double?) {
            if (map == null) {
                this.lat = lat
                this.long = long
                mapView?.onCreate(null);
                mapView?.onResume();
                mapView?.getMapAsync(this)
            } else {
                map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                val point = LatLng(lat!!, long!!)
                map?.addMarker(MarkerOptions().position(point))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, zoomLevel)
                map?.moveCamera(cameraUpdate)
            }
        }

        fun mapViewOnCreate(savedInstanceState: Bundle) {
            if (mapView != null) {
                mapView?.onCreate(savedInstanceState)
            }
        }

        fun mapViewOnResume() {
            if (mapView != null) {
                mapView?.onResume()
            }
        }

        fun mapViewOnPause() {
            if (mapView != null) {
                mapView?.onPause()
            }
        }

        fun mapViewOnDestroy() {
            if (mapView != null) {
                mapView?.onDestroy()
            }
        }

        fun mapViewOnLowMemory() {
            if (mapView != null) {
                mapView?.onLowMemory()
            }
        }

        fun mapViewOnSaveInstanceState(outState: Bundle) {
            if (mapView != null) {
                mapView?.onSaveInstanceState(outState)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset?.size!!
    }

    fun getLineSeparator(): CharSequence {
        return System.getProperty("line.separator")
    }
}