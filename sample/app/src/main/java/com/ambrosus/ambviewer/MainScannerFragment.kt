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
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_main_scanner.*

class MainScannerFragment :
        Fragment(),
        FragmentSwitchHelper.BackListener,
        BarcodeCallback,
        AssetInfoSearchFragment.SearchResultListener,
        AssetIDsSearchFragment.SearchResultListener,
        LoadAssetFragment.LoadResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarcodeScannerFragment.addAsChild(this, R.id.scannerContainer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_scanner, container, false)
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Scanner")
    }

    override fun handleBackKey(): Boolean {
        return removeCurStatusFragment()
    }

    override fun barcodeResult(code: BarcodeResult?) {
        var searchFragment: AssetInfoSearchFragment? = null
        if (code!!.barcodeFormat == BarcodeFormat.QR_CODE && code.text.startsWith("https://amb.to/0x")) {
            searchFragment = AssetInfoSearchFragment.createFor(code.text.replace("https://amb.to/", ""))
        } else if (code.barcodeFormat == BarcodeFormat.EAN_13 || code.barcodeFormat == BarcodeFormat.EAN_8) {

            val identifierType =
                    when (code.barcodeFormat) {
                        BarcodeFormat.EAN_13 -> Identifier.EAN13
                        BarcodeFormat.EAN_8 -> Identifier.EAN8
                        else -> throw IllegalStateException("shouldn't happen")
                    }

            searchFragment = AssetInfoSearchFragment.createFor(Identifier(identifierType, code.text))
        }

        if(searchFragment != null) {
            displayStatusFragment(searchFragment)
        } else {
            BarcodeScannerFragment.get(this).stopScanning()
            statusMessage.text = Html.fromHtml("Unknown barcode data format.<br><br><b>Tap to continue.</b>")
            statusContainer.setOnClickListener {
                BarcodeScannerFragment.get(this).startScanning()
                statusMessage.text = "Place a barcode inside viewfinder rectangle to scan it"
                statusContainer.setOnClickListener(null)
            }
        }
    }

    override fun onSearchResult(result: List<AMBAssetInfo>, searchCriteria: Any) {
        if(result.isEmpty()) {
            when(searchCriteria) {
                is String -> displayAssetLoading(searchCriteria)
                is Identifier -> displayAssetIdSearch(searchCriteria)
            }
        } else {
            displayAsset(result[0])
        }
    }

    override fun onSearchResult(result: List<String>) {
        displayAssetLoading(result[0])
    }

    override fun onLoadResult(asset: Asset) {
        displayAsset(asset)
    }

    private fun displayAssetLoading(assetId: String){
        displayStatusFragment(LoadAssetFragment.createFor(assetId))
    }

    private fun displayAssetIdSearch(identifier: Identifier){
        displayStatusFragment(AssetIDsSearchFragment.createFor(identifier))
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

    private fun displayAsset(asset: Any) {
        when(asset) {
            is AMBAssetInfo -> AssetActivity.startFor(asset, activity!!)
            is Asset -> AssetActivity.startFor(asset, activity!!)
        }
        removeCurStatusFragment()
    }

    override fun onCancel() {
        removeCurStatusFragment()
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}

}