package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.repository.AuthRepository
import javax.inject.Inject

sealed interface RegisterAccountResult {
    data class Success(val isNewUser: Boolean) : RegisterAccountResult
    data object EmailAlreadyInUse : RegisterAccountResult
    data object WeakPassword : RegisterAccountResult
    data object InvalidEmail : RegisterAccountResult
    data object DeviceAlreadyRegistered : RegisterAccountResult
    data object Cancelled : RegisterAccountResult
    data class Error(val message: String) : RegisterAccountResult
}

class RegisterAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val enforceDeviceLimitUseCase: EnforceDeviceLimitUseCase
) {
    suspend operator fun invoke(email: String, password: String): RegisterAccountResult {
        val authResult = authRepository.signUp(email, password)
        return enforceDeviceLimitUseCase(authResult)
    }
}