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
import android.widget.RelativeLayout
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.*
//import com.ambrosus.TestFile
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.lang.IllegalStateException
import java.util.*

/**
 * Activities that contain this fragment must implement the [OnTopLevelFragmentInteractionListener]
 * interface to handle interaction events.
 */
class ViewerFragment : Fragment(), BarcodeCallback {
    private val CAMERA_PERMISSION_REQUEST = 0

    // The main object for recognizing and displaying barcodes.
    private var mBarcodePicker: DecoratedBarcodeView? = null
    private var mDeniedCameraAccess = false
    private var mPaused = true

    private var rootView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //TestFile.test()
        // Inflate the layout for this fragment
        val inflated = inflater!!.inflate(R.layout.fragment_viewer, container, false)

//        inflated.findViewById<View>(R.id.button_detail).setOnClickListener { view -> openDetail() }

        rootView = inflated as ViewGroup? // needed for later ui updates

        initializeAndStartBarcodeScanning()
        return inflated
    }

    override fun onPause() {
        super.onPause()

        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        pauseScanning()
        mPaused = true
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
                if (!mPaused) {
                    resumeScanning()
                }
            } else {
                mDeniedCameraAccess = true
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()

        TitleHelper.ensureTitle(this, "Scanner")

        mPaused = false
        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning()
        } else {
            // Once the activity is in the foreground again, restart scanning.
            resumeScanning()
        }
        //TODO: remove this
//        IntentsUtil.runAssetActivity(this!!.activity!!)

    }

    private lateinit var lParams: RelativeLayout.LayoutParams
    var onScanListener: OnScanCompleteListener? = null

    /**
     * Initializes and starts the bar code scanning.
     */
    fun initializeAndStartBarcodeScanning() {

        mBarcodePicker = rootView!!.findViewById(R.id.barcode_scanner)
        val formats = Arrays.asList(BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.DATA_MATRIX, BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.CODE_128, BarcodeFormat.ITF, BarcodeFormat.UPC_E)
        mBarcodePicker!!.getBarcodeView().setDecoderFactory(DefaultDecoderFactory(formats))
        mBarcodePicker!!.decodeContinuous(this)
    }


    override fun barcodeResult(code: BarcodeResult) {

        val network: Network = AMBSampleApp.network

        val data = code.text
        // Truncate code to certain length.
        if (onScanListener != null) {
            if (code.barcodeFormat == BarcodeFormat.QR_CODE && data.startsWith("https://amb.to/0x")) {
                val assetId = data?.replace("https://amb.to/", "")!!
                onScanListener?.didScanAsset(assetId);
            }
        } else {
            pauseScanning()
            if (code.barcodeFormat == BarcodeFormat.QR_CODE && data.startsWith("https://amb.to/0x")) {
                val assetId = data?.replace("https://amb.to/", "")!!
                FragmentSwitchHelper.showNextFragment(this, AMBAssetSearchFragment.createFor(assetId))
            } else if (code.barcodeFormat == BarcodeFormat.EAN_13 || code.barcodeFormat == BarcodeFormat.EAN_8) {

                val identifierType =
                        when (code.barcodeFormat) {
                            BarcodeFormat.EAN_13 -> Identifier.EAN13
                            BarcodeFormat.EAN_8 -> Identifier.EAN8
                            else -> throw IllegalStateException("shouldn't happen")
                        }

                val assetIdentifier = Identifier(identifierType, data)
                FragmentSwitchHelper.showNextFragment(this, AMBAssetSearchFragment.createFor(assetIdentifier));
            } else {
                resumeScanning()
            }
        }

    }

    private fun pauseScanning() {
        mBarcodePicker!!.pause()
    }

    private fun resumeScanning() {
        mBarcodePicker!!.resume()
    }

    override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}

    companion object {
        private val TAG: String = "ViewerFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment GWMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): ViewerFragment {
            val fragment = ViewerFragment()
            return fragment
        }
    }
}

interface OnScanCompleteListener {
    fun didScanAsset(assetId: String)
}
