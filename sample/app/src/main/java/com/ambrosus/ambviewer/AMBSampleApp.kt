package com.ambrosus.ambviewer

import android.app.Application
import com.ambrosus.sdk.Network

class AMBSampleApp : Application() {

    companion object {
        lateinit var network: Network
        lateinit var errorHandler: ErrorHandler private set
    }

    override fun onCreate() {
        super.onCreate()
        network = Network()
        errorHandler = ErrorHandler(this)
    }
}