package com.zivkesten.test.data

import com.zivkesten.test.data.local.ExceptionsLocalDataSource
import com.zivkesten.test.data.local.entities.ExceptionEntity
import com.zivkesten.test.data.mapper.ExceptionsMapper.toDomainException
import com.zivkesten.test.data.remote.ExceptionRemoteDataSource
import com.zivkesten.test.data.remote.model.ExceptionReport
import kotlinx.coroutines.flow.map
import java.lang.Exception

internal class ExceptionRepositoryImpl(
    private val localDataSource: ExceptionsLocalDataSource,
    private val remoteDataSource: ExceptionRemoteDataSource
): ExceptionsRepository {
    override fun storedExceptions() = localDataSource.storedExceptions().map {
        exceptions -> exceptions.map {
            it.toDomainException()
        }
    }
    override suspend fun storeException(exception: ExceptionEntity) = localDataSource.storeException(exception)
    override suspend fun sendExceptionReport(
        report: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit,
        onFail: (Exception) -> Unit
    ) {
        remoteDataSource.sendExceptionReport(report,remoteIpForServer,onSuccess, onFail)
    }

    override suspend fun deleteAllExceptions() = localDataSource.deleteAllExceptions()
}