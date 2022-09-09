package com.eactative.ua.rn

import android.app.Application
import android.content.Context
import com.eactative.ua.service.ModuleInfo
import com.facebook.react.ReactInstanceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RNManager {
    companion object {
        private val reactInstanceManagerPool: HashMap<String, ReactInstanceManager> = HashMap()
        fun preload(context: Context, application: Application, moduleInfo: ModuleInfo): ReactInstanceManager? {
            val moduleID = moduleInfo.moduleID
            if(reactInstanceManagerPool[moduleID] != null) {
                return reactInstanceManagerPool[moduleID]
            }
            val mainApplicationReactNativeHost = MainApplicationReactNativeHost(application)
            mainApplicationReactNativeHost.jsBundleFile = "assets://index.android.bundle"
            mainApplicationReactNativeHost.jsMainModuleName = "index"
            val reactInstanceManager = mainApplicationReactNativeHost.reactInstanceManager
//                reactInstanceManager = ReactInstanceManager.builder()
//                    .setApplication(application)
//                    .setJSBundleFile("assets://index.android.bundle")
//                    .addPackage(MainReactPackage())
//                    .setUseDeveloperSupport(BuildConfig.DEBUG)
//                    .setInitialLifecycleState(LifecycleState.RESUMED)
//                    .build()
            reactInstanceManagerPool[moduleID] = reactInstanceManager
            GlobalScope.launch(Dispatchers.Main) {
                reactInstanceManager?.createReactContextInBackground()
            }
            return reactInstanceManager
        }
    }
}