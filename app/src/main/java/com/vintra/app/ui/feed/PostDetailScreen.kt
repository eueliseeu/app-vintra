package com.vintra.app.ui.feed

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.core.util.formatPostTimestamp
import com.vintra.app.domain.model.Comment
import com.vintra.app.ui.components.CenterToast
import com.vintra.app.ui.components.VerifiedBadge
import com.vintra.app.ui.feed.components.PostCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TOAST_DURATION_MS = 2500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            viewModel.clearToast()
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Post",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "To go back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            when {
                state.isLoadingPost -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                state.post == null -> {
                    Text(
                        text = state.errorMessage ?: "Post not found.",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            PostCard(post = state.post!!)
                        }

                        item {
                            CommentInputBar(
                                text = state.commentText,
                                isSending = state.isSendingComment,
                                onTextChange = viewModel::onCommentTextChange,
                                onSend = viewModel::sendComment
                            )
                        }

                        item {
                            Text(
                                text = "Comments (${state.comments.size})",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }

                        if (state.errorMessage != null) {
                            item {
                                Text(
                                    text = "Erro: ${state.errorMessage}",
                                    color = Color.Red.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // Lista de comentários
                        if (state.isLoadingComments) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        } else if (state.comments.isEmpty()) {
                            item {
                                Text(
                                    text = "No comments yet. Be the first!",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        } else {
                            items(
                                items = state.comments,
                                key = { it.id }
                            ) { comment ->
                                CommentItem(comment = comment)
                            }
                        }
                    }
                }
            }

            CenterToast(message = state.toastMessage)
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    var avatarBitmap by remember(comment.authorPhotoBase64) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(comment.authorPhotoBase64) {
        comment.authorPhotoBase64?.let { base64 ->
            withContext(Dispatchers.IO) {
                try {
                    val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
                    avatarBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } catch (_: Exception) {
                    avatarBitmap = null
                }
            }
        } ?: run { avatarBitmap = null }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            val currentAvatar = avatarBitmap
            if (currentAvatar != null) {
                Image(
                    bitmap = currentAvatar,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = comment.authorName.take(1).uppercase().ifEmpty { "?" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName.ifBlank { "Unknown" },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                if (comment.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedBadge(size = 14.dp)
                }
                if (comment.authorUsername.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${comment.authorUsername}",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.text,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatPostTimestamp(comment.createdAt),
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CommentInputBar(
    text: String,
    isSending: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp),
            placeholder = {
                Text(
                    text = "Write a comment...",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            },
            maxLines = 4,
            singleLine = false,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                focusedBorderColor = Color.White.copy(alpha = 0.15f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            onClick = onSend,
            enabled = text.isNotBlank() && !isSending,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (text.isNotBlank() && !isSending)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.White.copy(alpha = 0.08f)
                )
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Submit",
                    tint = if (text.isNotBlank()) Color.White else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}