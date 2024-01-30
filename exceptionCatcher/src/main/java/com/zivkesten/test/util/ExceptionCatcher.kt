package com.zivkesten.test.util

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

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
    lateinit var exceptionsHandler: ExceptionsHandler
    var ipAddress: String? = null
    fun initialize(application: Application) {
        exceptionsHandler =
            ExceptionsHandler(application, CoroutineScope(SupervisorJob() + Dispatchers.IO))

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
                        exceptionsHandler.scheduleRegularReports()
                    }
                }

                override fun onActivityDestroyed(activity: Activity) {
                    if (!activity.isChangingConfigurations) {
                        if (--activeActivities == 0) {
                            // No more activities in the stack,
                            // this means the task is done and we can stop sending exceptions to the server
                            exceptionsHandler.cancelReporting()
                        }
                    }
                }
            }
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
        this.ipAddress = ipAddress
    }
}