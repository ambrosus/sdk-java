package com.ambrosus.ambviewer

import android.app.Application
import android.util.Log
import android.widget.Toast

class ErrorHandler(private val context: Application) {

    fun handleError(t: Throwable){
        val message = getErrorMessage(t)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e(ErrorHandler::class.java.name, message, t)
    }

    fun getErrorMessage(t: Throwable) =
            t.javaClass.name + (if (t.message != null) ": " + t.message else "")


}