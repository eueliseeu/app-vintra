package com.vintra.app.ui.components

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.auth.SignOutUseCase
import com.vintra.app.domain.usecase.profile.GetProfilePhotoUseCase
import com.vintra.app.domain.usecase.profile.UploadPhotoResult
import com.vintra.app.domain.usecase.profile.UploadProfilePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VintraTopBarUiState(
    val photoBase64: String? = null,
    val isUploadingPhoto: Boolean = false,
    val toastMessage: String? = null
)

@HiltViewModel
class VintraTopBarViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfilePhotoUseCase: GetProfilePhotoUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VintraTopBarUiState())
    val uiState: StateFlow<VintraTopBarUiState> = _uiState.asStateFlow()

    init {
        loadPhoto()
    }

    private fun loadPhoto() {
        val uid = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            when (val result = getProfilePhotoUseCase(uid)) {
                is GetPhotoResult.Success -> _uiState.update { it.copy(photoBase64 = result.base64) }
                is GetPhotoResult.Error -> Unit
            }
        }
    }

    fun onPhotoPicked(uri: Uri) {
        val uid = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true) }
            when (val result = uploadProfilePhotoUseCase(uid, uri)) {
                is UploadPhotoResult.Success -> {
                    loadPhoto()
                    _uiState.update { it.copy(isUploadingPhoto = false) }
                }
                is UploadPhotoResult.Error -> {
                    _uiState.update {
                        it.copy(isUploadingPhoto = false, toastMessage = "Error updating photo. Please try again.")
                    }
                }
            }
        }
    }

    fun logout() {
        signOutUseCase()
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}