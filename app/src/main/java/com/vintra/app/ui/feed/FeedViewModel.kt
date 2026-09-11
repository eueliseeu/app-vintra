package com.vintra.app.ui.feed

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.repository.CreatePostResult
import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.ObservePostsResult
import com.vintra.app.domain.repository.ToggleLikeResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.post.CreatePostUseCase
import com.vintra.app.domain.usecase.post.ObserveFeedUseCase
import com.vintra.app.domain.usecase.post.ToggleLikeUseCase
import com.vintra.app.domain.usecase.profile.GetProfilePhotoUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POST_TITLE_MAX_LENGTH = 100
private const val POST_TEXT_MAX_LENGTH = 2000
private const val POST_LINK_MAX_LENGTH = 500

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getProfilePhotoUseCase: GetProfilePhotoUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val observeFeedUseCase: ObserveFeedUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(currentUid = getCurrentUserUseCase()?.uid) }
        observeFeed()
    }

    private fun observeFeed() {
        viewModelScope.launch {
            observeFeedUseCase().collectLatest { result ->
                when (result) {
                    is ObservePostsResult.Success -> {
                        _uiState.update { it.copy(isLoadingFeed = false, posts = result.posts) }
                    }
                    is ObservePostsResult.Error -> {
                        _uiState.update {
                            it.copy(isLoadingFeed = false, toastMessage = "Error loading feed. Please try again.")
                        }
                    }
                }
            }
        }
    }

    fun onPostTitleChange(value: String) {
        if (value.length <= POST_TITLE_MAX_LENGTH) {
            _uiState.update { it.copy(postTitle = value) }
        }
    }

    fun onPostTextChange(value: String) {
        if (value.length <= POST_TEXT_MAX_LENGTH) {
            _uiState.update { it.copy(postText = value) }
        }
    }

    fun onPostLinkChange(value: String) {
        if (value.length <= POST_LINK_MAX_LENGTH) {
            _uiState.update { it.copy(postLinkUrl = value) }
        }
    }

    fun onPostImagePicked(uri: Uri?) {
        _uiState.update { it.copy(postImageUri = uri) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun consumePostPublished() {
        _uiState.update { it.copy(postPublished = false) }
    }

    fun toggleLike(postId: String) {
        val uid = _uiState.value.currentUid ?: return

        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.id != postId) return@map post
                    val alreadyLiked = post.likedBy.contains(uid)
                    if (alreadyLiked) {
                        post.copy(likedBy = post.likedBy - uid, likeCount = (post.likeCount - 1).coerceAtLeast(0))
                    } else {
                        post.copy(likedBy = post.likedBy + uid, likeCount = post.likeCount + 1)
                    }
                }
            )
        }

        viewModelScope.launch {
            when (val result = toggleLikeUseCase(postId, uid)) {
                is ToggleLikeResult.Success -> Unit
                is ToggleLikeResult.Error -> {
                    _uiState.update { it.copy(toastMessage = result.message) }
                }
            }
        }
    }

    fun publishPost() {
        val state = _uiState.value
        val uid = getCurrentUserUseCase()?.uid ?: return

        if (state.postTitle.isBlank() && state.postText.isBlank() && state.postLinkUrl.isBlank() && state.postImageUri == null) {
            _uiState.update { it.copy(toastMessage = "Add text, a title, a link, or an image before publishing.") }
            return
        }

        val normalizedLink = state.postLinkUrl.trim()
        if (normalizedLink.isNotBlank() && !normalizedLink.startsWith("https://") && !normalizedLink.startsWith("http://")) {
            _uiState.update { it.copy(toastMessage = "The link must begin with http:// or https://.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }

            val provider = getCurrentUserUseCase()?.provider ?: AuthProvider.UNKNOWN

            var authorName = ""
            var authorUsername = ""
            var isVerified = false
            when (val profileResult = getProfileUseCase(uid)) {
                is GetProfileResult.Success -> {
                    authorName = profileResult.profile?.name.orEmpty()
                    authorUsername = profileResult.profile?.username.orEmpty()
                    isVerified = profileResult.profile?.isVerified == true
                }
                is GetProfileResult.Error -> Unit
            }

            var authorPhotoBase64: String? = null
            when (val photoResult = getProfilePhotoUseCase(uid)) {
                is GetPhotoResult.Success -> authorPhotoBase64 = photoResult.base64
                is GetPhotoResult.Error -> Unit
            }

            when (
                val result = createPostUseCase(
                    authorUid = uid,
                    authorName = authorName,
                    authorUsername = authorUsername,
                    authorPhotoBase64 = authorPhotoBase64,
                    authorProvider = provider,
                    title = state.postTitle.trim(),
                    text = state.postText.trim(),
                    linkUrl = normalizedLink,
                    imageUri = state.postImageUri,
                    isVerified = isVerified
                )
            ) {
                is CreatePostResult.Success -> {
                    _uiState.update {
                        it.copy(isPosting = false, postTitle = "", postText = "", postLinkUrl = "", postImageUri = null, postPublished = true)
                    }
                }
                is CreatePostResult.Error -> {
                    _uiState.update {
                        it.copy(isPosting = false, toastMessage = "Error publishing post. Please try again.")
                    }
                }
            }
        }
    }
}