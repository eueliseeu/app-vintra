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
import com.vintra.app.ui.auth.LoginScreen
import com.vintra.app.ui.faq.FaqScreen
import com.vintra.app.ui.feed.CreatePostScreen
import com.vintra.app.ui.feed.PostDetailScreen
import com.vintra.app.ui.home.HomeScreen
import com.vintra.app.ui.jobs.CreateJobScreen
import com.vintra.app.ui.jobs.JobDetailScreen
import com.vintra.app.ui.jobs.JobsScreen
import com.vintra.app.ui.navigation.CreateJobRoute
import com.vintra.app.ui.navigation.CreatePostRoute
import com.vintra.app.ui.navigation.FaqRoute
import com.vintra.app.ui.navigation.HomeRoute
import com.vintra.app.ui.navigation.JobDetailRoute
import com.vintra.app.ui.navigation.JobsRoute
import com.vintra.app.ui.navigation.LoginRoute
import com.vintra.app.ui.navigation.PostDetailRoute
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
                            onCreatePost = { navController.navigate(CreatePostRoute()) },
                            onCreateJob = { navController.navigate(CreateJobRoute()) },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo<HomeRoute> { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onPostClick = { postId ->
                                navController.navigate(PostDetailRoute(postId))
                            },
                            onNavigateToJobs = {
                                navController.navigate(JobsRoute) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToFaq = {
                                navController.navigate(FaqRoute) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<JobsRoute> {
                        JobsScreen(
                            onCreatePost = { navController.navigate(CreatePostRoute()) },
                            onCreateJob = { navController.navigate(CreateJobRoute()) },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onJobClick = { jobId ->
                                navController.navigate(JobDetailRoute(jobId))
                            },
                            onNavigateToHome = {
                                navController.navigate(HomeRoute) {
                                    launchSingleTop = true
                                    popUpTo(HomeRoute) { inclusive = true }
                                }
                            },
                            onNavigateToFaq = {
                                navController.navigate(FaqRoute) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<FaqRoute> {
                        FaqScreen(
                            onCreatePost = { navController.navigate(CreatePostRoute()) },
                            onCreateJob = { navController.navigate(CreateJobRoute()) },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(HomeRoute) {
                                    launchSingleTop = true
                                    popUpTo(HomeRoute) { inclusive = true }
                                }
                            },
                            onNavigateToJobs = {
                                navController.navigate(JobsRoute) {
                                    launchSingleTop = true
                                }
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

                    composable<CreateJobRoute> {
                        CreateJobScreen(
                            onClose = { navController.popBackStack() },
                            onSuccess = { navController.popBackStack() },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<PostDetailRoute> {
                        PostDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { postId ->
                                navController.navigate(CreatePostRoute(postId = postId))
                            }
                        )
                    }

                    composable<JobDetailRoute> {
                        JobDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { jobId ->
                                navController.navigate(CreateJobRoute(jobId = jobId))
                            },
                            onProfileClick = { navController.navigate(ProfileSetupRoute) },
                            onLogout = {
                                navController.navigate(SessionRouterRoute) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}