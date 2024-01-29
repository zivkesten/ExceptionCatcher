package com.zivkesten.similarwebtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exceptionCatcher = (application as SimilarWebApplication).exceptionCatcher



        setContent {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    Button(onClick = {
                        throw RuntimeException("Test Uncaught Exception")
                    }) {
                        Text("Test Uncaught Exception")
                    }
                    Button(onClick = {
                        try {
                            throw Exception("Test Caught Exception")
                        } catch (e: Exception) {
                            exceptionCatcher.handleException(e)
                        }
                    }) {
                        Text("Test caught Exception")
                    }
                }
            }
        }
    }
}
