package com.vintra.app.ui.jobs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vintra.app.domain.usecase.job.GetJobByIdUseCase
import com.vintra.app.ui.navigation.JobDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val getJobByIdUseCase: GetJobByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = savedStateHandle.toRoute<JobDetailRoute>().jobId

    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val job = getJobByIdUseCase(jobId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    job = job,
                    errorMessage = if (job == null) "Job not found." else null
                )
            }
        }
    }
}