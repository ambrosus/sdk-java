package com.ambrosus.ambviewer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.sdk.model.AMBAssetInfo
import kotlinx.android.synthetic.main.fragment_asset_details.*

class AssetDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_asset_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = PagerAdapter(arguments, childFragmentManager, context!!)
        viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.pageMargings)

        val asset = ARG_ASSET_DATA.get(this)
        if(asset is AMBAssetInfo) {
            //TODO add Image type to SDK
            asset.images["default"]?.get("url")?.asString?.let {
                GlideApp.with(this)
                        .load(it)
                        .placeholder(R.drawable.placeholder_logo)
                        .into(assetImage)
            }
        }
    }

    class PagerAdapter(
            private val args: Bundle?,
            fm: androidx.fragment.app.FragmentManager,
            private val context: Context)
        : FragmentPagerAdapter(fm) {

        private val fragments = arrayOf(
                        Pair(AssetInfoFragment::class.java, R.string.titleInfo),
                        Pair(IdentifiersFragment::class.java, R.string.titleIdentifiers),
                        Pair(EventsFragment::class.java, R.string.titleEvents)
        )

        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int) = instantiate(context, fragments[position].first.name, args)

        override fun getPageTitle(position: Int): CharSequence? = context.getString(fragments[position].second)
    }
}
