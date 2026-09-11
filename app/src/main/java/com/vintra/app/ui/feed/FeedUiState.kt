package com.vintra.app.ui.feed

import android.net.Uri
import com.vintra.app.domain.model.Post

data class FeedUiState(
    val isLoadingFeed: Boolean = true,
    val posts: List<Post> = emptyList(),
    val currentUid: String? = null,
    val postTitle: String = "",
    val postText: String = "",
    val postLinkUrl: String = "",
    val postImageUri: Uri? = null,
    val isPosting: Boolean = false,
    val postPublished: Boolean = false,
    val toastMessage: String? = null
)