package com.zivkesten.test.data.network

import java.net.HttpURLConnection
import java.net.URL

interface ConnectionFactory {
    fun createConnection(url: String): HttpURLConnection

}
