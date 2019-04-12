package com.ambrosus.ambviewer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_status_loading.*

class LoadingStatusFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsDescription.text = getUserFriendlyIdentifierText(ARG_IDENTIFIER.get(this))
    }

    companion object {
        fun createFor(identifier: Identifier): LoadingStatusFragment {
            return ARG_IDENTIFIER.putTo(LoadingStatusFragment(), identifier)
        }
    }
}