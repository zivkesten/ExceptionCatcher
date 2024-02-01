package com.zivkesten.test.data.network

import java.net.HttpURLConnection

internal interface ConnectionFactory {
    fun createConnection(url: String): HttpURLConnection
}
