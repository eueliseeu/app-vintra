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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vintra.app.ui.auth.LoginScreen
import com.vintra.app.ui.home.HomeScreen
import com.vintra.app.ui.navigation.HomeRoute
import com.vintra.app.ui.navigation.LoginRoute
import com.vintra.app.ui.navigation.ProfileSetupRoute
import com.vintra.app.ui.navigation.SessionRouterRoute
import com.vintra.app.ui.profile.ProfileSetupScreen
import com.vintra.app.ui.session.AuthStateViewModel
import com.vintra.app.ui.session.SessionRouter
import com.vintra.app.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val authStateViewModel: AuthStateViewModel = hiltViewModel()
                    val isAuthenticated by authStateViewModel.isAuthenticated.collectAsState()

                    LaunchedEffect(isAuthenticated) {
                        if (!isAuthenticated) {
                            navController.navigate(LoginRoute) {
                                popUpTo<SessionRouterRoute> { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = SessionRouterRoute
                    ) {
                        composable<LoginRoute> {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(SessionRouterRoute) {
                                        popUpTo<LoginRoute> { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<SessionRouterRoute> {
                            SessionRouter(
                                onNavigateProfileSetup = {
                                    navController.navigate(ProfileSetupRoute) {
                                        popUpTo<SessionRouterRoute> { inclusive = true }
                                    }
                                },
                                onNavigateHome = {
                                    navController.navigate(HomeRoute) {
                                        popUpTo<SessionRouterRoute> { inclusive = true }
                                    }
                                },
                                onNavigateLogin = {
                                    navController.navigate(LoginRoute) {
                                        popUpTo<SessionRouterRoute> { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<ProfileSetupRoute> {
                            ProfileSetupScreen(
                                onSaved = {
                                    navController.navigate(HomeRoute) {
                                        popUpTo<ProfileSetupRoute> { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<HomeRoute> {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}