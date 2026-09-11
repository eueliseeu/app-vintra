package com.vintra.app.ui.jobs.components

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vintra.app.core.util.formatPostTimestamp
import com.vintra.app.domain.model.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun JobCard(
    job: Job,
    onClick: () -> Unit,
    descriptionMaxChars: Int? = 180,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var avatarBitmap by remember(job.companyPhotoBase64) { mutableStateOf<ImageBitmap?>(null) }

    val isTruncated = descriptionMaxChars != null && job.description.length > descriptionMaxChars
    val displayDescription = if (isTruncated) {
        job.description.take(descriptionMaxChars!!).trimEnd() + "..."
    } else {
        job.description
    }

    LaunchedEffect(job.companyPhotoBase64) {
        job.companyPhotoBase64?.let { base64 ->
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                val bmp = avatarBitmap
                if (bmp != null) {
                    Image(
                        bitmap = bmp,
                        contentDescription = "Company logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = job.companyName.take(1).uppercase().ifEmpty { "?" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.companyName.ifBlank { "Company" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (job.companyUsername.isNotBlank()) {
                    Text(
                        text = "@${job.companyUsername}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = job.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = displayDescription,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        if (job.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                job.tags.forEach { tag ->
                    Text(
                        text = tag.label,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(job.linkUrl))
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D9BF0),
                    contentColor = Color.White
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(
                    text = job.buttonLabel.ifBlank { "Open" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Post ${formatPostTimestamp(job.createdAt)}",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}