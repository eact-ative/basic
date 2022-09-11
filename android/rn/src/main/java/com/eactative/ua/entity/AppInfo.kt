package com.eactative.ua.entity

data class AppInfo(
    var appID: String,
    var version: Int,
    var force: Boolean,
    var os: String,
    var entry: ModuleInfo
)
