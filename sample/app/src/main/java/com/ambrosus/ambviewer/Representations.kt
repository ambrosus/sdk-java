package com.ambrosus.ambviewer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ambrosus.ambviewer.utils.Representation
import com.ambrosus.ambviewer.utils.RepresentationAdapter
import com.ambrosus.ambviewer.utils.RepresentationFactory
import com.ambrosus.ambviewer.utils.ViewUtils
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.model.Location
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.util.Date

fun addSection(dataSetBuilder: RepresentationAdapter.DataSetBuilder, title: String, data: Map<String, JsonElement>) {
    if(!data.isEmpty()) {
        dataSetBuilder.add(title, SectionTitleRepresentation.factory)
        dataSetBuilder.add(data, SectionRepresentation.factory)
    }
}

class SectionTitleRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<String>(R.layout.item_section_title, inflater, parent) {

    private val title: TextView = itemView as TextView

    override fun display(data: String?) {
        title.text = data!!
    }

    companion object {
        val factory = object : RepresentationFactory<String>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<String> {
                return SectionTitleRepresentation(inflater, parent)
            }
        }
    }

}

class SectionRepresentation(private val inflater: LayoutInflater, parent: ViewGroup) : Representation<Map<String, Any>>(R.layout.item_section, inflater, parent) {

    private val itemsLayout: LinearLayout = itemView.findViewById(R.id.items)

    override fun display(data: Map<String, Any>?) {
        itemsLayout.removeAllViews();
        addItems(data!!.entries, true)
    }

    private fun addItems(entries: Set<Map.Entry<String, Any>>, expandJsonObjects: Boolean){
        for ((key, value) in entries) {
            if(expandJsonObjects && value is JsonObject) {
                addTextView(R.layout.text_view_section_header, key.capitalize())
                addItems(value.entrySet(), false)
            } else addItem(key, if(value is JsonPrimitive) value.asString else value.toString())
        }
    }

    private fun addItem(key: String, value: String){
        addTextView(R.layout.text_view_section_key, key.capitalize())
        val valueView = addTextView(R.layout.text_view_section_value, value)
        valueView.setOnClickListener { (it as TextView).text.setClipboard(itemView.context, key) }
    }

    private fun addTextView(layoutRes: Int, text: String): TextView {
        val textView =  inflater.inflate(layoutRes, itemsLayout, false) as TextView
        itemsLayout.addView(textView)
        textView.text = text
        return textView
    }

    companion object {
        val factory = object : RepresentationFactory<Map<String, Any>>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Map<String, Any>> {
                return SectionRepresentation(inflater, parent)
            }
        }
    }
}

class ShortEventRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<Event>(R.layout.item_event, inflater, parent) {

    override fun display(event: Event?) {
        ViewUtils.setText(itemView, R.id.eventTitle, if (event is com.ambrosus.sdk.model.AMBEvent) event.name ?: event.type else event!!.systemId)
        ViewUtils.setDate(itemView, R.id.eventDate, Date(event!!.gmtTimeStamp))

        //TODO need to understand how to check if event public or private
        var eventLocation : Location? = null
        if(event is com.ambrosus.sdk.model.AMBEvent) eventLocation = event?.location

        val locationSting = StringBuffer()

        eventLocation?.city?.let {
            locationSting.append(it).append(", ")
        }

        eventLocation?.country?.let {
            locationSting.append(it)
        }

        if(locationSting.endsWith(", "))
            locationSting.removeSuffix(", ")

        if(locationSting.isEmpty())
            eventLocation?.name?.let { locationSting.append(it) }

        ViewUtils.setText(
                itemView,
                R.id.eventSubTitle,
                locationSting.toString()
        )
        ViewUtils.setText(itemView, R.id.eventVisibility, generatePrivacy())

        itemView.setOnClickListener {
            EventActivity.startFor(event, itemView.context)
        }
    }

    private fun generatePrivacy(): String {
        val random = (0..2).shuffled().last()

        return when (random) {
            0 -> "Public"
            else -> "Private"
        }
    }


    companion object {
        val factory = object : RepresentationFactory<Event>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Event> {
                return ShortEventRepresentation(inflater, parent)
            }
        }
    }
}

private fun CharSequence.setClipboard(context: Context, label: String) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
        clipboard.text = this
    } else {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", this)
        clipboard.primaryClip = clip
    }
    Toast.makeText(context, "Copied $label!", Toast
            .LENGTH_LONG).show()
}