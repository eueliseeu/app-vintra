package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.UserPhotoDto
import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.repository.PhotoRepository
import com.vintra.app.domain.repository.SavePhotoResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_USER_PHOTOS = "userPhotos"

class PhotoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PhotoRepository {

    override suspend fun getPhoto(uid: String): GetPhotoResult = try {
        val snapshot = firestore.collection(COLLECTION_USER_PHOTOS).document(uid).get().await()
        if (!snapshot.exists()) {
            GetPhotoResult.Success(null)
        } else {
            val dto = snapshot.toObject(UserPhotoDto::class.java)
            GetPhotoResult.Success(dto?.toDomain(uid)?.base64)
        }
    } catch (exception: Exception) {
        GetPhotoResult.Error(exception.message ?: "Erro ao buscar foto de perfil.")
    }

    override suspend fun savePhoto(uid: String, base64: String): SavePhotoResult = try {
        val dto = UserPhotoDto(base64 = base64, updatedAt = System.currentTimeMillis())
        firestore.collection(COLLECTION_USER_PHOTOS).document(uid).set(dto).await()
        SavePhotoResult.Success
    } catch (exception: Exception) {
        SavePhotoResult.Error(exception.message ?: "Erro ao salvar foto de perfil.")
    }
}