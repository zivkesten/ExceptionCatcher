package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo

object ExceptionsHelper {
    fun create(exception: Throwable, additionalInfo: ExceptionAdditionalInfo, timeStamp: Long? = null): ExceptionEntity {
        return ExceptionEntity(
            timestamp = timeStamp ?: System.currentTimeMillis(),
            message = exception.message,
            deviceModel = additionalInfo.deviceModel,
            osVersion = additionalInfo.osVersion,
            appVersion = additionalInfo.appVersion,
            networkType = additionalInfo.networkType,
            locale = additionalInfo.locale,
            batteryLevel = additionalInfo.batteryLevel,
            memoryUsage = additionalInfo.memoryUsage,
            stackTrace = additionalInfo.stackTrace,
            screenOrientation = additionalInfo.screenOrientation,
        )
    }
}