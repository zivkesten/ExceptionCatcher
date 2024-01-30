package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity


interface ExceptionStore {
    suspend fun storeException(exception: ExceptionEntity): Long
    suspend fun getAllExceptions(): List<ExceptionEntity>
    suspend fun deleteAllExceptions()
}
