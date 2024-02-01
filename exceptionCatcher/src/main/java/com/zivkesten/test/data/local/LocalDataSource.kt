package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity
import kotlinx.coroutines.flow.Flow


internal interface LocalDataSource {
    suspend fun storeException(exception: ExceptionEntity): Long
    fun storedExceptionsFlow(): Flow<List<ExceptionEntity>>
    suspend fun storedExceptions(): List<ExceptionEntity>
    suspend fun deleteAllExceptions()
}
