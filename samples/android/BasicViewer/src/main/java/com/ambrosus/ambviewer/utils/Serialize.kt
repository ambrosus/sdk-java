package com.ambrosus.ambviewer.utils

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private const val BASE64_OPTIONS = Base64.NO_WRAP

public fun serializeToString(model: Serializable) : String {
    val output = ByteArrayOutputStream()
    ObjectOutputStream(output).use { outputStream ->
        outputStream.writeObject(model)
    }
    return Base64.encodeToString(output.toByteArray(), BASE64_OPTIONS)
}

public fun deserializeFromString(model: String) : Any {
    ObjectInputStream(ByteArrayInputStream(Base64.decode(model, BASE64_OPTIONS))).use {
        return it.readObject()
    }
}
