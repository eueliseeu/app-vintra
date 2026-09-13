package com.vintra.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.ui.navigation.BottomTab
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L

@Composable
fun AuthenticatedScaffold(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onCreatePost: () -> Unit = {},
    onCreateJob: (() -> Unit)? = null,
    topBarTitle: String = "Global",
    topBarViewModel: VintraTopBarViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val topBarState by topBarViewModel.uiState.collectAsState()
    val canCreateJob = onCreateJob != null && topBarState.isVerified

    LaunchedEffect(topBarState.toastMessage) {
        if (topBarState.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            topBarViewModel.clearToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                VintraLayoutContainer {
                    VintraTopBar(
                        photoBase64 = topBarState.photoBase64,
                        isUploadingPhoto = topBarState.isUploadingPhoto,
                        username = topBarState.username,
                        isVerified = topBarState.isVerified,
                        onPhotoPicked = topBarViewModel::onPhotoPicked,
                        onProfileClick = onProfileClick,
                        onLogout = {
                            topBarViewModel.logout()
                            onLogout()
                        },
                        title = topBarTitle
                    )
                }
            },
            bottomBar = {
                VintraBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                VintraLayoutContainer {
                    content()
                }

                FloatingCreateButton(
                    onCreatePost = onCreatePost,
                    onCreateJob = onCreateJob,
                    canCreateJob = canCreateJob,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                )
            }
        }

        CenterToast(message = topBarState.toastMessage)
    }
}