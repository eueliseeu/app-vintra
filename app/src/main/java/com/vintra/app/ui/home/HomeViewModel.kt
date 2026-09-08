package com.vintra.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.repository.GetBalanceResult
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.balance.ObserveBalanceUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val observeBalanceUseCase: ObserveBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeBalance()
    }

    private fun loadProfile() {
        val uid = getCurrentUserUseCase()?.uid ?: return

        viewModelScope.launch {
            val firstName = when (val profileResult = getProfileUseCase(uid)) {
                is GetProfileResult.Success -> profileResult.profile?.name
                    ?.trim()
                    ?.substringBefore(" ")
                    .orEmpty()
                is GetProfileResult.Error -> ""
            }

            _uiState.update { it.copy(firstName = firstName) }
        }
    }

    private fun observeBalance() {
        val uid = getCurrentUserUseCase()?.uid ?: return

        viewModelScope.launch {
            observeBalanceUseCase(uid).collectLatest { result ->
                val amountCents = when (result) {
                    is GetBalanceResult.Success -> result.amountCents
                    is GetBalanceResult.Error -> 0L
                }
                _uiState.update { it.copy(isLoading = false, amountCents = amountCents) }
            }
        }
    }
}