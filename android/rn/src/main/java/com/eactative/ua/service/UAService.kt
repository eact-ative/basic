package com.eactative.ua.service

import com.eactative.ua.entity.AppInfo
import com.eactative.ua.entity.BaseResponse
import com.eactative.ua.entity.ModuleInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UAService {
    @GET("appinfo/{id}")
    fun getAppInfo(@Path("id") id: String): Call<BaseResponse<AppInfo>>

    @GET("moduleinfo/{id}")
    fun getModuleInfo(@Path("id") id: String): Call<BaseResponse<ModuleInfo>>
}