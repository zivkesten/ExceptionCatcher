package com.zivkesten.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExceptionDao {
    @Insert
    suspend fun insertException(exceptionEntity: ExceptionEntity): Long

    @Query("SELECT * FROM ExceptionEntity")
    suspend fun getAllExceptions(): List<ExceptionEntity>

    @Query("DELETE FROM ExceptionEntity")
    suspend fun deleteAllExceptions()
}
