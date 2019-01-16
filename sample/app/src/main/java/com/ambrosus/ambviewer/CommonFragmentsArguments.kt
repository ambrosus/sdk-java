package com.ambrosus.ambviewer

import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.sdk.model.Identifier

val ARG_ID = BundleArgument<String>("KEY_ARG_ID", String::class.java)
val ARG_IDENTIFIER = BundleArgument<Identifier>("KEY_ARG_IDENTIFIER", Identifier::class.java)