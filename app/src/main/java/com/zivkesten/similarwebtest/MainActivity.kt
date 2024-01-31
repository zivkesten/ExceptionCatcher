package com.zivkesten.similarwebtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zivkesten.similarwebtest.ui.theme.SimilarWebTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimilarWebTestTheme {
                MainScreen()
            }
        }
    }
}
