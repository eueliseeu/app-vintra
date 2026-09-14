package com.vintra.app.ui.feed

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.repository.CreatePostResult
import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.UpdatePostResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.post.CreatePostUseCase
import com.vintra.app.domain.usecase.post.GetPostByIdUseCase
import com.vintra.app.domain.usecase.post.UpdatePostUseCase
import com.vintra.app.domain.usecase.profile.GetProfilePhotoUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import com.vintra.app.ui.navigation.CreatePostRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POST_TITLE_MAX_LENGTH = 100
private const val POST_TEXT_MAX_LENGTH = 2000
private const val POST_LINK_MAX_LENGTH = 500

data class CreatePostUiState(
    val postId: String? = null,
    val isEditMode: Boolean = false,
    val postTitle: String = "",
    val postText: String = "",
    val postLinkUrl: String = "",
    val postImageUri: Uri? = null,
    val existingImageBase64: String? = null,
    val isPosting: Boolean = false,
    val postPublished: Boolean = false,
    val toastMessage: String? = null
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getProfilePhotoUseCase: GetProfilePhotoUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val routePostId: String =
        savedStateHandle.toRoute<CreatePostRoute>().postId.trim()

    private val _uiState = MutableStateFlow(
        CreatePostUiState(
            postId = routePostId.ifBlank { null },
            isEditMode = routePostId.isNotBlank()
        )
    )
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    init {
        if (routePostId.isNotBlank()) loadForEdit(routePostId)
    }

    private fun loadForEdit(postId: String) {
        viewModelScope.launch {
            val post = getPostByIdUseCase(postId)
            if (post == null) {
                _uiState.update { it.copy(toastMessage = "Post not found.") }
                return@launch
            }
            val uid = getCurrentUserUseCase()?.uid
            if (uid == null || post.authorUid != uid) {
                _uiState.update {
                    it.copy(toastMessage = "You can only edit your own posts.")
                }
                return@launch
            }
            _uiState.update {
                it.copy(
                    postId = post.id,
                    isEditMode = true,
                    postTitle = post.title,
                    postText = post.text,
                    postLinkUrl = post.linkUrl,
                    existingImageBase64 = post.imageBase64
                )
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

    fun publishPost() {
        val state = _uiState.value
        val uid = getCurrentUserUseCase()?.uid ?: return

        if (
            state.postTitle.isBlank() &&
            state.postText.isBlank() &&
            state.postLinkUrl.isBlank() &&
            state.postImageUri == null &&
            state.existingImageBase64 == null
        ) {
            _uiState.update {
                it.copy(
                    toastMessage = "Add text, a title, a link, or an image before publishing."
                )
            }
            return
        }

        val normalizedLink = state.postLinkUrl.trim()
        if (
            normalizedLink.isNotBlank() &&
            !normalizedLink.startsWith("https://") &&
            !normalizedLink.startsWith("http://")
        ) {
            _uiState.update {
                it.copy(toastMessage = "The link must begin with http:// or https://.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }

            if (state.isEditMode && !state.postId.isNullOrBlank()) {
                when (
                    val result = updatePostUseCase(
                        postId = state.postId,
                        authorUid = uid,
                        title = state.postTitle,
                        text = state.postText,
                        linkUrl = normalizedLink,
                        imageUri = state.postImageUri,
                        keepExistingImageBase64 = state.existingImageBase64
                    )
                ) {
                    is UpdatePostResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isPosting = false,
                                postPublished = true,
                                toastMessage = "Post updated."
                            )
                        }
                    }
                    is UpdatePostResult.Error -> {
                        _uiState.update {
                            it.copy(isPosting = false, toastMessage = result.message)
                        }
                    }
                }
                return@launch
            }

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
                        it.copy(
                            isPosting = false,
                            postTitle = "",
                            postText = "",
                            postLinkUrl = "",
                            postImageUri = null,
                            postPublished = true,
                            toastMessage = "Post published."
                        )
                    }
                }
                is CreatePostResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isPosting = false,
                            toastMessage = "Error publishing post. Please try again."
                        )
                    }
                }
            }
        }
    }
}