package com.vintra.app.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.ui.components.CenterToast
import com.vintra.app.ui.components.VintraTopBar
import com.vintra.app.ui.components.VintraTopBarViewModel
import com.vintra.app.ui.feed.components.PostComposer
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L

@Composable
fun CreatePostScreen(
    onClose: () -> Unit,
    onPostSuccess: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
    topBarViewModel: VintraTopBarViewModel = hiltViewModel()
) {
    val feedState by viewModel.uiState.collectAsState()
    val topBarState by topBarViewModel.uiState.collectAsState()

    LaunchedEffect(feedState.postPublished) {
        if (feedState.postPublished) {
            viewModel.consumePostPublished()
            onPostSuccess()
        }
    }

    LaunchedEffect(feedState.toastMessage) {
        if (feedState.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            VintraTopBar(
                photoBase64 = topBarState.photoBase64,
                isUploadingPhoto = topBarState.isUploadingPhoto,
                onPhotoPicked = topBarViewModel::onPhotoPicked,
                onProfileClick = onProfileClick,
                onLogout = {
                    topBarViewModel.logout()
                    onLogout()
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "To go back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "New Post",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            PostComposer(
                title = feedState.postTitle,
                text = feedState.postText,
                linkUrl = feedState.postLinkUrl,
                imageUri = feedState.postImageUri,
                isPosting = feedState.isPosting,
                onTitleChange = viewModel::onPostTitleChange,
                onTextChange = viewModel::onPostTextChange,
                onLinkChange = viewModel::onPostLinkChange,
                onImagePicked = viewModel::onPostImagePicked,
                onSubmit = viewModel::publishPost
            )
        }

        CenterToast(message = feedState.toastMessage)
    }
}