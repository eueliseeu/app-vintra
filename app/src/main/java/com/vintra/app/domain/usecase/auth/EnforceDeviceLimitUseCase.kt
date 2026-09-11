package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthResult
import javax.inject.Inject

class EnforceDeviceLimitUseCase @Inject constructor() {
    operator fun invoke(authResult: AuthResult): RegisterAccountResult = when (authResult) {
        is AuthResult.Success -> RegisterAccountResult.Success(isNewUser = authResult.isNewUser)
        is AuthResult.EmailAlreadyInUse -> RegisterAccountResult.EmailAlreadyInUse
        is AuthResult.WeakPassword -> RegisterAccountResult.WeakPassword
        is AuthResult.InvalidCredentials -> RegisterAccountResult.InvalidEmail
        is AuthResult.Cancelled -> RegisterAccountResult.Cancelled
        is AuthResult.Error -> RegisterAccountResult.Error(authResult.message)
        is AuthResult.UserNotFound -> RegisterAccountResult.Error("User not found.")
    }
}