package com.zivkesten.test
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.room.Room
import com.google.gson.Gson
import com.zivkesten.test.ExceptionCatcher.isEmulator
import com.zivkesten.test.ExceptionInfoFactory.additionalInfo
import com.zivkesten.test.data.ExceptionAdditionalInfo
import com.zivkesten.test.data.ExceptionReport
import com.zivkesten.test.data.mapper.ExceptionsMapper.toDomainException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private const val DATABASE_NAME = "exception-database"

class ExceptionsHandler(
    context: Context,
    private val coroutineScope: CoroutineScope
): DefaultLifecycleObserver {
    private var job: Job? = null

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, DATABASE_NAME
    ).build()

    init {
        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            handleException(exception, context)
            defaultUncaughtExceptionHandler?.uncaughtException(thread, exception)
        }
    }

    fun handleException(exception: Throwable, context: Context) {
        job = coroutineScope.launch(Dispatchers.IO) {
            val additionalInfo = exception.additionalInfo(context)
            storeException(exception, additionalInfo)
        }
    }

    private suspend fun storeException(
        exception: Throwable,
        additionalInfo: ExceptionAdditionalInfo
    ): Long = db.exceptionDao().insertException(
        ExceptionEntity(
            timestamp = System.currentTimeMillis(),
            message = exception.message,
            deviceModel = additionalInfo.deviceModel,
            osVersion = additionalInfo.osVersion,
            appVersion = additionalInfo.appVersion,
            networkType = additionalInfo.networkType,
            locale = additionalInfo.locale,
            batteryLevel = additionalInfo.batteryLevel,
            memoryUsage = additionalInfo.memoryUsage,
            stackTrace = additionalInfo.stackTrace,
            screenOrientation = additionalInfo.screenOrientation,
        )
    )

    fun scheduleRegularReports() {
        Log.d("Zivi", "scheduleRegularReports")
        job = coroutineScope.launch(Dispatchers.IO) {
            while (isActive) {
                val allExceptions = db.exceptionDao().getAllExceptions()

                // If we have any exceptions stored we continue to send them to the server
                if (allExceptions.isNotEmpty()) {

                    // Map the entity to a domain model
                    val domainExceptionsList = allExceptions.map {
                        it.toDomainException()
                    }

                    // Try to send the exceptions
                    try {
                        sendPostRequest(
                            ExceptionReport(
                                exceptions = domainExceptionsList,
                                time = System.currentTimeMillis()
                            ),
                            remoteIpForServer = ExceptionCatcher.ipAddress
                        )

                        Log.i("Zivi", "Report sent successfully")
                    } catch (e: Exception) {
                        Log.e("Zivi", "Error sending report", e)
                    }
                }

                delay(5000) // Delay for one minute
            }
        }
    }

    private suspend fun sendPostRequest(
        report: ExceptionReport,
        endpoint: URL? = null,
        remoteIpForServer: String? = null
    ) {
        try {
            val requestBody = Gson().toJson(report)
            val ipAddress = if (isEmulator()) "10.0.2.2" else remoteIpForServer
            val url = endpoint ?: URL("http://$ipAddress:9000/api/exceptions")
            (url.openConnection() as HttpURLConnection).apply {
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
                    Log.e("HTTP_ERROR", "Response Code: $responseCode")
                    null
                }

                response?.let {
                    // We have a response, we can clear the cache now.
                    Log.d("Zivi", "Success! Response: $it")
                    db.exceptionDao().deleteAllExceptions()

                }
            }
        } catch (e: IOException) {
            Log.e("Zivi", "IOException in sendPostRequest", e)
        } catch (e: Exception) {
            Log.e("Zivi", "Exception in sendPostRequest", e)
        }
    }

    fun cancelReporting() = job?.cancel()
}
