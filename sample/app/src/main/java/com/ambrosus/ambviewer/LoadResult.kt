package com.ambrosus.ambviewer

import android.util.Log
import com.ambrosus.sdk.utils.Strings
import java.lang.IllegalStateException
import java.lang.NullPointerException

class LoadResult<T:Any> {

    private val result: Any

    constructor(error: Throwable) {
        result = error
    }

    constructor(data: T) {
        result = data
    }

    val data get() = if (result !is Throwable) (result as T) else throw IllegalStateException("wasn't able to get load result")
    val error get() = if (result is Throwable) result else throw IllegalStateException("has a successful result")

    fun isSuccessful() = result !is Throwable

    override fun toString(): String {
        return "${Strings.defaultToString(this)} : $result}"
    }
}