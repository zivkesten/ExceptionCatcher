package com.zivkesten.test.data.network

interface ExceptionRepository {
    suspend fun sendExceptionReport(
        exceptionReport: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit
    )
}

