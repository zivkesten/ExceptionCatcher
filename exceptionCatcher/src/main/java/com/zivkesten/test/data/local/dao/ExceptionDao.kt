package com.zivkesten.test.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zivkesten.test.data.local.entities.ExceptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExceptionDao {
    @Insert
    suspend fun insertException(exceptionEntity: ExceptionEntity): Long

    @Query("SELECT * FROM ExceptionEntity")
    fun exceptionsFlow(): Flow<List<ExceptionEntity>>

    @Query("SELECT * FROM ExceptionEntity")
    fun exceptions(): List<ExceptionEntity>

    @Query("DELETE FROM ExceptionEntity")
    suspend fun deleteAllExceptions()
}
