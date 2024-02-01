package com.zivkesten.test.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zivkesten.test.data.local.dao.ExceptionDao
import com.zivkesten.test.data.local.entities.ExceptionEntity

@Database(entities = [ExceptionEntity::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun exceptionDao(): ExceptionDao
}
