package com.eactative.ua.rn

import android.app.Application
import android.content.Context
import android.util.Log
import com.eactative.ua.Constants
import com.eactative.ua.entity.AppInfo
import com.eactative.ua.entity.ModuleInfo
import com.eactative.ua.entity.ResponseCode
import com.eactative.ua.service.UAService
import com.facebook.react.ReactInstanceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RNManager {
    companion object {
        private val reactInstanceManagerPool: HashMap<String, ReactInstanceManager> = HashMap()
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
    }
}