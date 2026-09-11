package com.vintra.app.domain.usecase.auth

import android.app.Activity
import com.vintra.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGitHubUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val enforceDeviceLimitUseCase: EnforceDeviceLimitUseCase
) {
    suspend operator fun invoke(activity: Activity): RegisterAccountResult {
        val authResult = authRepository.signInWithGitHub(activity)
        return enforceDeviceLimitUseCase(authResult)
    }
}