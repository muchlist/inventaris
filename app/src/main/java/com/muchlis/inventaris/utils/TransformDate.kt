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

fun Date.toStringddMMMyyyy(): String {
    val formatEx = SimpleDateFormat("dd MMM yyyy", Locale.US)
    return formatEx.format(this)
}

fun Date.toStringJustYear(): String {
    val formatEx = SimpleDateFormat("yyyy", Locale.US)
    return formatEx.format(this)
}

fun Date.toStringInputDate(): String {
    val formatEx = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US)
    return formatEx.format(this)
}

fun String.toDate(): Date {
    val pattern = listOf("yyyy-MM-dd HH:mm:ss.SSSSSS", "yyyy-MM-dd HH:mm:ss")
    var date = Date()
    for (p in pattern){
        try {
            val format = SimpleDateFormat(p, Locale.US)
            date = format.parse(this)
            return date
        } catch (e: ParseException){
            //FAILED
        }
    }
    return date
}

fun String.fromddMMMyyyytoDate(): Date {
    var date = Date()
    val format = SimpleDateFormat("dd MMM yyyy", Locale.US)
    try {
        date = format.parse(this)
    } catch (e: ParseException) {
        //DATE FAILED HANDLE
    }
    return date
}