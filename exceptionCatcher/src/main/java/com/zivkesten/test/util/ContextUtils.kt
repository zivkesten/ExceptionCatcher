package com.zivkesten.test.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.BatteryManager

private const val UNKNOWN = "Unknown"
private const val LANDSCAPE = "Landscape"
private const val PORTRAIT = "Portrait"

fun Context.appVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        UNKNOWN
    }
}

fun Context.screenOrientation(): String {
    val orientation = resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE else PORTRAIT
}

fun Context.networkType(): String {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return when (activeNetwork?.type) {
        ConnectivityManager.TYPE_WIFI -> "WiFi"
        ConnectivityManager.TYPE_MOBILE -> "Mobile"
        else -> "None"
    }
}

fun Context.batteryLevel(): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        registerReceiver(null, ifilter)
    }
    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

    return if (level >= 0 && scale > 0) {
        (level / scale.toFloat() * 100).toInt()
    } else {
        -1 // Battery level not available
    }
}