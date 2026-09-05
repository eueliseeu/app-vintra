package com.vintra.app.ui.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.repository.AuthRepository
import com.vintra.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionDestination {
    data object Loading : SessionDestination
    data object Login : SessionDestination
    data object ProfileSetup : SessionDestination
    data object Home : SessionDestination
}

@HiltViewModel
class SessionRouterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SessionDestination>(SessionDestination.Loading)
    val destination: StateFlow<SessionDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = authRepository.currentUser()?.uid
            if (uid == null) {
                _destination.value = SessionDestination.Login
                return@launch
            }
            val profile = profileRepository.getProfile(uid).getOrNull()
            _destination.value = if (profile == null) SessionDestination.ProfileSetup else SessionDestination.Home
        }
    }
}

@Composable
fun SessionRouter(
    onNavigateProfileSetup: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateLogin: () -> Unit,
    viewModel: SessionRouterViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SessionDestination.ProfileSetup -> onNavigateProfileSetup()
            SessionDestination.Home -> onNavigateHome()
            SessionDestination.Login -> onNavigateLogin()
            SessionDestination.Loading -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}