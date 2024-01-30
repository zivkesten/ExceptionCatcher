package com.zivkesten.test.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExceptionStoreImplTest {

    private lateinit var db: AppDatabase
    private lateinit var exceptionStore: ExceptionStoreImpl

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        exceptionStore = ExceptionStoreImpl(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun storeException_savesDataCorrectly() = runBlocking {
        populateExceptions(1)

        val storedException = db.exceptionDao().getAllExceptions().firstOrNull()
        assertEquals("Test Exception", storedException?.message)
    }

    @Test
    fun getAllExceptions_retrievesDataCorrectly() = runBlocking {
        val exceptionsCount = 10
        populateExceptions(exceptionsCount)

        // Retrieve Exceptions
        val allExceptions = exceptionStore.getAllExceptions()

        // Assert all exceptions stored
        assertTrue(allExceptions.size == exceptionsCount)
    }

    @Test
    fun deleteAllExceptions_clearsDataCorrectly() = runBlocking {
        populateExceptions(10)

        exceptionStore.deleteAllExceptions()

        val allExceptions = exceptionStore.getAllExceptions()

        assertEquals(0, allExceptions.size)
    }

    private suspend fun populateExceptions(exceptionsCount: Int) {
        val testException = RuntimeException("Test Exception")
        val additionalInfo = ExceptionAdditionalInfo.empty()

        // Add 10 exceptions
        repeat(exceptionsCount) {
            exceptionStore.storeException(ExceptionsHelper.create(testException, additionalInfo))
        }
    }
}
