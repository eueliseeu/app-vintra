package com.vintra.app.domain.repository

import com.vintra.app.domain.model.UserProfile

sealed interface SaveProfileResult {
    data object Success : SaveProfileResult
    data object UsernameTaken : SaveProfileResult
    data class Error(val message: String) : SaveProfileResult
}

interface ProfileRepository {
    suspend fun getProfile(uid: String): Result<UserProfile?>
    suspend fun isUsernameAvailable(username: String, uid: String): Result<Boolean>
    suspend fun saveProfile(
        uid: String,
        name: String,
        username: String,
        email: String,
        birthDateMillis: Long,
        nationality: String,
        previousUsername: String?
    ): SaveProfileResult
}