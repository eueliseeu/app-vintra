package com.vintra.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vintra.app.ui.components.AuthenticatedScaffold
import com.vintra.app.ui.navigation.BottomTab

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    AuthenticatedScaffold(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            if (tab == BottomTab.HOME) {
                selectedTab = tab
            }
        }
    ) {

    }
}