package com.vintra.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.model.ProfileEditability
import com.vintra.app.domain.model.editability
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.SaveProfileResult
import com.vintra.app.domain.repository.UsernameAvailability
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.profile.CheckUsernameAvailabilityUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import com.vintra.app.domain.usecase.profile.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val DATE_PATTERN = "dd/MM/yyyy"

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    private val usernameInput = MutableStateFlow("")
    private val uid: String? get() = getCurrentUserUseCase()?.uid

    init {
        loadProfile()
        observeUsernameChanges()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val currentUid = uid ?: return@launch
            val authEmail = getCurrentUserUseCase()?.email.orEmpty()

            when (val result = getProfileUseCase(currentUid)) {
                is GetProfileResult.Success -> {
                    val profile = result.profile
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                name = profile.name,
                                username = profile.username,
                                email = profile.email.ifBlank { authEmail },
                                birthDateText = profile.birthDateMillis.takeIf { ms -> ms > 0 }?.let(::formatDateText).orEmpty(),
                                originalUsername = profile.username,
                                editability = profile.editability(),
                                usernameStatus = UsernameCheckStatus.AVAILABLE
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, email = authEmail) }
                    }
                }
                is GetProfileResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, email = authEmail, toastMessage = "Error loading profile. Please try again.")
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onUsernameChange(value: String) {
        val normalized = value.trim().lowercase()
        _uiState.update { it.copy(username = normalized) }
        usernameInput.value = normalized
    }

    fun onBirthDateTextChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(8)
        val formatted = buildString {
            digitsOnly.forEachIndexed { index, digit ->
                append(digit)
                if (index == 1 || index == 3) append('/')
            }
        }
        _uiState.update { it.copy(birthDateText = formatted) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun observeUsernameChanges() {
        viewModelScope.launch {
            usernameInput
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { username ->
                    val currentUid = uid ?: return@collectLatest

                    if (username == _uiState.value.originalUsername) {
                        _uiState.update { it.copy(usernameStatus = UsernameCheckStatus.AVAILABLE) }
                        return@collectLatest
                    }

                    if (!isValidUsernameFormat(username)) {
                        _uiState.update {
                            it.copy(
                                usernameStatus = if (username.isEmpty()) UsernameCheckStatus.IDLE else UsernameCheckStatus.INVALID
                            )
                        }
                        return@collectLatest
                    }

                    _uiState.update { it.copy(usernameStatus = UsernameCheckStatus.CHECKING) }

                    when (checkUsernameAvailabilityUseCase(username, currentUid)) {
                        is UsernameAvailability.Available -> {
                            _uiState.update { it.copy(usernameStatus = UsernameCheckStatus.AVAILABLE) }
                        }
                        is UsernameAvailability.Taken -> {
                            _uiState.update { it.copy(usernameStatus = UsernameCheckStatus.TAKEN) }
                        }
                        is UsernameAvailability.Error -> {
                            _uiState.update { it.copy(usernameStatus = UsernameCheckStatus.IDLE) }
                        }
                    }
                }
        }
    }

    private fun isValidUsernameFormat(username: String): Boolean =
        username.length in 3..20 && username.matches(Regex("^[a-z0-9_]+$"))

    private fun parseBirthDate(text: String): Long? {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.US)
        format.isLenient = false
        return try {
            format.parse(text.trim())?.time
        } catch (exception: Exception) {
            null
        }
    }

    private fun formatDateText(millis: Long): String =
        SimpleDateFormat(DATE_PATTERN, Locale.US).format(millis)

    fun save() {
        val state = _uiState.value
        val currentUid = uid ?: return

        if (state.editability is ProfileEditability.Locked) return

        if (state.name.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Please enter your name.") }
            return
        }
        if (!isValidUsernameFormat(state.username)) {
            _uiState.update {
                it.copy(toastMessage = "Invalid username. Use only letters and numbers, between 3 and 20 characters.")
            }
            return
        }
        if (state.usernameStatus == UsernameCheckStatus.TAKEN) {
            _uiState.update { it.copy(toastMessage = "This username is already taken.") }
            return
        }
        if (state.usernameStatus == UsernameCheckStatus.CHECKING) {
            _uiState.update { it.copy(toastMessage = "Please wait while we check the username.") }
            return
        }
        val birthDateMillis = parseBirthDate(state.birthDateText)
        if (birthDateMillis == null) {
            _uiState.update { it.copy(toastMessage = "Invalid date. Use the format dd/mm/yyyy.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            when (
                val result = saveProfileUseCase(
                    uid = currentUid,
                    name = state.name.trim(),
                    username = state.username,
                    email = state.email,
                    birthDateMillis = birthDateMillis,
                    nationality = FIXED_NATIONALITY,
                    previousUsername = state.originalUsername
                )
            ) {
                is SaveProfileResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                is SaveProfileResult.UsernameTaken -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            usernameStatus = UsernameCheckStatus.TAKEN,
                            toastMessage = "This username is already taken."
                        )
                    }
                }
                is SaveProfileResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, toastMessage = "Error saving. Please try again.") }
                }
            }
        }
    }
}