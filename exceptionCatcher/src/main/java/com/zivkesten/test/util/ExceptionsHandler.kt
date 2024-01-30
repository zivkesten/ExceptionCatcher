package com.zivkesten.test.util

import android.content.Context
import com.zivkesten.test.data.local.ExceptionStore
import com.zivkesten.test.data.local.ExceptionsHelper
import com.zivkesten.test.data.mapper.ExceptionsMapper.toDomainException
import com.zivkesten.test.data.network.ExceptionReport
import com.zivkesten.test.data.network.ExceptionRepository
import com.zivkesten.test.domain.model.ExceptionAdditionalInfo
import com.zivkesten.test.util.ExceptionInfoFactory.additionalInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val INTERVAL: Long = 5000//1000 * 60

class ExceptionsHandler(
    private val coroutineScope: CoroutineScope,
    private val exceptionStore: ExceptionStore,
    private val exceptionRepository: ExceptionRepository
) {

    private val TAG = ExceptionsHandler::class.java.simpleName
    private var job: Job? = null

    fun handleException(exception: Throwable, additionalInfo: ExceptionAdditionalInfo) {
        job = coroutineScope.launch(Dispatchers.IO) {
            storeException(exception, additionalInfo)
        }
    }

    private suspend fun storeException(
        exception: Throwable,
        additionalInfo: ExceptionAdditionalInfo
    ): Long {
        return exceptionStore.storeException(
            ExceptionsHelper.create(exception, additionalInfo)
        )
    }


    fun scheduleRegularReports() {
        job = coroutineScope.launch {
            while (isActive) {
                val exceptionsCache = exceptionStore.getAllExceptions()

                if (exceptionsCache.isNotEmpty()) {

                    // Map the entity to a domain model
                    val domainExceptionsList = exceptionsCache.map {
                        it.toDomainException()
                    }

                    try {
                        exceptionRepository.sendExceptionReport(
                            ExceptionReport(
                                exceptions = domainExceptionsList,
                                time = System.currentTimeMillis()
                            ),
                            remoteIpForServer = ExceptionCatcher.ipAddress
                        ) {
                            println("$TAG, Report sent successfully")
                            handleSuccess()
                        }
                    } catch (e: Exception) {
                        println("$TAG, Error sending report $e")
                    }
                }

                delay(INTERVAL)
            }
        }
    }

    private fun handleSuccess() {
        coroutineScope.launch {
            exceptionStore.deleteAllExceptions()
        }
    }

    fun cancelReporting() = job?.cancel()
}