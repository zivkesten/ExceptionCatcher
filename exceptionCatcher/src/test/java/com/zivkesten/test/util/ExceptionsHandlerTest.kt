package com.zivkesten.test.util

import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.mock
import com.zivkesten.test.ExceptionsHandler
import com.zivkesten.test.additionalInfoMock
import com.zivkesten.test.data.local.ExceptionStore
import com.zivkesten.test.data.remote.ExceptionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class ExceptionsHandlerTest {

    private val testScope = TestScope()
    private lateinit var handler: ExceptionsHandler
    private lateinit var exceptionStore: ExceptionStore
    private lateinit var exceptionRepository: ExceptionRepository

    @Before
    fun setup() {

        exceptionStore = mock()
        exceptionRepository = mock()
        handler = ExceptionsHandler(testScope, exceptionStore, exceptionRepository)
    }

    @Test
    fun `handleException should store exception in database`() = runTest {

        val testException = RuntimeException("Test Exception")
        val additionalInfo = additionalInfoMock

        // When
        handler.handleException(testException, additionalInfo)

        // Verify the method call on the mocked DAO with a custom argument matcher
        verify(exceptionStore).storeException(argThat { entity ->
            // Check all fields except timestamp
            entity.message == testException.message &&
                    entity.deviceModel == additionalInfo.deviceModel &&
                    entity.osVersion == additionalInfo.osVersion &&
                    entity.appVersion == additionalInfo.appVersion &&
                    entity.networkType == additionalInfo.networkType &&
                    entity.locale == additionalInfo.locale &&
                    entity.batteryLevel == additionalInfo.batteryLevel &&
                    entity.memoryUsage == additionalInfo.memoryUsage &&
                    entity.stackTrace == additionalInfo.stackTrace &&
                    entity.screenOrientation == additionalInfo.screenOrientation
        })
    }
}