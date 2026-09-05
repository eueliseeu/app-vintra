package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vintra.app.domain.model.UserProfile
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
            val profile = UserProfile(
                uid = uid,
                name = snapshot.getString("name").orEmpty(),
                username = snapshot.getString("username").orEmpty(),
                email = snapshot.getString("email").orEmpty(),
                birthDateMillis = snapshot.getLong("birthDateMillis") ?: 0L,
                nationality = snapshot.getString("nationality").orEmpty(),
                createdAt = snapshot.getLong("createdAt") ?: 0L,
                updatedAt = snapshot.getLong("updatedAt") ?: 0L
            )
            GetProfileResult.Success(profile)
        }
    } catch (exception: Exception) {
        GetProfileResult.Error(exception.message ?: "Erro ao buscar perfil.")
    }

    override suspend fun isUsernameAvailable(username: String, uid: String): UsernameAvailability = try {
        val snapshot = firestore.collection(COLLECTION_USERNAMES).document(username).get().await()
        if (!snapshot.exists() || snapshot.getString("uid") == uid) {
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
                if (newUsernameSnapshot.exists() && newUsernameSnapshot.getString("uid") != uid) {
                    throw UsernameTakenException()
                }

                val existingUserSnapshot = transaction.get(usersRef)
                val createdAt = if (existingUserSnapshot.exists()) {
                    existingUserSnapshot.getLong("createdAt") ?: System.currentTimeMillis()
                } else {
                    System.currentTimeMillis()
                }

                if (oldUsernameRef != null) {
                    transaction.delete(oldUsernameRef)
                }
                transaction.set(newUsernameRef, mapOf("uid" to uid))

                val profileData = mapOf(
                    "name" to name,
                    "username" to username,
                    "email" to email,
                    "birthDateMillis" to birthDateMillis,
                    "nationality" to nationality,
                    "createdAt" to createdAt,
                    "updatedAt" to System.currentTimeMillis()
                )
                transaction.set(usersRef, profileData)
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