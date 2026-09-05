package com.vintra.app.domain.model

sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data object UserNotFound : AuthResult
    data object InvalidCredentials : AuthResult
    data object EmailAlreadyInUse : AuthResult
    data object WeakPassword : AuthResult
    data class Error(val message: String) : AuthResult
}