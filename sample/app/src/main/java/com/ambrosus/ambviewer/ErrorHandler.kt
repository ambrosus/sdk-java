package com.ambrosus.ambviewer

import android.app.Application
import android.widget.Toast

class ErrorHandler(private val context: Application) {

    fun handleError(t: Throwable){
        val message = t.message ?: t.javaClass.name
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}