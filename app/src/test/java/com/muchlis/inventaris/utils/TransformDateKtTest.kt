package com.muchlis.inventaris.utils

import org.junit.Test

import org.junit.Assert.*

class TransformDateKtTest {

    @Test
    fun `from 01 Dec 2019 to string request`() {
        val stringDate = "01 Dec 2019"
        val inputDate = stringDate.fromddMMMyyyytoDate().toStringInputDate()
        assertEquals("2019-12-01 00:00:00.000000", inputDate)
    }

    @Test
    fun `from server to date to ddMMMyyy`() {
        val stringDateFromServer = "2015-10-10 00:00:00.000000"
        val ddMMMyyyy = stringDateFromServer.toDate()?.toStringddMMMyyyy()
        assertEquals("10 Oct 2015", ddMMMyyyy)
    }

    @Test
    fun `from server to date`() {
        val stringDateFromServer = "2015-10-10 00:00:00"
        val stringDateFromServer2 = "2015-10-10 00:00:00.000000"
        val date = stringDateFromServer.toDate().toString()
        val date2 = stringDateFromServer2.toDate().toString()

        assertEquals("Sat Oct 10 00:00:00 SGT 2015", date)
        assertEquals("Sat Oct 10 00:00:00 SGT 2015", date2)
    }
}