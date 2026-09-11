package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.DeviceRegistrationResult
import com.vintra.app.domain.repository.DeviceRepository
import javax.inject.Inject

class EnforceDeviceLimitUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(authResult: AuthResult): RegisterAccountResult = when (authResult) {
        is AuthResult.Success -> {
            if (!authResult.isNewUser) {
                RegisterAccountResult.Success(isNewUser = false)
            } else {
                when (val deviceResult = deviceRepository.registerDevice(authResult.user.uid)) {
                    is DeviceRegistrationResult.Success -> RegisterAccountResult.Success(isNewUser = true)
                    is DeviceRegistrationResult.AlreadyRegistered -> {
                        authRepository.deleteCurrentUser()
                        authRepository.signOut()
                        RegisterAccountResult.DeviceAlreadyRegistered
                    }
                    is DeviceRegistrationResult.Error -> {
                        authRepository.deleteCurrentUser()
                        authRepository.signOut()
                        RegisterAccountResult.Error(deviceResult.message)
                    }
                }
            }
        }
        is AuthResult.EmailAlreadyInUse -> RegisterAccountResult.EmailAlreadyInUse
        is AuthResult.WeakPassword -> RegisterAccountResult.WeakPassword
        is AuthResult.InvalidCredentials -> RegisterAccountResult.InvalidEmail
        is AuthResult.Cancelled -> RegisterAccountResult.Cancelled
        is AuthResult.Error -> RegisterAccountResult.Error(authResult.message)
        is AuthResult.UserNotFound -> RegisterAccountResult.Error("User not found.")
    }
}