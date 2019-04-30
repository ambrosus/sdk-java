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

package com.ambrosus.demoapp

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.demoapp.utils.TitleHelper
import com.ambrosus.sdk.AuthToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_main_scanner.*
import java.lang.NumberFormatException
import java.util.Scanner
import java.util.concurrent.TimeUnit

class AuthorizationFragment : androidx.fragment.app.Fragment(), BarcodeCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarcodeScannerFragment.addAsChild(this, R.id.scannerContainer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Scan Private Key")
    }

    override fun barcodeResult(result: BarcodeResult?) {
        BarcodeScannerFragment.get(this).stopScanning()
        val barcodeFormat = result!!.barcodeFormat
        if (barcodeFormat == BarcodeFormat.QR_CODE || barcodeFormat == BarcodeFormat.DATA_MATRIX) {
            try {
                val authToken = AuthToken.create(result.text, 5, TimeUnit.DAYS)
                AMBSampleApp.network.authorize(authToken)
                activity!!.onBackPressed()
                return
            } catch (e: NumberFormatException) {
                AMBSampleApp.errorHandler.handleError(e)
            }
        } else AMBSampleApp.messageHandler.displayErrorMessage("Expected QRCode or DataMatrix")
        BarcodeScannerFragment.get(this).startScanning()
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}


}