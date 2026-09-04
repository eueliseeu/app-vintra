package com.vintra.app.ui.auth

enum class LoginScreenStep {
    FORM,
    ACCOUNT_CREATED
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
    val step: LoginScreenStep = LoginScreenStep.FORM,
    val isLoginSuccessful: Boolean = false
)