package com.vintra.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.model.AuthUser
import com.vintra.app.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult = try {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: return AuthResult.Error("Failed to authenticate user.")
        AuthResult.Success(AuthUser(uid = firebaseUser.uid, email = firebaseUser.email))
    } catch (exception: FirebaseAuthInvalidUserException) {
        AuthResult.UserNotFound
    } catch (exception: FirebaseAuthInvalidCredentialsException) {
        AuthResult.InvalidCredentials
    } catch (exception: Exception) {
        AuthResult.Error(exception.message ?: "Error logging in. Please try again.")
    }

    override suspend fun signUp(email: String, password: String): AuthResult = try {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: return AuthResult.Error("Failed to create user.")
        AuthResult.Success(AuthUser(uid = firebaseUser.uid, email = firebaseUser.email))
    } catch (exception: FirebaseAuthUserCollisionException) {
        AuthResult.EmailAlreadyInUse
    } catch (exception: FirebaseAuthWeakPasswordException) {
        AuthResult.WeakPassword
    } catch (exception: FirebaseAuthInvalidCredentialsException) {
        AuthResult.InvalidCredentials
    } catch (exception: Exception) {
        AuthResult.Error(exception.message ?: "Error creating account. Please try again.")
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
        Unit
    }

    override fun currentUser(): AuthUser? =
        firebaseAuth.currentUser?.let { AuthUser(uid = it.uid, email = it.email) }
}