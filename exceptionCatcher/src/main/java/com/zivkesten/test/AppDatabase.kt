package com.zivkesten.test

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExceptionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exceptionDao(): ExceptionDao
}
