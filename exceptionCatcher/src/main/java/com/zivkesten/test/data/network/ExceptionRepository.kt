package com.zivkesten.test.data.network

import com.zivkesten.test.data.network.model.ExceptionReport
import java.lang.Exception

interface ExceptionRepository {
    suspend fun sendExceptionReport(
        exceptionReport: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit,
        onFail: (Exception) -> Unit
    )
}

