package com.muchlis.inventaris.utils

import kotlin.math.roundToInt

fun Double.toStringView(): String {
    return this.roundToInt().toString()
    //return BigDecimal(this).setScale(1, RoundingMode.HALF_EVEN).toString()
}