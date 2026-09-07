package com.vintra.app.core.util

fun greetingForHour(hourOfDay: Int): String = when (hourOfDay) {
    in 5..11 -> "Good Morning"
    in 12..17 -> "Good Afternoon"
    else -> "Good Evening"
}