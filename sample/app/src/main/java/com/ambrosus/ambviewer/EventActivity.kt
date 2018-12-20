package com.ambrosus.ambviewer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambrosussdk.utils.Section
import kotlinx.android.synthetic.main.activity_event.*

class EventActivity : AppCompatActivity() {

    private var toolbarImage: ImageView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: EventRecyclerAdapter? = null
    private var event: AMBEvent? = null
    private var allSections: MutableList<Section>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        toolbarImage = findViewById(R.id.toolbarImage)
        eventAssetToolbar.setTitleTextColor(resources.getColor(R.color
                .colorTextPrimary))
        setSupportActionBar(eventAssetToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        layoutManager = LinearLayoutManager(this)
        rvEventsList.layoutManager = layoutManager

        adapter = EventRecyclerAdapter()
        event = intent.extras.getSerializable("event") as AMBEvent
        allSections = event?.formattedSections?.toMutableList()

        if (event?.locationName != null && event?.longitude != null && event?.lattitude != null) {
            allSections?.add(Section("map", mapOf("lat" to event?.lattitude!!,
                    "long" to event?.longitude!!)))
        }

        adapter?.dataset = allSections
        rvEventsList.adapter = adapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
