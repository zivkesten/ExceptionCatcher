package com.zivkesten.test.util

fun getMemoryUsage(): Long {
    val runtime = Runtime.getRuntime()
    return runtime.totalMemory() - runtime.freeMemory() // Returns memory usage in bytes
}

