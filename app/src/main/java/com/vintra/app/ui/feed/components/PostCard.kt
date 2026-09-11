package com.vintra.app.ui.feed.components

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vintra.app.R
import com.vintra.app.core.util.formatPostTimestamp
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.Post
import com.vintra.app.ui.components.VerifiedBadge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PostCard(
    post: Post,
    isLikedByCurrentUser: Boolean = false,
    onLikeClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    textPreviewMaxChars: Int? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var avatarBitmap by remember(post.authorPhotoBase64) { mutableStateOf<ImageBitmap?>(null) }
    var postImageBitmap by remember(post.imageBase64) { mutableStateOf<ImageBitmap?>(null) }

    val maxChars = textPreviewMaxChars
    val isTruncated = maxChars != null && post.text.length > maxChars
    val displayText = if (isTruncated) {
        post.text.take(maxChars!!).trimEnd() + "..."
    } else {
        post.text
    }

    LaunchedEffect(post.authorPhotoBase64) {
        post.authorPhotoBase64?.let { base64 ->
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

    LaunchedEffect(post.imageBase64) {
        post.imageBase64?.let { base64 ->
            withContext(Dispatchers.IO) {
                try {
                    val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
                    postImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } catch (_: Exception) {
                    postImageBitmap = null
                }
            }
        } ?: run { postImageBitmap = null }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                val currentAvatar = avatarBitmap
                if (currentAvatar != null) {
                    Image(
                        bitmap = currentAvatar,
                        contentDescription = "Author avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = post.authorName.take(1).uppercase().ifEmpty { "?" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.15f))
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName.ifBlank { "Unknown" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(size = 16.dp)
                        }
                    }
                    if (post.authorUsername.isNotBlank()) {
                        Text(
                            text = "@${post.authorUsername}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }
                ProviderBadge(provider = post.authorProvider)
            }

            if (post.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (post.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayText,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                if (isTruncated) {
                    Text(
                        text = "veja mais",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable(enabled = onClick != null) {
                                onClick?.invoke()
                            }
                    )
                }
            }

            if (post.linkUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = post.linkUrl,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(post.linkUrl))
                        )
                    }
                )
            }

            val currentPostImage = postImageBitmap
            if (currentPostImage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    bitmap = currentPostImage,
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Created ${formatPostTimestamp(post.createdAt)}",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = onLikeClick != null) {
                        onLikeClick?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (isLikedByCurrentUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.White.copy(alpha = 0.55f)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likeCount}",
                        color = if (isLikedByCurrentUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.White.copy(alpha = 0.55f)
                        },
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = onClick != null) {
                        onClick?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comments",
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentCount}",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderBadge(provider: AuthProvider) {
    when (provider) {
        AuthProvider.GITHUB -> {
            Icon(
                painter = painterResource(id = R.drawable.github),
                contentDescription = "GitHub Provider",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        AuthProvider.GOOGLE -> {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Provider",
                modifier = Modifier.size(18.dp)
            )
        }
        else -> {}
    }
}