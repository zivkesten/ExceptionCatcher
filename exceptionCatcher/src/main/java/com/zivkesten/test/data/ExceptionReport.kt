package com.zivkesten.test.data

data class ExceptionReport(
    val exceptions: List<DomainException>,
    val time: Long
)