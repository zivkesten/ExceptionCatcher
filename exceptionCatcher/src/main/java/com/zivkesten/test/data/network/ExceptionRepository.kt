package com.zivkesten.test.data.network

import com.zivkesten.test.data.network.model.ExceptionReport

interface ExceptionRepository {
    suspend fun sendExceptionReport(
        exceptionReport: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit
    )
}

