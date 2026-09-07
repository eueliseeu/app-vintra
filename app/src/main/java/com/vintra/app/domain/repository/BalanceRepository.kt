package com.vintra.app.domain.repository

sealed interface GetBalanceResult {
    data class Success(val amountCents: Long) : GetBalanceResult
    data class Error(val message: String) : GetBalanceResult
}

sealed interface InitBalanceResult {
    data object Success : InitBalanceResult
    data class Error(val message: String) : InitBalanceResult
}

interface BalanceRepository {
    suspend fun getBalance(uid: String): GetBalanceResult
    suspend fun initBalance(uid: String): InitBalanceResult
}