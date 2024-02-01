package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LocalDataSourceImpl(private val db: AppDatabase): LocalDataSource {
    override suspend fun storeException(exception: ExceptionEntity): Long {
        return withContext(Dispatchers.IO) {
            db.exceptionDao().insertException(exception)
        }
    }
    override suspend fun storedExceptions() = withContext(Dispatchers.IO) {
        db.exceptionDao().exceptions()
    }
    override suspend fun deleteAllExceptions() = withContext(Dispatchers.IO) {
        db.exceptionDao().deleteAllExceptions()
    }
    override fun storedExceptionsFlow() = db.exceptionDao().exceptionsFlow()
}
