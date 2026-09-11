package com.vintra.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vintra.app.ui.components.CenterToast
import com.vintra.app.ui.components.HomeGreetingHeader
import com.vintra.app.ui.feed.FeedViewModel
import com.vintra.app.ui.feed.components.PostCard
import com.vintra.app.ui.home.components.EventBannerCarousel
import com.vintra.app.ui.navigation.BottomTab
import kotlinx.coroutines.delay
import java.util.Calendar

private const val TOAST_DURATION_MS = 2500L

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onCreatePost: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onPostClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    val uiState by viewModel.uiState.collectAsState()
    val feedState by feedViewModel.uiState.collectAsState()
    val greeting = remember { greetingForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

    LaunchedEffect(feedState.toastMessage) {
        if (feedState.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            feedViewModel.clearToast()
        }
    }

    AuthenticatedScaffold(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onCreatePost = onCreatePost,
        onProfileClick = onProfileClick,
        onLogout = onLogout
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            HomeGreetingHeader(
                                greeting = greeting,
                                firstName = uiState.firstName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EventBannerCarousel()
                            }
                        }
                    }

                    if (feedState.isLoadingFeed) {
                        item { CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp)) }
                    } else {
                        items(
                            items = feedState.posts,
                            key = { it.id }
                        ) { post ->
                            PostCard(
                                post = post,
                                isLikedByCurrentUser = feedState.currentUid != null &&
                                        post.likedBy.contains(feedState.currentUid),
                                onLikeClick = { feedViewModel.toggleLike(post.id) },
                                onClick = { onPostClick(post.id) }
                            )
                        }
                    }
                }
            }
            CenterToast(message = feedState.toastMessage)
        }
    }
}