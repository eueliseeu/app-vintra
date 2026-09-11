package com.vintra.app.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.vintra.app.R
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.AuthResult
import com.vintra.app.domain.model.AuthUser
import com.vintra.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult = try {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: return AuthResult.Error("Failed to authenticate user.")
        AuthResult.Success(firebaseUser.toAuthUser(), isNewUser = false)
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
        AuthResult.Success(firebaseUser.toAuthUser(), isNewUser = true)
    } catch (exception: FirebaseAuthUserCollisionException) {
        AuthResult.EmailAlreadyInUse
    } catch (exception: FirebaseAuthWeakPasswordException) {
        AuthResult.WeakPassword
    } catch (exception: FirebaseAuthInvalidCredentialsException) {
        AuthResult.InvalidCredentials
    } catch (exception: Exception) {
        AuthResult.Error(exception.message ?: "Error creating account. Please try again.")
    }

    override suspend fun signInWithGoogle(context: Context): AuthResult = try {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = credentialManager.getCredential(context, request)
        val credential = response.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user ?: return AuthResult.Error("Failed to authenticate with Google.")
            AuthResult.Success(
                user = firebaseUser.toAuthUser(),
                isNewUser = authResult.additionalUserInfo?.isNewUser == true
            )
        } else {
            AuthResult.Error("Invalid Google credential.")
        }
    } catch (exception: GetCredentialCancellationException) {
        AuthResult.Cancelled
    } catch (exception: Exception) {
        AuthResult.Error(exception.message ?: "Error signing in with Google.")
    }

    override suspend fun signInWithGitHub(activity: Activity): AuthResult = try {
        val provider = OAuthProvider.newBuilder("github.com")
        val authResult = firebaseAuth.startActivityForSignInWithProvider(activity, provider.build()).await()
        val firebaseUser = authResult.user ?: return AuthResult.Error("Failed to authenticate with GitHub.")
        AuthResult.Success(
            user = firebaseUser.toAuthUser(),
            isNewUser = authResult.additionalUserInfo?.isNewUser == true
        )
    } catch (exception: FirebaseAuthUserCollisionException) {
        AuthResult.Error("An account already exists with this e-mail using a different sign-in method.")
    } catch (exception: Exception) {
        AuthResult.Error(exception.message ?: "Error signing in with GitHub.")
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
        Unit
    }

    override fun currentUser(): AuthUser? = firebaseAuth.currentUser?.toAuthUser()

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toAuthUser(): AuthUser =
        AuthUser(uid = uid, email = email, provider = resolveProvider())

    private fun FirebaseUser.resolveProvider(): AuthProvider {
        val providerIds = providerData.map { it.providerId }
        return when {
            providerIds.contains(GoogleAuthProvider.PROVIDER_ID) -> AuthProvider.GOOGLE
            providerIds.contains("github.com") -> AuthProvider.GITHUB
            providerIds.contains(EmailAuthProvider.PROVIDER_ID) -> AuthProvider.EMAIL
            else -> AuthProvider.UNKNOWN
        }
    }
}