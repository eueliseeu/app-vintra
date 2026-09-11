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
private const val COLLECTION_POSTS = "posts"
private const val POST_AUTHOR_UID_FIELD = "authorUid"
private const val POST_AUTHOR_PHOTO_FIELD = "authorPhotoBase64"
private const val FIRESTORE_BATCH_LIMIT = 500

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
        syncPhotoOnExistingPosts(uid, base64)
        SavePhotoResult.Success
    } catch (exception: Exception) {
        SavePhotoResult.Error(exception.message ?: "Erro ao salvar foto de perfil.")
    }

    private suspend fun syncPhotoOnExistingPosts(uid: String, base64: String) {
        val posts = firestore.collection(COLLECTION_POSTS)
            .whereEqualTo(POST_AUTHOR_UID_FIELD, uid)
            .get()
            .await()

        posts.documents.chunked(FIRESTORE_BATCH_LIMIT).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { post ->
                batch.update(post.reference, POST_AUTHOR_PHOTO_FIELD, base64)
            }
            if (chunk.isNotEmpty()) {
                batch.commit().await()
            }
        }
    }
}
