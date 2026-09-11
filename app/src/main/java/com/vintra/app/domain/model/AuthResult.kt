package com.vintra.app.domain.model

sealed interface AuthResult {
    data class Success(val user: AuthUser, val isNewUser: Boolean) : AuthResult
    data object UserNotFound : AuthResult
    data object InvalidCredentials : AuthResult
    data object EmailAlreadyInUse : AuthResult
    data object WeakPassword : AuthResult
    data object Cancelled : AuthResult
    data class Error(val message: String) : AuthResult
}