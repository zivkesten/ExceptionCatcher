package com.zivkesten.test.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zivkesten.test.data.local.entities.ExceptionEntity

@Dao
internal interface ExceptionDao {
    @Insert
    suspend fun insertException(exceptionEntity: ExceptionEntity): Long

    @Query("SELECT * FROM ExceptionEntity")
    suspend fun getAllExceptions(): List<ExceptionEntity>

    @Query("DELETE FROM ExceptionEntity")
    suspend fun deleteAllExceptions()
}
