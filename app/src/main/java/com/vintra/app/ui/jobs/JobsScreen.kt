package com.vintra.app.ui.jobs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.core.util.greetingForHour
import com.vintra.app.ui.components.AuthenticatedScaffold
import com.vintra.app.ui.components.HomeGreetingHeader
import com.vintra.app.ui.components.appTextFieldColors
import com.vintra.app.ui.home.HomeViewModel
import com.vintra.app.ui.home.components.EventBannerCarousel
import com.vintra.app.ui.jobs.components.JobCard
import com.vintra.app.ui.jobs.components.JobFeedTabs
import com.vintra.app.ui.navigation.BottomTab
import java.util.Calendar

@Composable
fun JobsScreen(
    onCreatePost: () -> Unit,
    onCreateJob: (() -> Unit)? = null,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onJobClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: JobsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val homeState by homeViewModel.uiState.collectAsState()
    val greeting = remember { greetingForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

    AuthenticatedScaffold(
        selectedTab = BottomTab.STATEMENT,
        onTabSelected = { tab ->
            if (tab == BottomTab.HOME) onNavigateToHome()
        },
        onCreatePost = onCreatePost,
        onCreateJob = onCreateJob,
        onProfileClick = onProfileClick,
        onLogout = onLogout,
        topBarTitle = "Jobs"
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                HomeGreetingHeader(
                    greeting = greeting,
                    firstName = homeState.firstName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EventBannerCarousel()
                }
            }

            item {
                JobFeedTabs(
                    selectedTab = state.selectedFeedTab,
                    onTabSelected = viewModel::onFeedTabSelected,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = appTextFieldColors(),
                        placeholder = { Text("Search jobs", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            when {
                state.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                state.visibleJobs.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (state.selectedFeedTab) {
                                    JobFeedTab.RECOMMENDED -> "No jobs yet."
                                    JobFeedTab.MY_PUBLIC -> "You haven't published any jobs yet."
                                },
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                else -> {
                    items(
                        items = state.visibleJobs,
                        key = { it.id }
                    ) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }
                }
            }
        }
    }
}