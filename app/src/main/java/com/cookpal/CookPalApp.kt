package com.cookpal

import android.app.Application
import com.cookpal.di.AppContainer

class CookPalApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
