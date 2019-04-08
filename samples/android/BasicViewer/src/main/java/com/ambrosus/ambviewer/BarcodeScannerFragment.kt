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

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.fragment_barcode_scanner.*
import java.util.*

/**
 * Activities that contain this fragment must implement the [OnTopLevelFragmentInteractionListener]
 * interface to handle interaction events.
 */
class BarcodeScannerFragment : Fragment() {
    private val CAMERA_PERMISSION_REQUEST = 0

    private var mDeniedCameraAccess = false

    private var isScanning = true;

    private val scanCallBack = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            getParentAsCallBack()?.barcodeResult(result)

        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            getParentAsCallBack()?.possibleResultPoints(resultPoints)
        }

        private fun getParentAsCallBack(): BarcodeCallback? {
            return parentFragment as? BarcodeCallback
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_barcode_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeAndStartBarcodeScanning()
    }

    override fun onPause() {
        super.onPause()
        pauseScanning()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            pauseScanning()
        } else {
            resumeScanning()
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun grantCameraPermissionsThenStartScanning() {
        if (activity!!.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (mDeniedCameraAccess == false) {
                // It's pretty clear for why the camera is required. We don't need to give a
                // detailed reason.
                this.requestPermissions(arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST)
            }
        } else {
            // We already have the permission.
            resumeScanning()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false
                resumeScanning()
            } else {
                mDeniedCameraAccess = true
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()

        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning()
        } else {
            // Once the activity is in the foreground again, restart scanning.
            resumeScanning()
        }
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    private fun initializeAndStartBarcodeScanning() {
        val formats = Arrays.asList(BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.DATA_MATRIX, BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.CODE_128, BarcodeFormat.ITF, BarcodeFormat.UPC_E)
        barcodePicker.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodePicker.decodeContinuous(scanCallBack)
    }

    private fun pauseScanning() {
        barcodePicker.pause()
    }

    private fun resumeScanning() {
        if(isScanning && isResumed)
            barcodePicker.resume()
    }

    fun startScanning(){
        isScanning = true
        resumeScanning()
    }

    fun stopScanning(){
        isScanning = false
        pauseScanning()
    }

    companion object {
        fun addAsChild(root: Fragment, containerID: Int) {
            FragmentSwitchHelper.addChild(root, BarcodeScannerFragment(), containerID)
        }

        fun get(root: Fragment): BarcodeScannerFragment {
            for (fragment in root.childFragmentManager!!.fragments) {
                if(fragment is BarcodeScannerFragment)
                    return fragment
            }
            throw IllegalArgumentException("root fragment doesn't contain BarcodeScannerFragment as a child")
        }
    }

}

