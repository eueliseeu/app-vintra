package com.vintra.app.ui.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.usecase.session.CheckSessionUseCase
import com.vintra.app.domain.usecase.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val SESSION_CHECK_TIMEOUT_MILLIS = 8000L

sealed interface SessionDestination {
    data object Loading : SessionDestination
    data object Login : SessionDestination
    data object ProfileSetup : SessionDestination
    data object Home : SessionDestination
    data object ConnectionError : SessionDestination
}

@HiltViewModel
class SessionRouterViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SessionDestination>(SessionDestination.Loading)
    val destination: StateFlow<SessionDestination> = _destination.asStateFlow()

    init {
        checkSession()
    }

    fun retry() {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            _destination.value = SessionDestination.Loading

            val result = withTimeoutOrNull(SESSION_CHECK_TIMEOUT_MILLIS) { checkSessionUseCase() }

            _destination.value = when (result) {
                null -> SessionDestination.ConnectionError
                is SessionState.LoggedOut -> SessionDestination.Login
                is SessionState.NeedsProfileSetup -> SessionDestination.ProfileSetup
                is SessionState.Ready -> SessionDestination.Home
                is SessionState.ConnectionError -> SessionDestination.ConnectionError
            }
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
            SessionDestination.Loading, SessionDestination.ConnectionError -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (destination) {
            SessionDestination.ConnectionError -> ConnectionErrorContent(onRetry = viewModel::retry)
            else -> CircularProgressIndicator()
        }
    }
}

@Composable
private fun ConnectionErrorContent(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Connection error. Check your internet and try again.",
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Try again")
        }
    }
}