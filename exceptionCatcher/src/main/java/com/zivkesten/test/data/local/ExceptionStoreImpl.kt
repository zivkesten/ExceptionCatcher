package com.zivkesten.test.data.local

import com.zivkesten.test.data.local.entities.ExceptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExceptionStoreImpl(private val db: AppDatabase): ExceptionStore {
    override suspend fun storeException(exception: ExceptionEntity): Long {
        return withContext(Dispatchers.IO) {
            db.exceptionDao().insertException(exception)
        }
    }

    override suspend fun getAllExceptions() = withContext(Dispatchers.IO) {
        db.exceptionDao().getAllExceptions()
    }

    override suspend fun deleteAllExceptions() = withContext(Dispatchers.IO) {
        db.exceptionDao().deleteAllExceptions()
    }
}
