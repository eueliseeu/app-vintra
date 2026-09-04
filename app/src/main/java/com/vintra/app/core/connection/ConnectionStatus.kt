package com.vintra.app.core.connection

sealed interface ConnectionStatus {
    data object Loading : ConnectionStatus
    data class Success(val message: String, val timestamp: Long) : ConnectionStatus
    data class Error(val message: String) : ConnectionStatus
}
