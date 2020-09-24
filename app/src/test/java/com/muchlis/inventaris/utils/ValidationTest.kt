package com.muchlis.inventaris.utils

import org.junit.Test

import org.junit.Assert.*

class ValidationTest {

    @Test
    fun `ip address valid`() {
        val ipAddress = "192.168.1.1"
        assertEquals(true, Validation().isIPAddressValid(ipAddress))
    }

    @Test
    fun `angka ip address tidak valid`() {
        val ipAddress = "192.168.1.256"
        assertEquals(false, Validation().isIPAddressValid(ipAddress))
    }

    @Test
    fun `ip address tidak valid dengan format salah`() {
        val ipAddress = "192.168"
        assertEquals(false, Validation().isIPAddressValid(ipAddress))
    }

}