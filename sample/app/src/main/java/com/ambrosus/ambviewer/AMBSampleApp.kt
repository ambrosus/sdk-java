package com.ambrosus.ambviewer

import android.app.Application
import com.ambrosus.sdk.Network
import com.ambrosus.sdk.model.AMBNetwork

class AMBSampleApp : Application() {

    companion object {
        lateinit var network: AMBNetwork
        lateinit var errorHandler: ErrorHandler private set
    }

    override fun onCreate() {
        super.onCreate()
        network = AMBNetwork()
        errorHandler = ErrorHandler(this)
    }
}