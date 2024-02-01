package com.zivkesten.similarwebtest

import android.app.Application
import com.zivkesten.test.ExceptionCatcher

class SimilarWebApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ExceptionCatcher.initialize(this)
    }
}