package com.vintra.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.ui.navigation.BottomTab
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L

@Composable
fun AuthenticatedScaffold(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onCreatePost: () -> Unit,
    onCreateJob: (() -> Unit)? = null,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    topBarViewModel: VintraTopBarViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val topBarState by topBarViewModel.uiState.collectAsState()
    var showCreateMenu by remember { mutableStateOf(false) }

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
                        }
                    )
                }
            },
            bottomBar = {
                Box {
                    VintraBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onCreateClick = {
                            if (canCreateJob) {
                                showCreateMenu = true
                            } else {
                                onCreatePost()
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showCreateMenu,
                        onDismissRequest = { showCreateMenu = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color(0xFF131313)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Post", color = Color.White) },
                            onClick = {
                                showCreateMenu = false
                                onCreatePost()
                            }
                        )
                        if (canCreateJob) {
                            DropdownMenuItem(
                                text = { Text("Job", color = Color.White) },
                                onClick = {
                                    showCreateMenu = false
                                    onCreateJob?.invoke()
                                }
                            )
                        }
                    }
                }
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
            }
        }

        CenterToast(message = topBarState.toastMessage)
    }
}