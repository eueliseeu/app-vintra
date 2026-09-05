package com.vintra.app.domain.usecase.auth

import com.vintra.app.domain.model.AuthUser
import com.vintra.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): AuthUser? = authRepository.currentUser()
}