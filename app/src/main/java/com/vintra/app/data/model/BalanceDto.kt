package com.vintra.app.data.model

data class BalanceDto @JvmOverloads constructor(
    val amountCents: Long = 0L,
    val updatedAt: Long = 0L
)