package com.ambrosus.ambviewer


import android.content.ActivityNotFoundException
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().getResults().observe(
                viewLifecycleOwner,
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
        loadingIndicator.visibility = View.INVISIBLE
        if(loadResult.isSuccessful()) {
            list.adapter = EventsAdapter(loadResult.data.items as List<Event>, context!!)
        } else {
            errorMessage.visibility = View.VISIBLE
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
    : androidx.recyclerview.widget.RecyclerView.Adapter<EventViewHolder>() {

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

        itemView.findViewById<View>(R.id.verificationIcon).setOnClickListener {
            val url = "https://ambrosus.github.io/app-checker/?eventId=${event.systemId}"
            try {
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                AMBSampleApp.errorHandler.handleError("Cant find browser app", e)
            }
        }
    }
}