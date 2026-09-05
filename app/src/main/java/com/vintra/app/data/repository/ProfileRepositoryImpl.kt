package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.UserProfileDto
import com.vintra.app.data.model.UsernameDto
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.ProfileRepository
import com.vintra.app.domain.repository.SaveProfileResult
import com.vintra.app.domain.repository.UsernameAvailability
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_USERS = "users"
private const val COLLECTION_USERNAMES = "usernames"

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    override suspend fun getProfile(uid: String): GetProfileResult = try {
        val snapshot = firestore.collection(COLLECTION_USERS).document(uid).get().await()

        if (!snapshot.exists()) {
            GetProfileResult.Success(null)
        } else {
            val dto = snapshot.toObject(UserProfileDto::class.java)
            GetProfileResult.Success(dto?.toDomain(uid))
        }
    } catch (exception: Exception) {
        GetProfileResult.Error(exception.message ?: "Erro ao buscar perfil.")
    }

    override suspend fun isUsernameAvailable(username: String, uid: String): UsernameAvailability = try {
        val snapshot = firestore.collection(COLLECTION_USERNAMES).document(username).get().await()
        val dto = snapshot.toObject(UsernameDto::class.java)
        if (!snapshot.exists() || dto?.uid == uid) {
            UsernameAvailability.Available
        } else {
            UsernameAvailability.Taken
        }
    } catch (exception: Exception) {
        UsernameAvailability.Error(exception.message ?: "Erro ao verificar username.")
    }

    override suspend fun saveProfile(
        uid: String,
        name: String,
        username: String,
        email: String,
        birthDateMillis: Long,
        nationality: String,
        previousUsername: String?
    ): SaveProfileResult {
        val usersRef = firestore.collection(COLLECTION_USERS).document(uid)
        val newUsernameRef = firestore.collection(COLLECTION_USERNAMES).document(username)
        val oldUsernameRef = previousUsername
            ?.takeIf { it != username }
            ?.let { firestore.collection(COLLECTION_USERNAMES).document(it) }

        return try {
            firestore.runTransaction { transaction ->
                val newUsernameSnapshot = transaction.get(newUsernameRef)
                val existingUsernameOwner = newUsernameSnapshot.toObject(UsernameDto::class.java)?.uid
                if (newUsernameSnapshot.exists() && existingUsernameOwner != uid) {
                    throw UsernameTakenException()
                }

                val existingUserSnapshot = transaction.get(usersRef)
                val existingDto = existingUserSnapshot.toObject(UserProfileDto::class.java)
                val createdAt = existingDto?.createdAt?.takeIf { it > 0 } ?: System.currentTimeMillis()

                if (oldUsernameRef != null) {
                    transaction.delete(oldUsernameRef)
                }
                transaction.set(newUsernameRef, UsernameDto(uid = uid))

                val profileDto = UserProfileDto(
                    name = name,
                    username = username,
                    email = email,
                    birthDateMillis = birthDateMillis,
                    nationality = nationality,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                transaction.set(usersRef, profileDto)
                Unit
            }.await()

            SaveProfileResult.Success
        } catch (exception: UsernameTakenException) {
            SaveProfileResult.UsernameTaken
        } catch (exception: Exception) {
            SaveProfileResult.Error(exception.message ?: "Erro ao salvar informações.")
        }
    }

    private class UsernameTakenException : Exception()
}