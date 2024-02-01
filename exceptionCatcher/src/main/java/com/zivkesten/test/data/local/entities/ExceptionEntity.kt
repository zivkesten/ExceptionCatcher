package com.zivkesten.test.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class ExceptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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