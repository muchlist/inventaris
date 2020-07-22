package com.muchlis.inventaris.utils

class Validation {
    fun isIPAddressValid(ipAddress: String): Boolean {
        if (ipAddress.isNotEmpty()) {
            val ipAddressList = ipAddress.split(".")
            if (ipAddressList.count() != 4) {
                return false
            }
            if (isIPUnitValid(ipAddressList[0])) {
                if (isIPUnitValid(ipAddressList[1])) {
                    if (isIPUnitValid(ipAddressList[2])) {
                        if (isIPUnitValid(ipAddressList[3])) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isIPUnitValid(ip_unit: String): Boolean {
        val ipUnitInt: Int = try {
            ip_unit.toInt()
        } catch (r: Error) {
            -1
        }
        if (ipUnitInt > 254 || ipUnitInt < 0) {
            return false
        }
        return true
    }
}