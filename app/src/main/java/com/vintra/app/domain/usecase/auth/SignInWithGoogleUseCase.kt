package com.vintra.app.domain.usecase.auth

import android.content.Context
import com.vintra.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val enforceDeviceLimitUseCase: EnforceDeviceLimitUseCase
) {
    suspend operator fun invoke(context: Context): RegisterAccountResult {
        val authResult = authRepository.signInWithGoogle(context)
        return enforceDeviceLimitUseCase(authResult)
    }
}