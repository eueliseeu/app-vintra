package com.vintra.app.ui.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.usecase.auth.LoginUseCase
import com.vintra.app.domain.usecase.auth.RegisterAccountResult
import com.vintra.app.domain.usecase.auth.RegisterAccountUseCase
import com.vintra.app.domain.usecase.auth.SignInWithGitHubUseCase
import com.vintra.app.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_PASSWORD_LENGTH = 6

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerAccountUseCase: RegisterAccountUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithGitHubUseCase: SignInWithGitHubUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChange(value: String) {
        _uiState.update { it.copy(identifier = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun onAccountCreatedContinue() {
        _uiState.update { it.copy(isLoginSuccessful = true) }
    }

    fun login() {
        val identifier = _uiState.value.identifier.trim()
        val password = _uiState.value.password

        if (identifier.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "Please fill in your username/e-mail and password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = loginUseCase(identifier, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
                is AuthResult.UserNotFound -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "User not found.") }
                }
                is AuthResult.InvalidCredentials -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Invalid username/e-mail or password.") }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = result.message) }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Error signing in. Please try again.") }
                }
            }
        }
    }

    fun register() {
        val email = _uiState.value.identifier.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "Please fill in email and password.") }
            return
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            _uiState.update {
                it.copy(toastMessage = "Password must be at least $MIN_PASSWORD_LENGTH characters long.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            handleRegisterResult(registerAccountUseCase(email, password))
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            handleRegisterResult(signInWithGoogleUseCase(context))
        }
    }

    fun signInWithGitHub(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            handleRegisterResult(signInWithGitHubUseCase(activity))
        }
    }

    private fun handleRegisterResult(result: RegisterAccountResult) {
        when (result) {
            is RegisterAccountResult.Success -> {
                if (result.isNewUser) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = LoginScreenStep.ACCOUNT_CREATED,
                            toastMessage = "Account created successfully!"
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
            }
            is RegisterAccountResult.EmailAlreadyInUse -> {
                _uiState.update { it.copy(isLoading = false, toastMessage = "Email already in use. Try logging in.") }
            }
            is RegisterAccountResult.WeakPassword -> {
                _uiState.update {
                    it.copy(isLoading = false, toastMessage = "Password is too weak. Use at least $MIN_PASSWORD_LENGTH characters.")
                }
            }
            is RegisterAccountResult.InvalidEmail -> {
                _uiState.update { it.copy(isLoading = false, toastMessage = "Invalid email.") }
            }
            is RegisterAccountResult.DeviceAlreadyRegistered -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        toastMessage = "This device already has an account registered. Please log in."
                    )
                }
            }
            is RegisterAccountResult.Cancelled -> {
                _uiState.update { it.copy(isLoading = false) }
            }
            is RegisterAccountResult.Error -> {
                _uiState.update { it.copy(isLoading = false, toastMessage = result.message) }
            }
        }
    }
}