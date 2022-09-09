package com.eactative.ua.service

data class AppInfo(
    val appID: String,
    val version: Int,
    val force: Boolean,
    val os: String,
    val entry: ModuleInfo
)
