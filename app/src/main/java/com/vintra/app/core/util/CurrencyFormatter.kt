package com.vintra.app.core.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private val BRAZIL_LOCALE = Locale("pt", "BR")

data class FormattedCurrency(val integerPart: String, val centsPart: String)

fun formatCurrencyParts(amountCents: Long): FormattedCurrency {
    val isNegative = amountCents < 0
    val absoluteCents = abs(amountCents)
    val wholeAmount = absoluteCents / 100
    val cents = absoluteCents % 100

    val groupedWhole = NumberFormat.getIntegerInstance(BRAZIL_LOCALE).format(wholeAmount)
    val sign = if (isNegative) "-" else ""

    return FormattedCurrency(
        integerPart = "${sign}R$$groupedWhole",
        centsPart = cents.toString().padStart(2, '0')
    )
}