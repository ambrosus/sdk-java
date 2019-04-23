/*
 * Copyright: Ambrosus Inc.
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.ambviewer

import android.os.Bundle
import android.support.v4.app.Fragment
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.model.Identifier

val ARG_IDENTIFIER = BundleArgument<Identifier>("KEY_ARG_IDENTIFIER", Identifier::class.java)
val ARG_IDENTIFIERS = BundleArgument<Array<Identifier>>("KEY_ARG_IDENTIFIERS", Array<Identifier>::class.java)
val ARG_ASSET_DATA = BundleArgument<Entity>("KEY_ASSET_DATA", Entity::class.java)

fun setArguments(args: Bundle?, destination: Fragment) : Fragment {
    destination.arguments = args
    return destination
}