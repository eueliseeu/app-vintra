package com.vintra.app.ui.feed

import com.vintra.app.domain.model.Comment
import com.vintra.app.domain.model.Post

data class PostDetailUiState(
    val isLoadingPost: Boolean = true,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val isLoadingComments: Boolean = true,
    val commentText: String = "",
    val isSendingComment: Boolean = false,
    val toastMessage: String? = null,
    val errorMessage: String? = null
)