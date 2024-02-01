package com.zivkesten.test

import android.content.Context
import android.widget.Toast
import com.zivkesten.test.data.local.ExceptionStore
import com.zivkesten.test.data.local.ExceptionsHelper
import com.zivkesten.test.data.local.entities.ExceptionEntity
import com.zivkesten.test.data.mapper.ExceptionsMapper.toDomainException
import com.zivkesten.test.data.remote.ExceptionRepository
import com.zivkesten.test.data.remote.model.ExceptionReport
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo
import com.zivkesten.test.util.AdditionalInfoFactory.additionalInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ExceptionsHandler(
    private val coroutineScope: CoroutineScope,
    private val exceptionStore: ExceptionStore,
    private val exceptionRepository: ExceptionRepository
) {
    private val TAG = ExceptionsHandler::class.java.simpleName
    private var job: Job? = null
    private var storedExceptions: StateFlow<List<ExceptionEntity>> = MutableStateFlow(emptyList())

    init {
        // We update the stored exceptions reactively, listening to changes in the data base,
        coroutineScope.launch(Dispatchers.IO) {
            storedExceptions = exceptionStore.storedExceptions().stateIn(coroutineScope)
        }
    }

    fun handleException(exception: Throwable, additionalInfo: ExceptionAdditionalInfo) {
        coroutineScope.launch(Dispatchers.IO) {
            storeException(exception, additionalInfo)
        }
    }

    private suspend fun storeException(
        exception: Throwable,
        additionalInfo: ExceptionAdditionalInfo
    ): Long = exceptionStore.storeException(ExceptionsHelper.create(exception, additionalInfo))

    fun scheduleRegularReports(context: Context, interval: Long = 1000 * 60) {
        job = coroutineScope.launch {
            while (isActive) {

                // We avoided touching the dataBase and committing an IO operation Every minute
                // by keeping an updated list that observes the dataBase for changes
                if (storedExceptions.value.isNotEmpty()) {

                    // Map the entity to a domain model
                    val domainExceptionsList = storedExceptions.value.map {
                        it.toDomainException()
                    }

                    try {
                        val report = ExceptionReport.create(exceptions = domainExceptionsList)
                        exceptionRepository.sendExceptionReport(
                            report,
                            remoteIpForServer = ExceptionCatcher.ipAddress,
                            onSuccess = {
                                println("$TAG, Report sent successfully")
                                handleSuccess(context)
                            }, onFail = {
                                println("$TAG, Error sending report ${it.message}")
                                handleError(it, context)
                            }
                        )
                    } catch (e: Exception) {
                        println("$TAG, Error sending report ${e.message}")
                        storeException(e, context.additionalInfo(e))
                    }
                }
                delay(interval)
            }
        }
    }

    private fun handleSuccess(context: Context) {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Report sent successfully", Toast.LENGTH_LONG).show()
            }
            withContext(Dispatchers.IO) {
                exceptionStore.deleteAllExceptions()
            }
        }
    }

    private fun handleError(exception: Throwable, context: Context) {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error sending report ${exception.message}", Toast.LENGTH_LONG).show()
            }
            withContext(Dispatchers.IO) {
                storeException(exception, context.additionalInfo(exception))
            }
        }
    }

    fun cancelReporting() {
        job?.cancel()
        job = null
    }
}