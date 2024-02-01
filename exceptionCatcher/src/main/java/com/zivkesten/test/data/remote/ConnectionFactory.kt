package com.zivkesten.test.data.remote

import java.net.HttpURLConnection

internal interface ConnectionFactory {
    fun createConnection(url: String): HttpURLConnection
}
