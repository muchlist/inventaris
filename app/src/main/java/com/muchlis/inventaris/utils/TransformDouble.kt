package com.muchlis.inventaris.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.toStringView(): String {
    return BigDecimal(this).setScale(1, RoundingMode.HALF_EVEN).toString()
}