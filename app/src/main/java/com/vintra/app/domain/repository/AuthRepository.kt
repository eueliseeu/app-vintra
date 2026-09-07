// app/src/main/java/com/vintra/app/domain/repository/AuthRepository.kt
package com.vintra.app.domain.repository

import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String): AuthResult
    suspend fun deleteCurrentUser(): Result<Unit>
    fun currentUser(): AuthUser?
    fun observeAuthState(): Flow<Boolean>
    fun signOut()
}