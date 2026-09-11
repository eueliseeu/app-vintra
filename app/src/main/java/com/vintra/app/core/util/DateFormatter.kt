package com.vintra.app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))

fun formatPostTimestamp(millis: Long): String =
    SimpleDateFormat("MMM d 'at' HH:mm", Locale.US).format(Date(millis))