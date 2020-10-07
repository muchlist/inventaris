package com.muchlis.inventaris.utils

import android.app.Application

class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs

        var activityDashboardMustBeRefresh : Boolean = false
        var activityComputerListMustBeRefresh : Boolean = false
        var fragmentDetailComputerMustBeRefresh : Boolean = false
        var fragmentHistoryComputerMustBeRefresh : Boolean = false

        var activityStockListMustBeRefresh : Boolean = false
        var fragmentDetailStockMustBeRefresh : Boolean = false

        var activityCctvListMustBeRefresh : Boolean = false
        var fragmentDetailCctvMustBeRefresh : Boolean = false

        var activityHHListMustBeRefresh : Boolean = false
        var fragmentDetailHHMustBeRefresh : Boolean = false

        var activityAppsHistoryListMustBeRefresh : Boolean = false
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}