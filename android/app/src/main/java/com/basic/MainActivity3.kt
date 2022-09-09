package com.basic

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.soloader.SoLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity3 : AppCompatActivity() {
    private lateinit var reactRootView: ReactRootView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val self = this
//        GlobalScope.launch(Dispatchers.Main) {
//            val reactRootView = RNManager.getReactRootView(self, application)
//            reactRootView?.setBackgroundColor(Color.RED)
//            setContentView(reactRootView)
//        }
        load()
    }

    override fun onDestroy() {
        super.onDestroy()
        findViewById<View>(android.R.id.content)
    }

    private fun load() {
        SoLoader.init(this, false)
        reactRootView = ReactRootView(this)
        val reactInstanceManager = RNManager.preload(this, application) ?: return
        if(reactInstanceManager.hasStartedCreatingInitialContext()) {
            val reactContext = reactInstanceManager.currentReactContext
            reactRootView.startReactApplication(reactInstanceManager, "basic")
            setContentView(reactRootView)
        }
    }
}