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
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode

/**
 * Activities that contain this fragment must implement the [OnTopLevelFragmentInteractionListener]
 * interface to handle interaction events.
 */
class ViewerFragment : Fragment(), OnScanListener {
    private val CAMERA_PERMISSION_REQUEST = 0

    // The main object for recognizing and displaying barcodes.
    private var mBarcodePicker: BarcodePicker? = null
    private var mDeniedCameraAccess = false
    private var mPaused = true
    private var mToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        mBarcodePicker!!.stopScanning()
        mPaused = true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mBarcodePicker!!.stopScanning()
        } else {
            mBarcodePicker!!.startScanning()
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
            mBarcodePicker!!.startScanning()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false
                if (!mPaused) {
                    mBarcodePicker!!.startScanning()
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
            mBarcodePicker!!.startScanning()
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
        ScanditLicense.setAppKey(sScanditSdkAppKey)
        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        val settings = ScanSettings.create()
        val symbologiesToEnable = intArrayOf(Barcode.SYMBOLOGY_EAN13, Barcode.SYMBOLOGY_EAN8, Barcode.SYMBOLOGY_UPCA, Barcode.SYMBOLOGY_DATA_MATRIX, Barcode.SYMBOLOGY_QR, Barcode.SYMBOLOGY_CODE39, Barcode.SYMBOLOGY_CODE128, Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5, Barcode.SYMBOLOGY_UPCE)
        for (sym in symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true)
        }

        // Some 1d barcode symbologies allow you to encode variable-length data. By default, the
        // Scandit BarcodeScanner SDK only scans barcodes in a certain length range. If your
        // application requires scanning of one of these symbologies, and the length is falling
        // outside the default range, you may need to adjust the "active symbol counts" for this
        // symbology. This is shown in the following few lines of code.

        val symSettings = settings.getSymbologySettings(Barcode.SYMBOLOGY_CODE39)
        val activeSymbolCounts = shortArrayOf(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        symSettings.activeSymbolCounts = activeSymbolCounts
        // For details on defaults and how to calculate the symbol counts for each symbology, take
        // a look at http://docs.scandit.com/stable/c_api/symbologies.html.

        // Prefer the back-facing camera, is there is any.
        settings.cameraFacingPreference = ScanSettings.CAMERA_FACING_BACK

        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        val emulatePortraitMode = !BarcodePicker.canRunPortraitPicker()
        if (emulatePortraitMode) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }


        val picker = BarcodePicker(activity, settings)
        mBarcodePicker = picker
        applyUISettings(activity!!, picker.overlayView)
        mBarcodePicker?.setOnScanListener(this)

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        val wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val display = wm!!.defaultDisplay

        lParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        lParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        rootView?.findViewById<ViewGroup>(R.id.rl_content)?.addView(picker, lParams)

    }

    /**
     * Called when a barcode has been decoded successfully.
     */
    override fun didScan(session: ScanSession) {
        var message = ""
        for (code in session.newlyRecognizedCodes) {
            val data = code.data
            // Truncate code to certain length.
            if (onScanListener != null) {
                if (code.symbology == Barcode.SYMBOLOGY_QR && data.startsWith("https://amb.to/0x")) {
                    val assetId = data?.replace("https://amb.to/", "")!!
                    onScanListener?.didScanAsset(assetId);
                }
            } else {

                mBarcodePicker!!.pauseScanning()
                if (code.symbology == Barcode.SYMBOLOGY_QR && data.startsWith("https://amb.to/0x")) {
                    val assetId = data?.replace("https://amb.to/", "")!!
                    AMBNetwork.instance.requestAsset(assetId,
                            {
                                if (it != null) {
                                    IntentsUtil.runAssetActivity(this!!.activity!!, it)

                                } else {
                                    Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                                }
                                mBarcodePicker!!.resumeScanning()
                            })
                } else if (code.symbology == Barcode.SYMBOLOGY_EAN13) {
                    AMBNetwork.instance.requestAsset("data[identifiers.ean13]", data,
                            {
                                if (it != null) {
                                    IntentsUtil.runAssetActivity(this!!.activity!!, it)

                                } else {
                                    Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                                }
                                mBarcodePicker!!.resumeScanning()
                            })
                } else if (code.symbology == Barcode.SYMBOLOGY_EAN8) {

                    AMBNetwork.instance.requestAsset("data[identifiers.ean8]", data,
                            {
                                if (it != null) {
                                    IntentsUtil.runAssetActivity(this!!.activity!!, it)

                                } else {
                                    Toast.makeText(activity, "No data in server for this code", Toast.LENGTH_LONG).show()
                                }
                                mBarcodePicker!!.resumeScanning()
                            })
                } else {
                    mBarcodePicker!!.resumeScanning()
                }
            }
        }
    }


    companion object {
        private val TAG: String = "ViewerFragment"

        // In order to enable the scanner you need a key for Scandit SDK, you can  sign up
        // for a 30 day Scandit trial here: https://ssl.scandit.com/customers/new?p=test
        private const val sScanditSdkAppKey = "[YOUR SCANDIT KEY HERE]"

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

    fun applyUISettings(context: Context, overlay: ScanOverlay) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val viewfinder_val = Integer.valueOf(prefs.getString("viewfinder_style", "0"))
        if (viewfinder_val == 0) {
            overlay.setGuiStyle(ScanOverlay.GUI_STYLE_DEFAULT)
        } else if (viewfinder_val == 1) {
            overlay.setGuiStyle(ScanOverlay.GUI_STYLE_LASER)
        } else if (viewfinder_val == 2) {
            overlay.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
        } else {
            overlay.setGuiStyle(ScanOverlay.GUI_STYLE_LOCATIONS_ONLY)
        }

        overlay.setViewfinderDimension(
                prefs.getInt("viewfinder_width", 65) / 100.0f,
                prefs.getInt("viewfinder_height", 65) / 100.0f,
                prefs.getInt("viewfinder_landscape_width", 65) / 100.0f,
                prefs.getInt("viewfinder_landscape_height", 65) / 100.0f)

        overlay.setBeepEnabled(prefs.getBoolean("beep_enabled", true))
        overlay.setVibrateEnabled(prefs.getBoolean("vibrate_enabled", false))


        overlay.setTorchEnabled(prefs.getBoolean("torch_enabled", true))
        overlay.setTorchButtonMarginsAndSize(prefs.getInt("torch_button_x", 15),
                prefs.getInt("torch_button_y", 15),
                40, 40)

        val cameraSwitchVisibility = getCameraSwitchVisibility(prefs)
        overlay.setCameraSwitchVisibility(cameraSwitchVisibility)
        overlay.setCameraSwitchButtonMarginsAndSize(prefs.getInt("camera_switch_button_x", 5),
                prefs.getInt("camera_switch_button_y", 5),
                40, 40)
    }

    private fun getCameraSwitchVisibility(prefs: SharedPreferences): Int {
        val `val` = Integer.valueOf(prefs.getString("camera_switch_visibility", "0"))
        var cameraSwitchVisibility = ScanOverlay.CAMERA_SWITCH_NEVER
        if (`val` == 1) {
            cameraSwitchVisibility = ScanOverlay.CAMERA_SWITCH_ON_TABLET
        } else if (`val` == 2) {
            cameraSwitchVisibility = ScanOverlay.CAMERA_SWITCH_ALWAYS
        }
        return cameraSwitchVisibility
    }

}

interface OnScanCompleteListener {
    fun didScanAsset(assetId: String)
}
