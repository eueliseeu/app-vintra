package com.vintra.app.domain.repository

sealed interface GetPhotoResult {
    data class Success(val base64: String?) : GetPhotoResult
    data class Error(val message: String) : GetPhotoResult
}

sealed interface SavePhotoResult {
    data object Success : SavePhotoResult
    data class Error(val message: String) : SavePhotoResult
}

interface PhotoRepository {
    suspend fun getPhoto(uid: String): GetPhotoResult
    suspend fun savePhoto(uid: String, base64: String): SavePhotoResult
}