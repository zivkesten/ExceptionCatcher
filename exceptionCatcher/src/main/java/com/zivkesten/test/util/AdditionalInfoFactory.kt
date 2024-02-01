package com.zivkesten.test.util

import android.content.Context
import android.util.Log
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo
import java.util.Locale


private const val NO_MESSAGE_PROVIDED = "NO Message provided"

internal object AdditionalInfoFactory {
    fun Context.additionalInfo(throwable: Throwable): ExceptionAdditionalInfo {
        val deviceModel = android.os.Build.MODEL
        val osVersion = android.os.Build.VERSION.RELEASE
        val appVersion = appVersion()
        val networkType = networkType()
        val batteryLevel = batteryLevel()
        val locale = Locale.getDefault().toString()
        val memoryUsage = getMemoryUsage()
        val stackTrace = Log.getStackTraceString(throwable)
        val errorMessage = throwable.message ?: NO_MESSAGE_PROVIDED
        val screenOrientation = screenOrientation()

        return ExceptionAdditionalInfo(
            deviceModel,
            osVersion,
            appVersion,
            networkType,
            locale,
            batteryLevel,
            memoryUsage,
            stackTrace,
            errorMessage,
            screenOrientation,
        )
    }
}