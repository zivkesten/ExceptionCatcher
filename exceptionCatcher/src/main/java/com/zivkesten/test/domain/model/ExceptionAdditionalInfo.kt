package com.zivkesten.test.domain.model

data class ExceptionAdditionalInfo(
    val deviceModel: String,
    val osVersion: String,
    val appVersion: String,
    val networkType: String,
    val locale: String,
    val batteryLevel: Int,
    val memoryUsage: Long,
    val stackTrace: String,
    val errorMessage: String,
    val screenOrientation: String,
)
