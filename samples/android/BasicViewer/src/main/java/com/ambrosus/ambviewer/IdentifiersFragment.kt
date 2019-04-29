package com.ambrosus.ambviewer


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.Representation
import com.ambrosus.ambviewer.utils.RepresentationAdapter
import com.ambrosus.ambviewer.utils.RepresentationFactory
import com.ambrosus.ambviewer.utils.ViewUtils
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_list_with_caption.*
import java.lang.IllegalArgumentException


class IdentifiersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_list_with_caption, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        caption.setText(R.string.txtIdentifiers)
        loadingIndicator.visibility = View.INVISIBLE
        list.layoutManager = LinearLayoutManager(context!!)
        list.adapter =
                RepresentationAdapter.DataSetBuilder()
                        .addAll(getIdentifiers(), IdentifierRepresentation.factory)
                        .createAdapter(context!!)
    }

    private fun getIdentifiers() : List<Identifier>
        = when(val asset = ARG_ASSET_DATA.get(this)) {
            is AMBAssetInfo -> ArrayList(asset.identifiers)
            is Asset -> ArrayList()
            else -> throw IllegalArgumentException("not supported assed data")
        }

}

private class IdentifierRepresentation(inflater: LayoutInflater, parent: ViewGroup)
    : Representation<Identifier>(R.layout.item_identifier, inflater, parent) {

    override fun display(identifier: Identifier) {
        ViewUtils.setText(itemView, R.id.identifierType, identifier.type)
        ViewUtils.setText(itemView, R.id.identifierValue, identifier.value)
    }

    companion object {
        val factory = object : RepresentationFactory<Identifier>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Identifier> {
                return IdentifierRepresentation(inflater, parent)
            }
        }
    }

}



