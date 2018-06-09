package com.ambrosus.ambviewer

import android.app.Activity
import android.content.Intent
import com.ambrosus.ambrosussdk.model.AMBAsset
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambviewer.MainActivity

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

        fun runAssetActivity(activity: Activity, asset: AMBAsset) {
            val intent = Intent(activity, AssetActivity::class.java).apply {
                putExtra("asset", asset)
            }
            activity.startActivity(intent)
        }
    }
}

