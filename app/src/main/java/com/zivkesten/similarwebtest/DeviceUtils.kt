package com.zivkesten.similarwebtest

import android.os.Build

fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("google/sdk_gphone")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86"))
}