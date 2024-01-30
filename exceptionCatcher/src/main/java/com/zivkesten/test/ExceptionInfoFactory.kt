package com.zivkesten.test

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.util.Log
import com.zivkesten.test.data.ExceptionAdditionalInfo
import java.util.Locale

private const val LANDSCAPE = "Landscape"
private const val PORTRAIT = "Portrait"
private const val NO_MESSAGE_PROVIDED = "NO Message provided"
private const val UNKNOWN = "Unknown"

object ExceptionInfoFactory {

    fun Throwable.additionalInfo(context: Context): ExceptionAdditionalInfo {
        val deviceModel = android.os.Build.MODEL
        val osVersion = android.os.Build.VERSION.RELEASE
        val appVersion = getAppVersion(context)
        val networkType = getNetworkType(context)
        val locale = Locale.getDefault().toString()
        val batteryLevel = getBatteryLevel(context)
        val memoryUsage = getMemoryUsage()
        val stackTrace = Log.getStackTraceString(this)
        val errorMessage = message ?: NO_MESSAGE_PROVIDED
        val screenOrientation = getScreenOrientation(context)

        return ExceptionAdditionalInfo(
            deviceModel,
            osVersion,
            appVersion,
            networkType,
            locale,
            batteryLevel,
            memoryUsage,
            stackTrace,
            errorMessage,
            screenOrientation,
        )
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            UNKNOWN
        }
    }

    private fun getNetworkType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return when (activeNetwork?.type) {
            ConnectivityManager.TYPE_WIFI -> "WiFi"
            ConnectivityManager.TYPE_MOBILE -> "Mobile"
            else -> "None"
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level >= 0 && scale > 0) {
            (level / scale.toFloat() * 100).toInt()
        } else {
            -1 // Battery level not available
        }
    }

    private fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory() // Returns memory usage in bytes
    }

    private fun getScreenOrientation(context: Context): String {
        val orientation = context.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE else PORTRAIT
    }
}
