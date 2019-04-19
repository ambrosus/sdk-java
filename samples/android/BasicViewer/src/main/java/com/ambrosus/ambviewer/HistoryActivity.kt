package com.ambrosus.ambviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setTitle(R.string.titleHistory)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_arrow_blue)
    }
}