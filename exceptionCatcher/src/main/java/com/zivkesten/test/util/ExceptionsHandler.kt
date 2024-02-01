package com.zivkesten.test.util

import android.content.Context
import com.zivkesten.test.data.local.ExceptionStore
import com.zivkesten.test.data.local.ExceptionsHelper
import com.zivkesten.test.data.mapper.ExceptionsMapper.toDomainException
import com.zivkesten.test.data.network.ExceptionRepository
import com.zivkesten.test.data.network.model.ExceptionReport
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo
import com.zivkesten.test.util.AdditionalInfoFactory.additionalInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class ExceptionsHandler(
    private val coroutineScope: CoroutineScope,
    private val exceptionStore: ExceptionStore,
    private val exceptionRepository: ExceptionRepository
) {

    private val TAG = ExceptionsHandler::class.java.simpleName
    private var job: Job? = null

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
                val exceptionsCache = exceptionStore.getAllExceptions()

                if (exceptionsCache.isNotEmpty()) {

                    // Map the entity to a domain model
                    val domainExceptionsList = exceptionsCache.map {
                        it.toDomainException()
                    }

                    try {
                        val report = ExceptionReport.create(exceptions = domainExceptionsList)
                        exceptionRepository.sendExceptionReport(
                            report,
                            remoteIpForServer = ExceptionCatcher.ipAddress,
                            onSuccess = {
                                println("$TAG, Report sent successfully")
                                handleSuccess()
                            }, onFail = {
                                println("$TAG, Error sending report ${it.message}")
                                handleError(it, context)
                            }
                        )
                    } catch (e: Exception) {
                        println("$TAG, Error sending report ${e.message}")
                        storeException(e, e.additionalInfo(context))
                    }
                }
                delay(interval)
            }
        }
    }

    private fun handleSuccess() {
        coroutineScope.launch {
            exceptionStore.deleteAllExceptions()
        }
    }

    private fun handleError(exception: Throwable, context: Context) {
        coroutineScope.launch {
            storeException(exception, exception.additionalInfo(context))
        }
    }

    fun cancelReporting() {
        job?.cancel()
        job = null
    }
}
