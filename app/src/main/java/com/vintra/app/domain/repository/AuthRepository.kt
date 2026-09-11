package com.vintra.app.domain.repository

import android.app.Activity
import android.content.Context
import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(context: Context): AuthResult
    suspend fun signInWithGitHub(activity: Activity): AuthResult
    suspend fun deleteCurrentUser(): Result<Unit>
    fun currentUser(): AuthUser?
    fun observeAuthState(): Flow<Boolean>
    fun signOut()
}