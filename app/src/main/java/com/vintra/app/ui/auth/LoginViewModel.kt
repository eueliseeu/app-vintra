package com.vintra.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.DeviceRegistrationResult
import com.vintra.app.domain.repository.DeviceRepository
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
    private val authRepository: AuthRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
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
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "Please fill in email and password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
                is AuthResult.UserNotFound -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "User not found.") }
                }
                is AuthResult.InvalidCredentials -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Invalid email or password.") }
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
        val email = _uiState.value.email.trim()
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

            val newUser = when (val result = authRepository.signUp(email, password)) {
                is AuthResult.Success -> result.user
                is AuthResult.EmailAlreadyInUse -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Email already in use. Try logging in.") }
                    return@launch
                }
                is AuthResult.WeakPassword -> {
                    _uiState.update {
                        it.copy(isLoading = false, toastMessage = "Password is too weak. Use at least $MIN_PASSWORD_LENGTH characters.")
                    }
                    return@launch
                }
                is AuthResult.InvalidCredentials -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Invalid email.") }
                    return@launch
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = result.message) }
                    return@launch
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false, toastMessage = "Error creating account. Please try again.") }
                    return@launch
                }
            }

            when (deviceRepository.registerDevice(newUser.uid)) {
                is DeviceRegistrationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = LoginScreenStep.ACCOUNT_CREATED,
                            toastMessage = "Account created successfully!"
                        )
                    }
                }

                is DeviceRegistrationResult.AlreadyRegistered -> {
                    authRepository.deleteCurrentUser()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = "This device already has an account registered. Please log in."
                        )
                    }
                }

                is DeviceRegistrationResult.Error -> {
                    authRepository.deleteCurrentUser()
                    _uiState.update {
                        it.copy(isLoading = false, toastMessage = "Error registering device. Please try again.")
                    }
                }
            }
        }
    }
}