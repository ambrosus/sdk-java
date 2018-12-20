package com.ambrosus.ambviewer

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.ambrosus.ambrosussdk.network.AMBNetwork
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
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
        mBarcodePicker!!.pause()
        mPaused = true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mBarcodePicker!!.pause()
        } else {
            mBarcodePicker!!.resume()
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
            mBarcodePicker!!.resume()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false
                if (!mPaused) {
                    mBarcodePicker!!.resume()
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

        mPaused = false
        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning()
        } else {
            // Once the activity is in the foreground again, restart scanning.
            mBarcodePicker!!.resume()
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
        val data = code.text
        // Truncate code to certain length.
        if (onScanListener != null) {
            if (code.barcodeFormat == BarcodeFormat.QR_CODE && data.startsWith("https://amb.to/0x")) {
                val assetId = data?.replace("https://amb.to/", "")!!
                onScanListener?.didScanAsset(assetId);
            }
        } else {

            mBarcodePicker!!.pause()
            if (code.barcodeFormat == BarcodeFormat.QR_CODE && data.startsWith("https://amb.to/0x")) {
                val assetId = data?.replace("https://amb.to/", "")!!
                AMBNetwork.instance.requestAsset(assetId,
                        {
                            if (it != null) {
                                IntentsUtil.runAssetActivity(this!!.activity!!, it)

                            } else {
                                Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                            }
                            mBarcodePicker!!.resume()
                        })
            } else if (code.barcodeFormat == BarcodeFormat.EAN_13) {
                AMBNetwork.instance.requestAsset("data[identifiers.ean13]", data,
                        {
                            if (it != null) {
                                IntentsUtil.runAssetActivity(this!!.activity!!, it)

                            } else {
                                Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                            }
                            mBarcodePicker!!.resume()
                        })
            } else if (code.barcodeFormat == BarcodeFormat.EAN_8) {

                AMBNetwork.instance.requestAsset("data[identifiers.ean8]", data,
                        {
                            if (it != null) {
                                IntentsUtil.runAssetActivity(this!!.activity!!, it)

                            } else {
                                Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                            }
                            mBarcodePicker!!.resume()
                        })
            } else {
                mBarcodePicker!!.resume()
            }
        }

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
