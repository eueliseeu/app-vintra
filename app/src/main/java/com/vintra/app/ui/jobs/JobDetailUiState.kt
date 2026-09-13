package com.vintra.app.ui.jobs

import com.vintra.app.domain.model.Job

data class JobDetailUiState(
    val isLoading: Boolean = true,
    val job: Job? = null,
    val currentUid: String? = null,
    val jobDeleted: Boolean = false,
    val toastMessage: String? = null,
    val errorMessage: String? = null
) {
    val isOwner: Boolean
        get() = currentUid != null && job?.publisherUid == currentUid
}