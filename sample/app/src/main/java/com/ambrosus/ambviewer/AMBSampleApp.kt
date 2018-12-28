package com.ambrosus.ambviewer

import android.app.Application
import com.ambrosus.sdk.AMBNetwork

class AMBSampleApp : Application() {

    companion object {
        lateinit var ambNetwork: AMBNetwork
        lateinit var errorHandler: ErrorHandler private set
    }

    override fun onCreate() {
        super.onCreate()
        ambNetwork = AMBNetwork()
        errorHandler = ErrorHandler(this)
    }
}