package com.vintra.app.domain.repository

import com.vintra.app.domain.model.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun signUp(email: String, password: String): Result<AuthUser>
    suspend fun deleteCurrentUser(): Result<Unit>
    fun currentUser(): AuthUser?
}