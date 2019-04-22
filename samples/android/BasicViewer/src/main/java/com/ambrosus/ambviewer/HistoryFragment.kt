package com.ambrosus.ambviewer


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.Representation
import com.ambrosus.ambviewer.utils.ViewUtils
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.AMBAssetInfo
import kotlinx.android.synthetic.main.fragment_history.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = HistoryAdapter(context!!)
    }

    class HistoryViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_history, parent, false)) {

        private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        private val calendar = Calendar.getInstance()

        fun update(historyItem: History.HistoryItem, previous: History.HistoryItem?) {

            val previousItemScanTime = previous?.scanTimeStamp ?: 0
            calendar.time = Date(previousItemScanTime)
            val previousItemScanDay = calendar.get(Calendar.DAY_OF_YEAR)

            calendar.time = Date(historyItem.scanTimeStamp)
            val itemScanDay = calendar.get(Calendar.DAY_OF_YEAR)

            ViewUtils.changeVisibility(itemView, R.id.creationDate, previousItemScanDay != itemScanDay)

            if(previousItemScanDay != itemScanDay) {
                calendar.time = Date(System.currentTimeMillis())
                val today = calendar.get(Calendar.DAY_OF_YEAR)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.get(Calendar.DAY_OF_YEAR)

                val scanDateText = when(itemScanDay) {
                    today -> itemView.context.getString(R.string.txtToday)
                    yesterday -> itemView.context.getString(R.string.txtYesterday)
                    else -> dateFormat.format(Date(historyItem.scanTimeStamp))
                }

                ViewUtils.setText(itemView, R.id.creationDate, scanDateText)
            }

            ViewUtils.setText(
                    itemView,
                    R.id.identifier,
                    getUserFriendlyIdentifiersText(
                            historyItem.identifiers.asList(),
                            itemView.context
                    )
            )

            when(historyItem.asset) {
                is AMBAssetInfo -> {
                    ViewUtils.setText(itemView, R.id.assetName, getAssetDisplayName(historyItem.asset.name))
                    ViewUtils.setText(itemView, R.id.assetID, historyItem.asset.assetId)
                }
                is Asset -> {
                    ViewUtils.setText(itemView, R.id.assetName, getAssetDisplayName(null))
                    ViewUtils.setText(itemView, R.id.assetID, historyItem.asset.systemId)
                }
            }
        }

        fun getAssetDisplayName(name: String?) = name ?: itemView.context.getString(R.string.txtNoName)

    }

    class HistoryAdapter(context: Context) : RecyclerView.Adapter<HistoryViewHolder>() {

        val items = History(context).getItems()
        val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryViewHolder(inflater, parent)

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.update(items[position], if(position > 0) items[position-1] else null)
        }
    }


}
