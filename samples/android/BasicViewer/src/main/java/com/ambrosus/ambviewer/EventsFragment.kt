package com.ambrosus.ambviewer


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.ViewUtils
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.EventQueryBuilder
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.AMBEvent
import com.ambrosus.sdk.model.AMBEventQueryBuilder
import com.ambrosus.sdk.model.Location
import kotlinx.android.synthetic.main.fragment_list_with_caption.*
import java.lang.IllegalArgumentException


class EventsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel().getResults().observe(
                this,
                Observer {
                    display(it!!)
                }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_list_with_caption, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        caption.setText(R.string.txtPublicEvents)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(context!!)
    }

    private fun display(loadResult: LoadResult<SearchResult<out Entity>>) {
        if(loadResult.isSuccessful()) {
            list.adapter = EventsAdapter(loadResult.data.items as List<Event>, context!!)
        } else {

        }
    }

    private fun getViewModel(): SingleSearchResultViewModel {
        val assetData = ARG_ASSET_DATA.get(this)
        val queryBuilder = when (assetData) {
            is Asset -> EventQueryBuilder().forAsset(assetData.systemId)
            is AMBAssetInfo -> AMBEventQueryBuilder().forAsset(assetData.assetId)
            else -> throw IllegalArgumentException("This activity can display only Asset or AssetInfo but got ${assetData.javaClass.name}")
        }
        val factory = SingleSearchResultViewModel.Factory(AMBSampleApp.network, queryBuilder.build())

        return ViewModelProviders.of(
                this,
                factory
        ).get(SingleSearchResultViewModel::class.java)
    }
}

private class EventsAdapter(
        private val events: List<Event>,
        context: Context)
    : RecyclerView.Adapter<EventViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): EventViewHolder
        = EventViewHolder(layoutInflater, parent)

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int) {
        viewHolder.update(events[position], position == 0, position == itemCount - 1)
    }
}


private class EventViewHolder(inflater: LayoutInflater, root: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_event, root, false)) {

    fun update(event: Event, firstItem: Boolean, lastItem: Boolean) {
        ViewUtils.setText(itemView, R.id.eventName, if (event is AMBEvent) event.name ?: event.type else event!!.systemId)
        ViewUtils.setDate(itemView, R.id.creationDate, event!!.timestamp)

        var eventLocation : Location? = null
        if(event is AMBEvent) eventLocation = event?.location

        val locationSting = StringBuffer()

        eventLocation?.city?.let {
            locationSting.append(it).append(", ")
        }

        eventLocation?.country?.let {
            locationSting.append(it)
        }

        if(locationSting.isEmpty())
            eventLocation?.name?.let { locationSting.append(it) }

        ViewUtils.setText(itemView, R.id.locationText, locationSting.toString())

        ViewUtils.setText(itemView, R.id.createdBy, itemView.context.getString(R.string.txtCreatedBy, event.accountAddress))

        ViewUtils.changeVisibilityVisible(itemView, R.id.topTimeLine, !firstItem)
        ViewUtils.changeVisibilityVisible(itemView, R.id.bottomTimeLine, !lastItem)

    }
}