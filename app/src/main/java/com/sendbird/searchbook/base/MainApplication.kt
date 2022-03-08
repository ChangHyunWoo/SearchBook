package com.sendbird.searchbook.base

import android.app.Application
import com.sendbird.searchbook.di.allModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(allModule) }
    }
}