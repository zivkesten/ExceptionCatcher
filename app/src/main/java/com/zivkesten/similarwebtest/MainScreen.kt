package com.zivkesten.similarwebtest

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zivkesten.test.util.ExceptionCatcher

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val ipAddress = remember { mutableStateOf(ExceptionCatcher.ipAddress ?: "") }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            IpAddressInput(ipAddress.value) {
                ipAddress.value = it
            }
            Spacer(modifier = Modifier.height(30.dp))
            ThrowUnCaughtException()
            ThrowCaughtException()
        }
    }
}

@Composable
private fun ThrowCaughtException() {
    Button(onClick = {
        try {
            throw Exception("Test Caught Exception")
        } catch (e: Exception) {
            ExceptionCatcher.handleException(e)
            e.printStackTrace()
        }
    }) {
        Text("Test caught Exception")
    }
}

@Composable
private fun ThrowUnCaughtException() {
    Button(onClick = {
        throw RuntimeException("Test Uncaught Exception")
    }) {
        Text("Test Uncaught Exception")
    }
}

@Composable
private fun IpAddressInput(
    ipAddress: String,
    onValueChange: (String) -> Unit
) {
    if (!ExceptionCatcher.isEmulator()) {
        Text(
            text = "YOU ARE ON A PHYSICAL DEVICE, ENTER YOUR SERVER IP HERE (it is printed when you run node server.js)",
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center
        )
        TextField(
            value = ipAddress,
            onValueChange = {
                onValueChange(it)
                ExceptionCatcher.setExternalIpAddress(it)
            }
        )
    }
}