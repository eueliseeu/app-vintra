package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.BalanceRepository
import com.vintra.app.domain.repository.DeviceRegistrationResult
import com.vintra.app.domain.repository.DeviceRepository
import com.vintra.app.domain.repository.InitBalanceResult
import javax.inject.Inject

sealed interface RegisterAccountResult {
    data object Success : RegisterAccountResult
    data object EmailAlreadyInUse : RegisterAccountResult
    data object WeakPassword : RegisterAccountResult
    data object InvalidEmail : RegisterAccountResult
    data object DeviceAlreadyRegistered : RegisterAccountResult
    data class Error(val message: String) : RegisterAccountResult
}

class RegisterAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val deviceRepository: DeviceRepository,
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(email: String, password: String): RegisterAccountResult {
        val authResult = authRepository.signUp(email, password)

        val newUser = when (authResult) {
            is AuthResult.Success -> authResult.user
            is AuthResult.EmailAlreadyInUse -> return RegisterAccountResult.EmailAlreadyInUse
            is AuthResult.WeakPassword -> return RegisterAccountResult.WeakPassword
            is AuthResult.InvalidCredentials -> return RegisterAccountResult.InvalidEmail
            is AuthResult.Error -> return RegisterAccountResult.Error(authResult.message)
            else -> return RegisterAccountResult.Error("Erro ao criar conta. Tente novamente.")
        }

        when (val deviceResult = deviceRepository.registerDevice(newUser.uid)) {
            is DeviceRegistrationResult.Success -> Unit
            is DeviceRegistrationResult.AlreadyRegistered -> {
                authRepository.deleteCurrentUser()
                return RegisterAccountResult.DeviceAlreadyRegistered
            }
            is DeviceRegistrationResult.Error -> {
                authRepository.deleteCurrentUser()
                return RegisterAccountResult.Error(deviceResult.message)
            }
        }

        return when (val balanceResult = balanceRepository.initBalance(newUser.uid)) {
            is InitBalanceResult.Success -> RegisterAccountResult.Success
            is InitBalanceResult.Error -> {
                authRepository.deleteCurrentUser()
                RegisterAccountResult.Error(balanceResult.message)
            }
        }
    }
}