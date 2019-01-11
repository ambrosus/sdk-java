package com.ambrosus.ambviewer

import android.app.Application
import android.widget.Toast

class ErrorHandler(private val context: Application) {

    fun handleError(t: Throwable){
        val message = t.javaClass.name + (if (t.message != null) ": " + t.message else "")
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}