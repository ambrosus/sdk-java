package com.ambrosus.ambviewer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.SearchManager
import android.content.Intent
import kotlinx.android.synthetic.main.fragment_status_error.*

class ErrorStatusFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomText.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

}