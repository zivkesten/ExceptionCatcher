package com.zivkesten.test.data.mapper

import com.zivkesten.test.data.local.entities.ExceptionEntity
import com.zivkesten.test.domain.model.DomainException

internal object ExceptionsMapper {
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