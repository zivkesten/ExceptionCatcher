package com.zivkesten.test
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.WorkManager
import com.google.gson.Gson
import com.zivkesten.test.data.CaughtException
import com.zivkesten.test.data.ExceptionReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ExceptionCatcher(private val context: Context, private val coroutineScope: CoroutineScope) {

    private val TAG = "ExceptionCatcher"

    private val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "exception-database"
    ).build()

    init {
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            // TODO: Change to throwable
            handleException(Exception(exception.message))
            exceptionHandler?.uncaughtException(thread, exception)
        }
    }

    fun handleException(exception: Exception) {
        // Store exception details in local storage for later reporting using Room
        coroutineScope.launch(Dispatchers.IO) {
            val long = db.exceptionDao().insertException(
                ExceptionEntity(
                    timestamp = System.currentTimeMillis(),
                    exception = exception.message ?: "ASJSH"
                )
            )
            Log.d("Zivi", "insertException ${exception.message} succsess: $long")

        }
    }

    fun scheduleRegularReports() {
        Log.d("Zivi", "scheduleRegularReports")
        coroutineScope.launch(Dispatchers.IO) {
            while (isActive) {
                val exceptionsCache = db.exceptionDao().getAllExceptions().map {
                    CaughtException(
                        exception = it.exception,
                        timeStamp = it.timestamp
                    )
                }
                Log.d("Zivi", "exceptionsCache $exceptionsCache")

                try {
                    sendPostRequest(
                        ExceptionReport(
                                exceptions = exceptionsCache,
                                time = System.currentTimeMillis()
                        )
                    )

                    Log.d("Zivi", "Report sent successfully")
                } catch (e: Exception) {
                    Log.e("Zivi", "Error sending report", e)
                }

                delay(5000) // Delay for one minute
            }
        }
    }

    private suspend fun sendPostRequest(report: ExceptionReport) {
        try {
            val gson = Gson()
            val requestBody = gson.toJson(report)

            Log.w("Zivi", "RequestBody $requestBody")
            val url = URL("http://10.0.2.2:9000/api/exceptions")
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

                // Check if response is not null and then execute action
                response?.let {
                    // Execute your action here
                    Log.d("Zivi", "Success! Response: $it")
                    db.exceptionDao().deleteAllExceptions()
                    // For example, you can parse the response and act based on its content
                }
            }
        } catch (e: IOException) {
            Log.e("Zivi", "IOException in sendPostRequest", e)
        } catch (e: Exception) {
            Log.e("Zivi", "Exception in sendPostRequest", e)
        }
    }

    fun cancelSceduleing() {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG);
    }
}
