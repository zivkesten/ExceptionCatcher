package com.zivkesten.similarwebtest

import android.app.Application
import android.util.Log
import com.zivkesten.test.ExceptionCatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SimilarWebApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var exceptionCatcher: ExceptionCatcher

    override fun onCreate() {
        super.onCreate()
        exceptionCatcher = ExceptionCatcher(this, applicationScope).also {
            Log.d("Zivi", "onCreate")
            it.scheduleRegularReports()
        }
    }

}