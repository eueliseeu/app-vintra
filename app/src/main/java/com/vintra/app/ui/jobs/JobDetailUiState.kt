package com.vintra.app.ui.jobs

import com.vintra.app.domain.model.Job

data class JobDetailUiState(
    val isLoading: Boolean = true,
    val job: Job? = null,
    val errorMessage: String? = null
)