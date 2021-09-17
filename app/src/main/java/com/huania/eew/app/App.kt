package com.huania.eew.app

import android.app.Application

lateinit var mApp: Application
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mApp = this
    }
}