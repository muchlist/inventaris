package com.muchlis.inventaris.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date.toStringDateForView(): String {
    val formatEx = SimpleDateFormat("dd-MMM HH:mm", Locale.US)
    return formatEx.format(this)
}

//fun Date.toStringDateForViewWithYear(): String {
//    val formatEx = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US)
//    return formatEx.format(this)
//}

fun Date.toStringJustDate(): String {
    val formatEx = SimpleDateFormat("dd MMM yyyy", Locale.US)
    return formatEx.format(this)
}

fun Date.toStringJustTime(): String {
    val formatEx = SimpleDateFormat("HH:mm", Locale.US)
    return formatEx.format(this)
}

fun Date.toStringInputDate(): String {
    val formatEx = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US)
    return formatEx.format(this)
}

fun String.toDate(): Date {
    var date = Date()
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US)
    try {
        date = format.parse(this)
    } catch (e: ParseException) {
        //DATE FAILED HANDLE
    }
    return date
}