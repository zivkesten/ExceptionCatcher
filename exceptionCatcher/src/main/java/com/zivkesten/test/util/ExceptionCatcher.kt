package com.zivkesten.test.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.room.Room
import com.zivkesten.test.data.local.AppDatabase
import com.zivkesten.test.data.local.ExceptionStoreImpl
import com.zivkesten.test.data.remote.ConnectionFactory
import com.zivkesten.test.data.remote.ExceptionRepositoryImpl
import com.zivkesten.test.util.AdditionalInfoFactory.additionalInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.net.HttpURLConnection
import java.net.URL

private const val DATABASE_NAME = "exception-database"
private const val INTERVAL: Long = 1000 * 60

/**
 * ExceptionCatcherInitializer is a utility object designed to initialize the ExceptionCatcher
 * in an Android application. It registers activity lifecycle callbacks to start and stop
 * exception reporting based on the app's foreground and background state.
 *
 * Usage:
 * To use ExceptionCatcher in your Android application, call ExceptionCatcherInitializer.initialize(this)
 * within your Application class's onCreate method. This setup will automatically handle the
 * lifecycle of exception reporting, starting when the app enters the foreground and stopping
 * when the app is no longer active (i.e., in the background).
 *
 * Note:
 * This initializer assumes that your application uses a single instance of the Application class
 * and that it manages the lifecycle of your activities correctly. If your app's architecture
 * differs significantly from this (e.g., using multiple processes), additional integration steps
 * might be required.
 */
object ExceptionCatcher {
    private var exceptionsHandler: ExceptionsHandler? = null

    var ipAddress: String? = null
    private lateinit var application: Application
    private lateinit var preferences: SharedPreferences
    fun initialize(application: Application) {
        this.application = application
        preferences = application.applicationContext.getSharedPreferences("ip", Context.MODE_PRIVATE)
        setExternalIpAddress(preferences.getString("ip", "") ?: "")
        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            handleException(exception)
            defaultUncaughtExceptionHandler?.uncaughtException(thread, exception)
        }

        val exceptionStore = ExceptionStoreImpl(
            Room.databaseBuilder(
                application.applicationContext,
                AppDatabase::class.java, DATABASE_NAME
            ).build()
        )

        val exceptionRepository = ExceptionRepositoryImpl(
            object : ConnectionFactory{
                override fun createConnection(url: String) = (URL(url).openConnection() as HttpURLConnection)
            })

        exceptionsHandler = ExceptionsHandler(
            CoroutineScope(SupervisorJob() + Dispatchers.IO),
            exceptionStore,
            exceptionRepository
        )

        // We register for life cycle events to determine when to start and stop the scheduled reporting
        // This could present issues in edge cases like multi-window or activities in other processes
        // but for now this is good enough
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                private var activeActivities = 0
                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activeActivities++ == 0) {
                        // App enters the foreground
                        exceptionsHandler?.scheduleRegularReports(
                            application.applicationContext,
                            INTERVAL
                        )
                    }
                }

                override fun onActivityDestroyed(activity: Activity) {
                    if (!activity.isChangingConfigurations) {
                        if (--activeActivities == 0) {
                            // No more activities in the stack,
                            // this means the task is done and we can stop sending exceptions to the server
                            exceptionsHandler?.cancelReporting()
                        }
                    }
                }
            }
        )
    }

    fun isInitialized() = exceptionsHandler != null

    fun handleException(exception: Throwable) {
        exceptionsHandler?.handleException(
            exception,
            exception.additionalInfo(application.applicationContext)
        )
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))
    }

    fun setExternalIpAddress(ipAddress: String) {
        this.ipAddress = if (isEmulator())  "" else ipAddress
        preferences.edit().putString("ip", this.ipAddress).apply()
    }
}