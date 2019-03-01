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
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.apps.SearchResultsListFragment
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.Query
import com.ambrosus.sdk.QueryBuilder
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_main_scanner.*
import java.io.Serializable

class MainScannerFragment :
        Fragment(),
        FragmentSwitchHelper.BackListener,
        BarcodeCallback,
        AssetInfoSearchFragment.SearchResultListener,
        LoadAssetFragment.LoadResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        BarcodeScannerFragment.addAsChild(this, R.id.scannerContainer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_scanner, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.main, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val authorized = AMBSampleApp.network.authToken != null
        menu!!.findItem(R.id.menu_item_logout).isVisible = authorized
        menu.findItem(R.id.menu_item_authorize).isVisible = !authorized
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            R.id.menu_item_authorize -> {
                FragmentSwitchHelper.showNextFragment(this, AuthorizationFragment())
                true
            }
            R.id.menu_item_logout -> {
                AMBSampleApp.network.authorize(null)
                activity!!.invalidateOptionsMenu()
                displayAuthorizationState()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Scanner")
        displayAuthorizationState()
    }

    private fun displayAuthorizationState() {
        val authToken = AMBSampleApp.network.authToken
        when (authToken) {
            null -> authorizationMessage.visibility = View.GONE
            else -> {
                authorizationMessage.visibility = View.VISIBLE
                authorizationMessage.setText("Authorized as ${authToken.account}");
            }
        }
    }

    override fun handleBackKey(): Boolean {
        return removeCurStatusFragment()
    }

    override fun barcodeResult(code: BarcodeResult?) {

        val supportedIdentifiers = mapOf(
                BarcodeFormat.EAN_13 to "EAN13",
                BarcodeFormat.EAN_8 to "EAN8",
                BarcodeFormat.CODE_128 to "GTIN"
        )

        val barcodeFormat = code!!.barcodeFormat
        val barcodeText = code.text

        var searchFragment: AssetInfoSearchFragment? = null

        if (barcodeFormat == BarcodeFormat.QR_CODE && barcodeText.startsWith("https://amb.to/0x")) {
            searchFragment = AssetInfoSearchFragment.createFor(barcodeText.replace("https://amb.to/", ""))
        } else if (supportedIdentifiers.contains(barcodeFormat)) {
            searchFragment = AssetInfoSearchFragment.createFor(Identifier(supportedIdentifiers[barcodeFormat], barcodeText))
        }

        if(searchFragment != null) {
            displayStatusFragment(searchFragment)
        } else {
            displayError("Unknown barcode data format.")
        }
    }

    private fun displayError(message: String) {
        removeCurStatusFragment()
        BarcodeScannerFragment.get(this).stopScanning()
        statusMessage.text = Html.fromHtml(message + "<br><br><b>Tap to continue.</b>")
        statusContainer.setOnClickListener {
            BarcodeScannerFragment.get(this).startScanning()
            statusMessage.text = "Place a barcode inside viewfinder rectangle to scan it"
            statusContainer.setOnClickListener(null)
        }
    }

    override fun onSearchResult(result: LoadResult<SearchResult<out Entity>>, searchCriteria: Serializable) {
        if(!processError(result)) {
            val searchResult = result.data
            val resultsList = result.data.values
            if(resultsList.isEmpty()) {
                if(searchCriteria is String)
                    displayAssetLoading(searchCriteria)
                else
                    displayError("Can't find any asset by $searchCriteria")
            } else if(resultsList.size == 1) {
                displayAsset(searchResult.values[0])
            } else {
                displayResultsList(searchResult.query)
            }
        }
    }

    private fun processError(result: LoadResult<*>): Boolean {
        if(!result.isSuccessful()) {
            displayError(AMBSampleApp.errorHandler.getErrorDescription(result.error))
        }
        return !result.isSuccessful()
    }

    private fun displayResultsList(query: Query<out Entity>) {
        removeCurStatusFragment()
        FragmentSwitchHelper.showNextFragment(this, SearchResultsListFragment.create(query))
    }

    override fun onLoadResult(result: LoadResult<Asset>) {
        if(!processError(result))
            displayAsset(result.data)
    }

    private fun displayAssetLoading(assetId: String){
        displayStatusFragment(LoadAssetFragment.createFor(assetId))
    }

    private fun removeCurStatusFragment(): Boolean {
        BarcodeScannerFragment.get(this).startScanning()
        return FragmentSwitchHelper.goBack(this, R.id.statusContainer)
    }

    private fun displayStatusFragment(fragment: Fragment) {
        removeCurStatusFragment()
        BarcodeScannerFragment.get(this).stopScanning()
        FragmentSwitchHelper.addChild(this, fragment, R.id.statusContainer, true, true)
    }

    private fun displayAsset(asset: Entity) {
        when(asset) {
            is AMBAssetInfo -> AssetActivity.startFor(asset, activity!!)
            is Asset -> AssetActivity.startFor(asset, activity!!)
            else -> throw IllegalStateException("Can't display $asset")
        }
        removeCurStatusFragment()
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}

}