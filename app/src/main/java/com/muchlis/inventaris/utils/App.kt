package com.muchlis.inventaris.utils

import android.app.Application

class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs

        var activityDashboardMustBeRefresh : Boolean = false
        var activityComputerListMustBeRefresh : Boolean = false
        var fragmentDetailComputerMustBeRefresh : Boolean = false
        var fragmentHistoryComputerMustBeRefresh : Boolean = false
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}