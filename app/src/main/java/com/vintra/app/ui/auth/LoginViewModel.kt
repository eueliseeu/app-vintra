package com.vintra.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
            _uiState.update { it.copy(toastMessage = "Preencha e-mail e senha.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            authRepository.login(email, password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, toastMessage = throwable.toFriendlyLoginMessage())
                    }
                }
        }
    }

    fun register() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "Preencha e-mail e senha.") }
            return
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            _uiState.update {
                it.copy(toastMessage = "A senha precisa ter pelo menos $MIN_PASSWORD_LENGTH caracteres.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val newUser = authRepository.signUp(email, password).getOrElse { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, toastMessage = throwable.toFriendlyRegisterMessage())
                }
                return@launch
            }

            when (val result = deviceRepository.registerDevice(newUser.uid)) {
                is DeviceRegistrationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = LoginScreenStep.ACCOUNT_CREATED,
                            toastMessage = "Conta criada com sucesso!"
                        )
                    }
                }

                is DeviceRegistrationResult.AlreadyRegistered -> {
                    authRepository.deleteCurrentUser()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = "Este aparelho já possui uma conta cadastrada. Faça login."
                        )
                    }
                }

                is DeviceRegistrationResult.Error -> {
                    authRepository.deleteCurrentUser()
                    _uiState.update {
                        it.copy(isLoading = false, toastMessage = "Erro ao registrar dispositivo. Tente novamente.")
                    }
                }
            }
        }
    }

    private fun Throwable.toFriendlyLoginMessage(): String = when (this) {
        is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos."
        else -> message ?: "Erro ao entrar. Tente novamente."
    }

    private fun Throwable.toFriendlyRegisterMessage(): String = when (this) {
        is FirebaseAuthUserCollisionException -> "E-mail já cadastrado. Tente fazer login."
        is FirebaseAuthWeakPasswordException -> "Senha muito fraca. Use pelo menos $MIN_PASSWORD_LENGTH caracteres."
        is FirebaseAuthInvalidCredentialsException -> "E-mail inválido."
        else -> message ?: "Erro ao criar conta. Tente novamente."
    }
}