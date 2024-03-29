package com.zivkesten.test.util

import android.os.Build

fun getMemoryUsage(): Long {
    val runtime = Runtime.getRuntime()
    return runtime.totalMemory() - runtime.freeMemory() // Returns memory usage in bytes
}

fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("google/sdk_gphone")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86"))
}

