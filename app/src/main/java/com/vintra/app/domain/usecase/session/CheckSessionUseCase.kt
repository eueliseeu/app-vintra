package com.vintra.app.domain.usecase.session

import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.ProfileRepository
import javax.inject.Inject

sealed interface SessionState {
    data object LoggedOut : SessionState
    data object NeedsProfileSetup : SessionState
    data object Ready : SessionState
    data class ConnectionError(val message: String) : SessionState
}

class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): SessionState {
        val uid = authRepository.currentUser()?.uid ?: return SessionState.LoggedOut

        return when (val result = profileRepository.getProfile(uid)) {
            is GetProfileResult.Success -> {
                if (result.profile == null) SessionState.NeedsProfileSetup else SessionState.Ready
            }
            is GetProfileResult.Error -> SessionState.ConnectionError(result.message)
        }
    }
}