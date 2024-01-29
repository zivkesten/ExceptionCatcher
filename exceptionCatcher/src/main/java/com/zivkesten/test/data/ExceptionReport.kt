package com.zivkesten.test.data

data class ExceptionReport(
    val exceptions: List<CaughtException>,
    val time: Long
)