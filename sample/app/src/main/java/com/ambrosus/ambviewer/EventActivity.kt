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
