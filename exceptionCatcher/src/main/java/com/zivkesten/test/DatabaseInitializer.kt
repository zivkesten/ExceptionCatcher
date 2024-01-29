package com.zivkesten.test

import android.content.Context
import androidx.room.Room
import java.lang.ref.WeakReference

class DatabaseInitializer private constructor(private val context: Context) {

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "my-database-name").build()
    }

    companion object {
        private var instanceRef: WeakReference<DatabaseInitializer>? = null

        fun getInstance(context: Context): DatabaseInitializer {
            var instance = instanceRef?.get()
            if (instance == null) {
                instance = DatabaseInitializer(context)
                instanceRef = WeakReference(instance)
            }
            return instance
        }
    }

    fun getExceptionDao(): ExceptionDao {
        return appDatabase.exceptionDao()
    }

    // Other methods to access the database or DAOs as needed

}
