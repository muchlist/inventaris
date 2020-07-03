package com.muchlis.inventaris.utils

import android.view.View

fun View.invisible() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.disable() {
    alpha = .5f
    isClickable = false
    isLongClickable = false
}

fun View.enable() {
    alpha = 1f
    isClickable = true
    isLongClickable = true
}