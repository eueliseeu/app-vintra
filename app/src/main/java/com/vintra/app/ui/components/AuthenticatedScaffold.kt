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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.ui.navigation.BottomTab
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L

@Composable
fun AuthenticatedScaffold(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    topBarViewModel: VintraTopBarViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val topBarState by topBarViewModel.uiState.collectAsState()

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
                VintraTopBar(
                    photoBase64 = topBarState.photoBase64,
                    isUploadingPhoto = topBarState.isUploadingPhoto,
                    onPhotoPicked = topBarViewModel::onPhotoPicked
                )
            },
            bottomBar = { VintraBottomBar(selectedTab = selectedTab, onTabSelected = onTabSelected) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }

        CenterToast(message = topBarState.toastMessage)
    }
}