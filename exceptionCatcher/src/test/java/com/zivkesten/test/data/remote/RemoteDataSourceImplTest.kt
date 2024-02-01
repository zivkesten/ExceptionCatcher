package com.zivkesten.test.data.remote

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.zivkesten.test.data.remote.model.ExceptionReport
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.net.HttpURLConnection

class RemoteDataSourceImplTest {

    private lateinit var repository: RemoteDataSourceImpl
    private val mockHttpURLConnection: HttpURLConnection = mock()
    private val mockOutputStream: OutputStream = mock()


    @Before
    fun setUp() {
        whenever(mockHttpURLConnection.outputStream).thenReturn(mockOutputStream)
    }

    @Test
    fun `sendExceptionReport should call onSuccess on successful response`() = runTest {
        // Arrange
        val connectionFactoryMock = object : ConnectionFactory {
            override fun createConnection(url: String) = mockHttpURLConnection
        }
        repository = RemoteDataSourceImpl(connectionFactoryMock, true)
        val testResponse = "OK"
        whenever(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_OK)
        whenever(mockHttpURLConnection.inputStream).thenReturn(ByteArrayInputStream(testResponse.toByteArray()))
        var success = false

        repository.sendExceptionReport(
            ExceptionReport(listOf(), 0), "http://localhost",
            onSuccess = { success = true },
            onFail = { success = false }
        )

        // Assert
        assertTrue(success)
    }

    @Test
    fun `sendExceptionReport should handle Exception`() = runTest {
        // Arrange
        val connectionFactoryMock: ConnectionFactory = mock()
        repository = RemoteDataSourceImpl(connectionFactoryMock, true)

        whenever(connectionFactoryMock.createConnection("http://localhost")).thenThrow(RuntimeException("Test exception"))

        // Act
        var success = false

        repository.sendExceptionReport(
            ExceptionReport(listOf(), 0), "http://localhost",
            onSuccess = { success = true },
            onFail = { success = false }
        )

        assertFalse(success)
    }

    @Test
    fun `sendExceptionReport should handle non-successful response`() = runTest {
        // Arrange
        val connectionFactoryMock: ConnectionFactory = mock()
        repository = RemoteDataSourceImpl(connectionFactoryMock, true)
        whenever(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR)

        // Act
        var success = false

        repository.sendExceptionReport(
            ExceptionReport(listOf(), 0), "http://localhost",
            onSuccess = { success = true },
            onFail = { success = false }
        )

        assertFalse(success)
    }
}
