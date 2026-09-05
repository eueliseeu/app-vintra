package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult =
        authRepository.login(email, password)
}