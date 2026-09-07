package com.vintra.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

private val CONTENT_HORIZONTAL_PADDING = 24.dp
private val HEADER_TOP_PADDING = 32.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    val uiState by viewModel.uiState.collectAsState()
    val greeting = remember { greetingForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

    AuthenticatedScaffold(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            if (tab == BottomTab.HOME) {
                selectedTab = tab
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                HomeGreetingHeader(
                    greeting = greeting,
                    firstName = uiState.firstName,
                    amountCents = uiState.amountCents,
                    modifier = Modifier.padding(
                        start = CONTENT_HORIZONTAL_PADDING,
                        end = CONTENT_HORIZONTAL_PADDING,
                        top = HEADER_TOP_PADDING
                    )
                )
            }
        }
    }
}