package com.zivkesten.similarwebtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zivkesten.similarwebtest.ui.theme.SimilarWebTestTheme
import com.zivkesten.test.util.ExceptionCatcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimilarWebTestTheme {
                MainScreen()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun MainScreen() {
        val context = LocalContext.current
        val ipAddress by remember { mutableStateOf("") }
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                IpAddressInput(ipAddress)
                Spacer(modifier = Modifier.height(30.dp))
                ThrowUnCaughtException()
                ThrowCaughtException(context)
            }
        }
    }

    @Composable
    private fun ThrowCaughtException(context: Context) {
        Button(onClick = {
            try {
                throw Exception("Test Caught Exception")
            } catch (e: Exception) {
                ExceptionCatcher.exceptionsHandler.handleException(e, context)
                Log.d("Zivi", "handleException")
                e.printStackTrace()
            }
        }) {
            Text("Test caught Exception")
        }
    }

    @Composable
    private fun ThrowUnCaughtException() {
        Button(onClick = {
            Log.d("Zivi", "Click")
            throw RuntimeException("Test Uncaught Exception")
        }) {
            Text("Test Uncaught Exception")
        }
    }

    @Composable
    private fun IpAddressInput(ipAddress: String) {
        var ipAddress1 = ipAddress
        if (!ExceptionCatcher.isEmulator()) {
            Text(
                text = "YOU ARE ON A PHYSICAL DEVICE, ENTER YOUR SERVER IP HERE (it is printed when you run node server.js)",
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Center
            )
            TextField(
                value = ipAddress1,
                onValueChange = {
                    ipAddress1 = it
                    ExceptionCatcher.setExternalIpAddress(it)
                }
            )
        }
    }
}
