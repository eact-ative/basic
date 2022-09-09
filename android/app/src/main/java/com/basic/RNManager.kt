package com.basic

import android.app.Application
import android.content.Context
import com.basic.newarchitecture.MainApplicationReactNativeHost
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RNManager {

    companion object {
        private var reactInstanceManager: ReactInstanceManager? = null
        private val reactRootViewPool: HashMap<String, ReactRootView> = HashMap()
        suspend fun getReactRootView(context: Context, application: Application): ReactRootView? {
            if(reactRootViewPool["dft"] != null) {
                return reactRootViewPool["dft"]
            }
            val mNewArchitectureNativeHost: ReactNativeHost = MainApplicationReactNativeHost(application)

            val reactRootView = ReactRootView(context)
            val reactInstanceManager = mNewArchitectureNativeHost.reactInstanceManager
//            val reactInstanceManager = ReactInstanceManager.builder()
//                .setApplication(application)
//                .setJSMainModulePath("index.android")
//                .setRequireActivity(false)
//                .setJSBundleFile("assets://index.android.bundle")
//                .setJsEngineAsHermes(true)
//                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
//                .addPackage(MainReactPackage())
//                .setUseDeveloperSupport(true)
//                .build()
            reactRootView.setIsFabric(true)
            GlobalScope.launch(Dispatchers.Main) {
                reactRootView.startReactApplication(reactInstanceManager, "basic")
            }
            reactRootViewPool["dft"] = reactRootView
            return reactRootView
        }

        fun preload(context: Context, application: Application): ReactInstanceManager? {
            if(reactInstanceManager == null) {
                val mainApplicationReactNativeHost = MainApplicationReactNativeHost(application)
                reactInstanceManager = mainApplicationReactNativeHost.reactInstanceManager
//                reactInstanceManager = ReactInstanceManager.builder()
//                    .setApplication(application)
//                    .setJSBundleFile("assets://index.android.bundle")
//                    .addPackage(MainReactPackage())
//                    .setUseDeveloperSupport(BuildConfig.DEBUG)
//                    .setInitialLifecycleState(LifecycleState.RESUMED)
//                    .build()
            }
            GlobalScope.launch(Dispatchers.Main) {
                reactInstanceManager?.createReactContextInBackground()
            }
            return reactInstanceManager
        }

    }
}