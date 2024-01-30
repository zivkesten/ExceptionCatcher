package com.zivkesten.test.data

data class DomainException(
    val timestamp: Long,
    val message: String?,
    val deviceModel: String,
    val osVersion: String,
    val appVersion: String,
    val networkType: String,
    val locale: String,
    val batteryLevel: Int,
    val memoryUsage: Long,
    val stackTrace: String,
    val screenOrientation: String,
)




