package com.zivkesten.test.data.network

import com.google.gson.Gson
import com.zivkesten.test.util.ExceptionCatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ExceptionRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val isEmulator: Boolean? = null
): ExceptionRepository {
    private val TAG = ExceptionRepository::class.java.simpleName

    override suspend fun sendExceptionReport(
        exceptionReport: ExceptionReport,
        remoteIpForServer: String?,
        onSuccess: () -> Unit,
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val requestBody = Gson().toJson(exceptionReport)
            val local = isEmulator ?: ExceptionCatcher.isEmulator()
            val ipAddress = if (local) LOCAL_HOST_IP else remoteIpForServer
            val url = "http://$ipAddress:$PORT/api/exceptions"

            connectionFactory.createConnection(url).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Charset", "utf-8")
                setRequestProperty("Content-Length", requestBody.toByteArray().size.toString())

                outputStream.use { os ->
                    val input = requestBody.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                    os.flush()
                }

                // Read and handle the response
                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream.bufferedReader().use { it.readText() }  // Read response
                } else {
                    println("$TAG, HTTP_ERROR, Response Code: $responseCode")
                    null
                }

                response?.let {
                    // We have a response, we can clear the cache now.
                    println("$TAG, Success! Response: $it")
                    onSuccess()
                }
            }
        } catch (e: IOException) {
            println("$TAG, IOException during sending report -> $e")
        } catch (e: Exception) {
            println("$TAG, Error sending report -> $e")
        }
    }

    companion object {
        private const val LOCAL_HOST_IP = "10.0.2.2"
        private const val PORT = 9000
    }
}