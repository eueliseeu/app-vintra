package com.vintra.app.domain.usecase.balance

import com.vintra.app.domain.repository.BalanceRepository
import com.vintra.app.domain.repository.GetBalanceResult
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(uid: String): GetBalanceResult = balanceRepository.getBalance(uid)
}