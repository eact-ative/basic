package com.eactative.ua.rn

import android.app.Application
import android.util.Log
import com.eactative.rn.BuildConfig
import com.facebook.react.bridge.ReactMarker
import com.facebook.react.bridge.ReactMarkerConstants
import com.facebook.react.config.ReactFeatureFlags

open class RNApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // If you opted-in for the New Architecture, we enable the TurboModule system
        ReactFeatureFlags.useTurboModules = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
        ReactMarker.addListener { name, tag, instanceKey ->
            Log.e(
                "testp",
                "$name -- $tag -- $instanceKey"
            )
        }
        ReactMarker.addFabricListener { name, tag, instanceKey, timestamp ->
            Log.e(
                "testfabric",
                "$name -- $tag -- $instanceKey"
            )
        }
    }
}