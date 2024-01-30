package com.zivkesten.test.util

import android.app.Application
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ExceptionCatcherTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        // Using Robolectric to provide an Android context
        application = RuntimeEnvironment.getApplication()
    }

    @Test
    fun testInitialize() {
        ExceptionCatcher.initialize(application)
    }

    @Test
    fun testSetExternalIpAddress() {
        val testIpAddress = "192.168.1.1"
        ExceptionCatcher.setExternalIpAddress(testIpAddress)

        // Verify that the IP address was set correctly
        assertTrue(ExceptionCatcher.ipAddress == testIpAddress)
    }
}
