package com.zivkesten.test.data.remote

import com.zivkesten.test.data.remote.model.ExceptionReport
import java.lang.Exception

internal interface RemoteDataSource {
    suspend fun sendExceptionReport(
        exceptionReport: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit,
        onFail: (Exception) -> Unit
    )
}

