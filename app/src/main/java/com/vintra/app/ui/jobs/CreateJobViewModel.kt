package com.vintra.app.ui.jobs

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.model.JobTag
import com.vintra.app.domain.repository.CreateJobResult
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.job.CreateJobUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val createJobUseCase: CreateJobUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateJobUiState())
    val uiState: StateFlow<CreateJobUiState> = _uiState.asStateFlow()

    fun onCompanyNameChange(value: String) {
        _uiState.update { it.copy(companyName = value) }
    }

    fun onCompanyUsernameChange(value: String) {
        _uiState.update { it.copy(companyUsername = value) }
    }

    fun onCompanyPhotoPicked(uri: Uri) {
        _uiState.update { it.copy(companyPhotoUri = uri) }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value.take(120)) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value.take(4000)) }
    }

    fun onLinkUrlChange(value: String) {
        _uiState.update { it.copy(linkUrl = value) }
    }

    fun onButtonLabelChange(value: String) {
        _uiState.update { it.copy(buttonLabel = value.take(24)) }
    }

    fun toggleTag(tag: JobTag) {
        _uiState.update { state ->
            val next = state.selectedTags.toMutableSet()
            if (!next.add(tag)) next.remove(tag)
            state.copy(selectedTags = next)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun consumePublished() {
        _uiState.update { it.copy(published = false) }
    }

    fun publish() {
        val state = _uiState.value
        val uid = getCurrentUserUseCase()?.uid ?: return

        if (state.companyName.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Company name is required.") }
            return
        }
        if (state.title.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Job title is required.") }
            return
        }
        if (state.description.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Description is required.") }
            return
        }
        val link = state.linkUrl.trim()
        if (link.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Link is required.") }
            return
        }
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            _uiState.update { it.copy(toastMessage = "Link must start with http:// or https://.") }
            return
        }

        viewModelScope.launch {
            when (val profileResult = getProfileUseCase(uid)) {
                is GetProfileResult.Success -> {
                    if (profileResult.profile?.isVerified != true) {
                        _uiState.update {
                            it.copy(toastMessage = "Only verified accounts can post jobs.")
                        }
                        return@launch
                    }
                }
                is GetProfileResult.Error -> {
                    _uiState.update { it.copy(toastMessage = "Could not verify your account.") }
                    return@launch
                }
            }

            _uiState.update { it.copy(isPublishing = true) }

            when (
                val result = createJobUseCase(
                    publisherUid = uid,
                    companyName = state.companyName,
                    companyUsername = state.companyUsername,
                    companyPhotoUri = state.companyPhotoUri,
                    companyPhotoBase64Fallback = null,
                    title = state.title,
                    description = state.description,
                    linkUrl = link,
                    buttonLabel = state.buttonLabel,
                    tags = state.selectedTags.toList()
                )
            ) {
                is CreateJobResult.Success -> {
                    _uiState.update {
                        it.copy(isPublishing = false, published = true)
                    }
                }
                is CreateJobResult.Error -> {
                    _uiState.update {
                        it.copy(isPublishing = false, toastMessage = result.message)
                    }
                }
            }
        }
    }
}