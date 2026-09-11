package com.vintra.app.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vintra.app.domain.model.Comment
import com.vintra.app.domain.repository.CreateCommentResult
import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.ObserveCommentsResult
import com.vintra.app.domain.repository.ToggleLikeResult
import com.vintra.app.domain.usecase.auth.GetCurrentUserUseCase
import com.vintra.app.domain.usecase.comment.CreateCommentUseCase
import com.vintra.app.domain.usecase.comment.ObserveCommentsUseCase
import com.vintra.app.domain.usecase.post.GetPostByIdUseCase
import com.vintra.app.domain.usecase.post.ToggleLikeUseCase
import com.vintra.app.domain.usecase.profile.GetProfilePhotoUseCase
import com.vintra.app.domain.usecase.profile.GetProfileUseCase
import com.vintra.app.ui.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

private const val COMMENT_MAX_LENGTH = 500
private const val TEMP_MATCH_WINDOW_MS = 60_000L

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getProfilePhotoUseCase: GetProfilePhotoUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: String = savedStateHandle.toRoute<PostDetailRoute>().postId

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(currentUid = getCurrentUserUseCase()?.uid) }
        loadPost()
        observeComments()
    }

    fun onCommentTextChange(text: String) {
        _uiState.update { it.copy(commentText = text.take(COMMENT_MAX_LENGTH)) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun toggleLike() {
        val post = _uiState.value.post ?: return
        val uid = _uiState.value.currentUid ?: return
        val alreadyLiked = post.likedBy.contains(uid)

        val optimisticPost = if (alreadyLiked) {
            post.copy(likedBy = post.likedBy - uid, likeCount = (post.likeCount - 1).coerceAtLeast(0))
        } else {
            post.copy(likedBy = post.likedBy + uid, likeCount = post.likeCount + 1)
        }
        _uiState.update { it.copy(post = optimisticPost) }

        viewModelScope.launch {
            when (val result = toggleLikeUseCase(post.id, uid)) {
                is ToggleLikeResult.Success -> Unit
                is ToggleLikeResult.Error -> {
                    _uiState.update { it.copy(post = post, toastMessage = result.message) }
                }
            }
        }
    }

    fun sendComment() {
        val text = _uiState.value.commentText.trim()
        if (text.isBlank() || _uiState.value.isSendingComment) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingComment = true) }

            val user = getCurrentUserUseCase()
            if (user == null) {
                _uiState.update {
                    it.copy(
                        isSendingComment = false,
                        toastMessage = "Log in to comment."
                    )
                }
                return@launch
            }

            var authorName = ""
            var authorUsername = ""
            var isVerified = false
            when (val profileResult = getProfileUseCase(user.uid)) {
                is GetProfileResult.Success -> {
                    authorName = profileResult.profile?.name.orEmpty()
                    authorUsername = profileResult.profile?.username.orEmpty()
                    isVerified = profileResult.profile?.isVerified == true
                }
                is GetProfileResult.Error -> Unit
            }

            var authorPhotoBase64: String? = null
            when (val photoResult = getProfilePhotoUseCase(user.uid)) {
                is GetPhotoResult.Success -> authorPhotoBase64 = photoResult.base64
                is GetPhotoResult.Error -> Unit
            }

            val tempId = "temp_${System.currentTimeMillis()}"
            val optimisticComment = Comment(
                id = tempId,
                postId = postId,
                authorUid = user.uid,
                authorName = authorName,
                authorUsername = authorUsername,
                authorPhotoBase64 = authorPhotoBase64,
                text = text,
                createdAt = System.currentTimeMillis(),
                isVerified = isVerified
            )

            _uiState.update {
                it.copy(
                    isSendingComment = false,
                    commentText = "",
                    comments = it.comments + optimisticComment
                )
            }

            when (
                val result = createCommentUseCase(
                    postId = postId,
                    authorUid = user.uid,
                    authorName = authorName,
                    authorUsername = authorUsername,
                    authorPhotoBase64 = authorPhotoBase64,
                    text = text,
                    isVerified = isVerified
                )
            ) {
                is CreateCommentResult.Success -> {
                    _uiState.update { state ->
                        state.copy(comments = state.comments.filterNot { it.id == tempId })
                    }
                }
                is CreateCommentResult.Error -> {
                    _uiState.update {
                        it.copy(
                            comments = it.comments.filterNot { c -> c.id == tempId },
                            toastMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun loadPost() {
        viewModelScope.launch {
            val post = getPostByIdUseCase(postId)
            _uiState.update {
                it.copy(
                    isLoadingPost = false,
                    post = post,
                    errorMessage = if (post == null) "Post not found." else null
                )
            }
        }
    }

    private fun observeComments() {
        viewModelScope.launch {
            observeCommentsUseCase(postId).collectLatest { result ->
                when (result) {
                    is ObserveCommentsResult.Success -> {
                        _uiState.update { current ->
                            val pendingTemps = current.comments.filter { temp ->
                                temp.id.startsWith("temp_") &&
                                        result.comments.none { server ->
                                            server.authorUid == temp.authorUid &&
                                                    server.text == temp.text &&
                                                    abs(server.createdAt - temp.createdAt) < TEMP_MATCH_WINDOW_MS
                                        }
                            }

                            current.copy(
                                isLoadingComments = false,
                                comments = result.comments + pendingTemps,
                                errorMessage = null
                            )
                        }
                    }
                    is ObserveCommentsResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingComments = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}