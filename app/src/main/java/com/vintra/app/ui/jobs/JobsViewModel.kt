package com.vintra.app.ui.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.repository.ObserveJobsResult
import com.vintra.app.domain.usecase.job.ObserveJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val observeJobsUseCase: ObserveJobsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    init {
        observeJobs()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun observeJobs() {
        viewModelScope.launch {
            observeJobsUseCase().collectLatest { result ->
                when (result) {
                    is ObserveJobsResult.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, jobs = result.jobs, errorMessage = null)
                        }
                    }
                    is ObserveJobsResult.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.message)
                        }
                    }
                }
            }
        }
    }
}