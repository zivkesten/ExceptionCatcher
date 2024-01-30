package com.zivkesten.test.data.mapper

import com.zivkesten.test.ExceptionEntity
import com.zivkesten.test.data.DomainException

object ExceptionsMapper {
    fun ExceptionEntity.toDomainException() = DomainException(
        timestamp = this.timestamp,
        message = this.message,
        deviceModel = this.deviceModel,
        osVersion = this.osVersion,
        appVersion = this.appVersion,
        networkType = this.networkType,
        locale = this.locale,
        batteryLevel = this.batteryLevel,
        memoryUsage = this.memoryUsage,
        stackTrace = this.stackTrace,
        screenOrientation = this.screenOrientation,
    )
}