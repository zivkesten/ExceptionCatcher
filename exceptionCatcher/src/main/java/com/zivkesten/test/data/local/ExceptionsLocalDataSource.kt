package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity
import kotlinx.coroutines.flow.Flow


internal interface ExceptionsLocalDataSource {
    suspend fun storeException(exception: ExceptionEntity): Long
    fun storedExceptions(): Flow<List<ExceptionEntity>>
    suspend fun deleteAllExceptions()
}
