package com.vintra.app.ui.jobs

import android.net.Uri
import com.vintra.app.domain.model.JobTag

data class CreateJobUiState(
    val companyName: String = "",
    val companyUsername: String = "",
    val companyPhotoUri: Uri? = null,
    val title: String = "",
    val description: String = "",
    val linkUrl: String = "",
    val buttonLabel: String = "Open",
    val selectedTags: Set<JobTag> = emptySet(),
    val isPublishing: Boolean = false,
    val published: Boolean = false,
    val toastMessage: String? = null
)