package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.EmailLookupResult
import com.vintra.app.domain.repository.ProfileRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(identifier: String, password: String): AuthResult {
        val trimmed = identifier.trim()

        val email = if (trimmed.contains("@")) {
            trimmed
        } else {
            when (val result = profileRepository.getEmailForUsername(trimmed.lowercase())) {
                is EmailLookupResult.Success -> result.email
                is EmailLookupResult.UsernameNotFound -> return AuthResult.UserNotFound
                is EmailLookupResult.Error -> return AuthResult.Error(result.message)
            }
        }

        return authRepository.login(email, password)
    }
}