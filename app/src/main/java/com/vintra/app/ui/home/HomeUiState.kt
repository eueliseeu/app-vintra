package com.vintra.app.ui.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val firstName: String = "",
    val amountCents: Long = 0L
)