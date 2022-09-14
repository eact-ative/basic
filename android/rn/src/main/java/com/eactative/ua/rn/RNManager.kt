package com.eactative.ua.rn

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import com.eactative.resource.ResourceManager
import com.eactative.ua.Constants
import com.eactative.ua.entity.AppInfo
import com.eactative.ua.entity.ModuleInfo
import com.eactative.ua.entity.ResponseCode
import com.eactative.ua.service.UAService
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactContext
import com.facebook.react.views.text.ReactFontManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class RNManager {
    companion object {
        private val reactInstanceManagerPool: HashMap<String, ReactInstanceManager> = HashMap()
        fun boot(context: Context, application: Application, moduleInfo: ModuleInfo) {
            val moduleID = moduleInfo.moduleID
            if(reactInstanceManagerPool[moduleID] != null) {
                return
            }
            var mainApplicationReactNativeHost = MainApplicationReactNativeHost(application)
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
            Log.e(Constants.TAG_UA_RN, reactInstanceManager.jsExecutorName)
            GlobalScope.launch(Dispatchers.Main) {
                reactInstanceManager?.createReactContextInBackground()
            }
            return
        }

        fun preload(context: Context, application: Application, moduleInfo: ModuleInfo): ReactInstanceManager? {
            val moduleID = moduleInfo.moduleID
            if(reactInstanceManagerPool[moduleID] != null) {
                return reactInstanceManagerPool[moduleID]
            }
            var mainApplicationReactNativeHost = MainApplicationReactNativeHost(application)
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

        suspend fun pre(context: Context, application: Application, moduleInfo: ModuleInfo): ReactInstanceManager? {
            val moduleID = moduleInfo.moduleID
            if(reactInstanceManagerPool[moduleID] != null) {
                return reactInstanceManagerPool[moduleID]
            }
            if(moduleInfo.script.isEmpty()) {
                Log.e(Constants.TAG_UA_RN, "module info script is empty: $moduleInfo")
                return null
            }
            val baseScript = moduleInfo.script[0]
            if(baseScript.src == "") {
                Log.e(Constants.TAG_UA_RN, "base script source is empty: $moduleInfo")
                return null
            }
            val baseScriptSrc = ResourceManager.getResource(context, baseScript.src) ?: return null
            var mainApplicationReactNativeHost = MainApplicationReactNativeHost(application)
            mainApplicationReactNativeHost.jsBundleFile = baseScriptSrc
            mainApplicationReactNativeHost.jsMainModuleName = "index"
            val reactInstanceManager = mainApplicationReactNativeHost.reactInstanceManager

            reactInstanceManager.addReactInstanceEventListener(object: ReactInstanceEventListener {
                override fun onReactContextInitialized(context: ReactContext?) {
                    if (context == null) {
                        return
                    }
                    moduleInfo.script.forEachIndexed{i, script ->
                        if(i == 0) {
                            return
                        }
                        if(script.src == "") {
                            Log.e(Constants.TAG_UA_RN, "module info script source is empty: $moduleInfo")
                            return
                        }
                        context.catalystInstance.loadScriptFromFile(script.src, script.src, false)
                    }
                }
            })
            reactInstanceManagerPool[moduleID] = reactInstanceManager
            GlobalScope.launch(Dispatchers.Main) {
                reactInstanceManager?.createReactContextInBackground()
            }
            return reactInstanceManager
        }

        fun getAppInfo(id: String): AppInfo? {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.20.0.18:3000") // todo: read from env
                .build()
            val service = retrofit.create(UAService::class.java)
            val res = service.getAppInfo(id).execute()
            if(res.isSuccessful) {
                val body = res.body()
                if(body == null) {
                    Log.e(Constants.TAG_UA, "get app info fail")
                    return null
                }
                return when (body.code) {
                    ResponseCode.SUCCESS -> body.data
                    else -> null
                }
            }
            return null
        }

        suspend fun loadFont(context: Context, moduleInfo: ModuleInfo) {
            if (moduleInfo.ttf.isEmpty()) {
                return
            }
            if(moduleInfo.ttf[0] == null) {
                return
            }
            val ttf = ResourceManager.getResource(context, moduleInfo.ttf[0].src) ?: return
            val reactFontManager = ReactFontManager.getInstance()
            val typeface = Typeface.createFromFile(File(ttf))
            reactFontManager.setTypeface("iconfont", 0, typeface)
        }
    }
}