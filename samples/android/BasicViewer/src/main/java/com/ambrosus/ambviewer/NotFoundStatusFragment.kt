package com.ambrosus.ambviewer

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_status_not_found.*


class NotFoundStatusFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_not_found, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val identifiers = getIdentifiers()
        identifier.text = getUserFriendlyIdentifiersText(identifiers, context!!)
        bottomText.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)

            intent.putExtra(
                    SearchManager.QUERY,
                    getSearchRequestFor(identifiers)
            )
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                AMBSampleApp.errorHandler.handleError(e)
            }
        }
        view.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun getIdentifiers() = ARG_IDENTIFIERS.get(this).asList()

    private fun getSearchRequestFor(identifiers: List<Identifier>): String {
        val searchRequest = StringBuilder()
        for (identifier in identifiers) {
            searchRequest.append(
                    getUserFriendlyIdentifierText(identifier)
            )
            searchRequest.append(' ')
        }
        return searchRequest.toString()
    }

    companion object {
        fun createFor(identifiers: List<Identifier>): NotFoundStatusFragment {
            return ARG_IDENTIFIERS.putTo(NotFoundStatusFragment(), identifiers.toTypedArray())
        }
    }
}