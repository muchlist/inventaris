package com.muchlis.inventaris.utils

import org.junit.Test

import org.junit.Assert.*

class TranslateMinuteToHourTest {

    @Test
    fun `diatas 60 menit`() {
        val minute = 500
        val string = TranslateMinuteToHour(minute).getStringHour()
        assertEquals("8 jam 20 menit", string)
    }

    @Test
    fun `dibawah 60 menit`() {
        val minute = 45
        val string = TranslateMinuteToHour(minute).getStringHour()
        assertEquals("45 menit", string)
    }

    @Test
    fun `0 menit`() {
        val minute = 0
        val string = TranslateMinuteToHour(minute).getStringHour()
        assertEquals("0 menit", string)
    }

    @Test
    fun `dibawah 0 menit`() {
        val minute = -70
        val string = TranslateMinuteToHour(minute).getStringHour()
        assertEquals("-70 menit", string)
    }
}