package com.muchlis.inventaris.utils

class TranslateMinuteToHour(private val minute: Int){
    fun getStringHour(): String {
        if (minute > 60){
            val minuteLeft = minute%60
            val hour = minute/60
            return "$hour jam $minuteLeft menit"
        }
        return "$minute menit"
    }
}