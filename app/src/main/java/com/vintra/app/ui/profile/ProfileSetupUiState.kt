package com.vintra.app.ui.profile

import com.vintra.app.domain.model.ProfileEditability

enum class UsernameCheckStatus {
    IDLE, CHECKING, AVAILABLE, TAKEN, INVALID
}

const val FIXED_NATIONALITY = "Brazil"

data class ProfileSetupUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val birthDateText: String = "",
    val nationality: String = FIXED_NATIONALITY,
    val usernameStatus: UsernameCheckStatus = UsernameCheckStatus.IDLE,
    val originalUsername: String? = null,
    val editability: ProfileEditability = ProfileEditability.Editable,
    val toastMessage: String? = null,
    val isSaved: Boolean = false
)