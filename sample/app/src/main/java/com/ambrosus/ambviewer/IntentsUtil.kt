/*
 * Copyright: Ambrosus Technologies GmbH
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

import android.app.Activity
import android.content.Intent
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.sdk.Asset

class IntentsUtil() {
    companion object {
        fun runMainActivity(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java).apply {
                //                putExtra(KEY,VALUE) // put the init app values in here
            }
            activity.startActivity(intent)
        }

        fun runEventActivity(activity: Activity, event: AMBEvent) {
            val intent = Intent(activity, EventActivity::class.java).apply {
                putExtra("event", event)
            }
            activity.startActivity(intent)

        }

//        fun runAssetActivity(activity: Activity, asset: Asset) {
//            val intent = Intent(activity, AssetActivity::class.java).apply {
//                putExtra("asset", asset)
//            }
//            activity.startActivity(intent)
//        }
    }
}

