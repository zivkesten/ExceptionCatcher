package com.zivkesten.test.data

import com.zivkesten.test.data.local.entities.ExceptionEntity
import com.zivkesten.test.data.remote.model.ExceptionReport
import com.zivkesten.test.domain.model.DomainException
import kotlinx.coroutines.flow.Flow

internal interface ExceptionsRepository {
    fun storedExceptionsFlow(): Flow<List<DomainException>>
    suspend fun storedExceptions(): List<DomainException>

    suspend fun storeException(exception: ExceptionEntity): Long
    suspend fun sendExceptionReport(
        report: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit,
        onFail: (Exception) -> Unit
    )

    suspend fun deleteAllExceptions()
}