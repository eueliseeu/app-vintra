package com.vintra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.ui.auth.LoginScreen
import com.vintra.app.ui.home.HomeScreen
import com.vintra.app.ui.profile.ProfileSetupScreen
import com.vintra.app.ui.session.AuthStateViewModel
import com.vintra.app.ui.session.SessionRouter
import com.vintra.app.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.vintra.app.ui.theme.DarkBackground

private sealed interface AppDestination {
    data object Login : AppDestination
    data object SessionRouter : AppDestination
    data object ProfileSetup : AppDestination
    data object Home : AppDestination
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    var destination by remember { mutableStateOf<AppDestination>(AppDestination.SessionRouter) }

                    val authStateViewModel: AuthStateViewModel = hiltViewModel()
                    val isAuthenticated by authStateViewModel.isAuthenticated.collectAsState()

                    LaunchedEffect(isAuthenticated) {
                        if (!isAuthenticated && destination != AppDestination.Login) {
                            destination = AppDestination.Login
                        }
                    }

                    when (destination) {
                        AppDestination.Login -> LoginScreen(
                            onLoginSuccess = { destination = AppDestination.SessionRouter }
                        )
                        AppDestination.SessionRouter -> SessionRouter(
                            onNavigateProfileSetup = { destination = AppDestination.ProfileSetup },
                            onNavigateHome = { destination = AppDestination.Home },
                            onNavigateLogin = { destination = AppDestination.Login }
                        )
                        AppDestination.ProfileSetup -> ProfileSetupScreen(
                            onSaved = { destination = AppDestination.Home }
                        )
                        AppDestination.Home -> HomeScreen()
                    }
                }
            }
        }
    }
}