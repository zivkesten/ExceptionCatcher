package com.zivkesten.test.data.network.model

import com.zivkesten.test.domain.model.DomainException

data class ExceptionReport(
    val exceptions: List<DomainException>,
    val time: Long
) {
    companion object {
        fun create(exceptions: List<DomainException>) =
            ExceptionReport(exceptions, System.currentTimeMillis())
    }
}