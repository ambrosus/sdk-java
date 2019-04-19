/*
 * Copyright: Ambrosus Inc.
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

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
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
import com.ambrosus.sdk.EntityNotFoundException
import com.ambrosus.sdk.Query
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import com.ambrosus.sdk.utils.AmbrosusLinkParser
import com.ambrosus.sdk.utils.GS1DataMatrixHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_main_scanner.*
import kotlinx.android.synthetic.main.view_finder.*
import java.net.URISyntaxException

class MainScannerFragment :
        Fragment(),
        FragmentSwitchHelper.BackListener,
        BarcodeCallback,
        AssetInfoSearchFragment.SearchResultListener,
        LoadAssetFragment.LoadResultListener {

    private val TAG = javaClass.name

    val history by lazy {
        History(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        BarcodeScannerFragment.addAsChild(this, R.id.scannerContainer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnHistory.setOnClickListener {
            startActivity(Intent(context!!, HistoryActivity::class.java))
        }
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
                //displayAuthorizationState()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Scanner")
        //displayAuthorizationState()

        //executing all pending transactions, making view finder visible if there are no any status fragments
        //(we leave it invisible when switching to AssetActivity in order to get rid of UI flashing)
        childFragmentManager.executePendingTransactions()
        if(childFragmentManager.findFragmentById(R.id.statusContainer) == null)
            switchViewFinderVisibility(View.VISIBLE)
    }

//    private fun displayAuthorizationState() {
//        val authToken = AMBSampleApp.network.authToken
//        when (authToken) {
//            null -> authorizationMessage.visibility = View.GONE
//            else -> {
//                authorizationMessage.visibility = View.VISIBLE
//                authorizationMessage.setText("Authorized as ${authToken.accountAddress}");
//            }
//        }
//    }

    override fun handleBackKey(): Boolean {
        removeCurActionFragment()
        return removeCurStatusFragment()
    }

    private val barcodeFormatsIdentifierTypes = mapOf(
            BarcodeFormat.EAN_13 to Identifier.EAN13,
            BarcodeFormat.EAN_8 to Identifier.EAN8,
            BarcodeFormat.CODE_128 to Identifier.GTIN
    )

    override fun barcodeResult(code: BarcodeResult?) {
        val barcodeFormat = code!!.barcodeFormat
        val barcodeText = code.text

        val assetIdentifiers = ArrayList<Identifier>()

        if (barcodeFormat == BarcodeFormat.QR_CODE) {
            try {
                assetIdentifiers.add(Id(AmbrosusLinkParser.getAssetID(barcodeText)))
            } catch (extractIDException: URISyntaxException) {
                try {
                    assetIdentifiers.addAll(AmbrosusLinkParser.extractIdentifiers(barcodeText))
                } catch (extractIdentifiersException: URISyntaxException) {
                    Log.e(TAG, "Can't parse QRCode data", extractIDException)
                    Log.e(TAG, "Can't parse QRCode data", extractIdentifiersException)
                }
            }
        } else if (barcodeFormat == BarcodeFormat.DATA_MATRIX) {
            try {
                assetIdentifiers.addAll(GS1DataMatrixHelper.extractIdentifiers(barcodeText))
            } catch (e: GS1DataMatrixHelper.IllegalDataFormatException) {
                Log.e(TAG, "Can't parse data matrix data", e)
            }
        } else if (barcodeFormatsIdentifierTypes.contains(barcodeFormat)) {
            assetIdentifiers.add(Identifier(barcodeFormatsIdentifierTypes[barcodeFormat], barcodeText))
        }

        if(assetIdentifiers.size != 0) {
            searchFor(convertToDemoStandDataFormat(assetIdentifiers))
        } else {
            displayNotFound(listOf(Identifier(barcodeFormat.toString(), barcodeText)))
        }
    }

    private fun convertToDemoStandDataFormat(sdkIdentifiers: List<Identifier>) : List<Identifier> {
        val demoDataTypesMap = mapOf(
            Identifier.EAN13 to Identifier.EAN13.toUpperCase(),
            Identifier.EAN8 to Identifier.EAN8.toUpperCase(),
            Identifier.GTIN to Identifier.GTIN.toUpperCase(),
            Identifier.LOT to "Lot",
            Identifier.SERIAL to "Serial Number"
        )

        val result = ArrayList<Identifier>()
        for (sdkIdentifier in sdkIdentifiers) {
            val demoDataIdentifierType = demoDataTypesMap[sdkIdentifier.type]
            if (demoDataIdentifierType != null)
                result.add(Identifier(demoDataIdentifierType, sdkIdentifier.value))
            else
                result.add(sdkIdentifier)
        }
        return result
    }

    private fun searchFor(assetIdentifiers: List<Identifier>) {
        displayLoadingStatus(assetIdentifiers)
        performAction(AssetInfoSearchFragment.createFor(assetIdentifiers))
    }

    private fun displayLoadingStatus(assetIdentifiers: List<Identifier>){
        displayStatusFragment(
                LoadingStatusFragment.createFor(assetIdentifiers)
        )
    }

    private fun displayNotFound(assetIdentifiers: List<Identifier>) {
        displayStatusFragment(
                NotFoundStatusFragment.createFor(assetIdentifiers)
        )
    }

    private fun displayError(message: String) {
        Log.e(TAG, message);
        displayStatusFragment(ErrorStatusFragment())
    }

    override fun onSearchResult(result: LoadResult<SearchResult<out Entity>>, searchCriteria: List<Identifier>) {
        removeCurActionFragment() //removing fragment which performed search
        if(!processError(result, searchCriteria)) {
            val searchResult = result.data
            val resultsList = result.data.items
            if(resultsList.isEmpty()) {
                if(searchCriteria[0] is Id)
                    loadAsset(searchCriteria[0] as Id)
                else
                    displayNotFound(searchCriteria)
            } else if(resultsList.size > 0) {
                displayAsset(searchResult.items[0], searchCriteria)
            }
//            else {
//                displayResultsList(searchResult.query)
//            }
        }
    }


    private fun processError(result: LoadResult<*>, assetIdentifiers: List<Identifier>): Boolean {
        if(!result.isSuccessful()) {
            if(result.error !is EntityNotFoundException) {
                Log.e(TAG, "Can't perform request due to: ", result.error)
                displayError(AMBSampleApp.errorHandler.getErrorDescription(result.error))
            } else {
                displayNotFound(assetIdentifiers)
            }
        }
        return !result.isSuccessful()
    }

    private fun displayResultsList(query: Query<out Entity>) {
        removeCurStatusFragment()
        FragmentSwitchHelper.showNextFragment(this, SearchResultsListFragment.create(query))
    }

    override fun onLoadResult(result: LoadResult<Asset>, assetIdentifier: Id) {
        removeCurActionFragment() //removing fragment which performed loading
        if(!processError(result, listOf(assetIdentifier))) {
            displayAsset(result.data, listOf<Identifier>(assetIdentifier))
        }
    }

    private fun loadAsset(assetIdentifier: Id){
        performAction(LoadAssetFragment.createFor(assetIdentifier))
    }

    private fun displayStatusFragment(fragment: Fragment) {
        removeCurStatusFragment()
        switchViewFinderVisibility(View.INVISIBLE)
        if(fragment is NotFoundStatusFragment || fragment is ErrorStatusFragment) {
            switchAdvancedUIVisibility(View.INVISIBLE)
        }
        BarcodeScannerFragment.get(this).stopScanning()
        FragmentSwitchHelper.addChild(this, fragment, R.id.statusContainer, true, true)
    }

    private fun removeCurStatusFragment(): Boolean {
        BarcodeScannerFragment.get(this).startScanning()

        switchViewFinderVisibility(View.VISIBLE)
        switchAdvancedUIVisibility(View.VISIBLE)

        return FragmentSwitchHelper.goBack(this, R.id.statusContainer)
    }

    private fun switchViewFinderVisibility(visibility: Int) {
        zxing_viewfinder_view.visibility = visibility
        statusContainer.setBackgroundColor(
                ContextCompat.getColor(
                        context!!,
                        if (visibility == View.INVISIBLE) R.color.viewFinderMaskColor else android.R.color.transparent
                )
        )
    }

    private fun switchAdvancedUIVisibility(visibility: Int){
        copyright.visibility = visibility
        scanBarcodeHint.visibility = visibility
    }

    private val actionFragmentTag = "actionFragment"

    private fun removeCurActionFragment() {
        val curActionFragment = childFragmentManager!!.findFragmentByTag(actionFragmentTag)
        if(curActionFragment != null) {
            val ft = childFragmentManager!!.beginTransaction()
            ft.remove(curActionFragment)
            ft.commit()
        }
    }

    private fun performAction(fragment: Fragment) {
        removeCurActionFragment()
        val ft = childFragmentManager!!.beginTransaction()
        ft.add(0, fragment, actionFragmentTag)
        ft.commit()
    }

    private fun displayAsset(asset: Entity, identifiers: List<Identifier>) {

        when(asset) {
            is AMBAssetInfo -> {
                history.addAssetInfo(asset, identifiers)
                AssetActivity.startFor(asset, activity!!)
            }
            is Asset -> {
                history.addAsset(asset, identifiers)
                AssetActivity.startFor(asset, activity!!)
            }
            else -> throw IllegalStateException("Can't display $asset")
        }
        val items = history.getItems()
        removeCurStatusFragment()
        switchViewFinderVisibility(View.INVISIBLE) // doing this to get rid of flashing before switching to asset activity
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}

}