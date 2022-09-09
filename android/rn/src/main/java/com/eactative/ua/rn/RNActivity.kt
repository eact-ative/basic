package com.eactative.ua.rn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eactative.ua.service.ModuleInfo
import com.eactative.ua.service.Source
import com.facebook.react.ReactRootView
import com.facebook.soloader.SoLoader

class RNActivity : AppCompatActivity() {
    private lateinit var reactRootView: ReactRootView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        if(reactRootView != null) {
            reactRootView.reactInstanceManager?.onHostPause(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if(reactRootView != null) {
            reactRootView.reactInstanceManager?.onHostResume(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(reactRootView != null) {
            reactRootView.reactInstanceManager?.onHostDestroy(this)
            reactRootView.unmountReactApplication()
        }
    }

    private fun load() {
        SoLoader.init(this, false)
        reactRootView = ReactRootView(this)
        val moduleInfo = ModuleInfo(
            "aa",
            "1234",
            1,
            "Android",
            "RN",
            arrayOf(Source("", true)),
            arrayOf(Source("", true))
        )
        val reactInstanceManager = RNManager.preload(this, application, moduleInfo) ?: return
        if(reactInstanceManager.hasStartedCreatingInitialContext()) {
            val reactContext = reactInstanceManager.currentReactContext
            reactRootView.startReactApplication(reactInstanceManager, "basic")
            setContentView(reactRootView)
        }
    }
}