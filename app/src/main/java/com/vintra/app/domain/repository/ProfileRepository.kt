package com.vintra.app.domain.repository

import com.vintra.app.domain.model.UserProfile

sealed interface GetProfileResult {
    data class Success(val profile: UserProfile?) : GetProfileResult
    data class Error(val message: String) : GetProfileResult
}

sealed interface UsernameAvailability {
    data object Available : UsernameAvailability
    data object Taken : UsernameAvailability
    data class Error(val message: String) : UsernameAvailability
}

sealed interface SaveProfileResult {
    data object Success : SaveProfileResult
    data object UsernameTaken : SaveProfileResult
    data class Error(val message: String) : SaveProfileResult
}

interface ProfileRepository {
    suspend fun getProfile(uid: String): GetProfileResult
    suspend fun isUsernameAvailable(username: String, uid: String): UsernameAvailability
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