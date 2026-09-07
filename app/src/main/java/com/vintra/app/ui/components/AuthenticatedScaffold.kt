package com.vintra.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vintra.app.ui.navigation.BottomTab

@Composable
fun AuthenticatedScaffold(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { VintraTopBar() },
        bottomBar = { VintraBottomBar(selectedTab = selectedTab, onTabSelected = onTabSelected) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}