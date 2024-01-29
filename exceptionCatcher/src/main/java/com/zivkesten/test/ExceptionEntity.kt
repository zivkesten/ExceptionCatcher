package com.zivkesten.test

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Exception

@Entity
data class ExceptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val exception: String
)