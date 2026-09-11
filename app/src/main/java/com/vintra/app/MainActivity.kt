// app/src/main/java/com/vintra/app/MainActivity.kt
package com.vintra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.vintra.app.ui.auth.LoginScreen
import com.vintra.app.ui.feed.CreatePostScreen
import com.vintra.app.ui.feed.PostDetailScreen
import com.vintra.app.ui.home.HomeScreen
import com.vintra.app.ui.navigation.CreatePostRoute
import com.vintra.app.ui.navigation.HomeRoute
import com.vintra.app.ui.navigation.LoginRoute
import com.vintra.app.ui.navigation.PostDetailRoute
import com.vintra.app.ui.navigation.ProfileSetupRoute
import com.vintra.app.ui.navigation.SessionRouterRoute
import com.vintra.app.ui.profile.ProfileSetupScreen
import com.vintra.app.ui.session.AuthStateViewModel
import com.vintra.app.ui.session.SessionRouter
import com.vintra.app.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                val authStateViewModel: AuthStateViewModel = hiltViewModel()
                val isAuthenticated by authStateViewModel.isAuthenticated.collectAsState()
                var hasBeenAuthenticated by remember { mutableStateOf(false) }

                LaunchedEffect(isAuthenticated) {
                    if (isAuthenticated) {
                        hasBeenAuthenticated = true
                    } else if (hasBeenAuthenticated) {
                        hasBeenAuthenticated = false
                        navController.navigate(SessionRouterRoute) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = SessionRouterRoute
                ) {
                    composable<SessionRouterRoute> {
                        SessionRouter(
                            onNavigateLogin = {
                                navController.navigate(LoginRoute) {
                                    popUpTo<SessionRouterRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateProfileSetup = {
                                navController.navigate(ProfileSetupRoute) {
                                    popUpTo<SessionRouterRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateHome = {
                                navController.navigate(HomeRoute) {
                                    popUpTo<SessionRouterRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<LoginRoute> {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo<LoginRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<ProfileSetupRoute> {
                        ProfileSetupScreen(
                            onSaved = {
                                navController.navigate(HomeRoute) {
                                    popUpTo<ProfileSetupRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<HomeRoute> {
                        HomeScreen(
                            onCreatePost = { navController.navigate(CreatePostRoute) },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo<HomeRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onPostClick = { postId ->
                                navController.navigate(PostDetailRoute(postId))
                            }
                        )
                    }

                    composable<CreatePostRoute> {
                        CreatePostScreen(
                            onClose = { navController.popBackStack() },
                            onPostSuccess = { navController.popBackStack() },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo<HomeRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<PostDetailRoute> {
                        PostDetailScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}