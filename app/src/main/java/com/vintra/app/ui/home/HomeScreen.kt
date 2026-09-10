package com.vintra.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.core.util.greetingForHour
import com.vintra.app.ui.components.AuthenticatedScaffold
import com.vintra.app.ui.components.HomeGreetingHeader
import com.vintra.app.ui.navigation.BottomTab
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedTab by remember {
        mutableStateOf(BottomTab.HOME)
    }

    val uiState by viewModel.uiState.collectAsState()

    val greeting = remember {
        greetingForHour(
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        )
    }

    AuthenticatedScaffold(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            selectedTab = tab
        }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        top = 12.dp,
                        start = 20.dp,
                        end = 20.dp
                    ),
                horizontalAlignment = Alignment.Start
            ) {
                HomeGreetingHeader(
                    greeting = greeting,
                    firstName = uiState.firstName,
                    amountCents = uiState.amountCents
                )
            }
        }
    }
}