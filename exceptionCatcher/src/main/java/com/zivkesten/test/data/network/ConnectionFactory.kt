package com.zivkesten.test.data.network

import java.net.HttpURLConnection

interface ConnectionFactory {
    fun createConnection(url: String): HttpURLConnection
}
