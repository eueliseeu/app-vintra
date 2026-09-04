package com.vintra.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.vintra.app.domain.model.AuthUser
import com.vintra.app.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthUser> = runCatching {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: error("Falha ao autenticar usuário.")
        AuthUser(uid = firebaseUser.uid, email = firebaseUser.email)
    }

    override suspend fun signUp(email: String, password: String): Result<AuthUser> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: error("Falha ao criar usuário.")
        AuthUser(uid = firebaseUser.uid, email = firebaseUser.email)
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
        Unit
    }

    override fun currentUser(): AuthUser? =
        firebaseAuth.currentUser?.let { AuthUser(uid = it.uid, email = it.email) }
}