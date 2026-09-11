package com.vintra.app.ui.feed.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vintra.app.ui.components.appTextFieldColors

@Composable
fun PostComposer(
    title: String,
    text: String,
    linkUrl: String,
    imageUri: Uri?,
    isPosting: Boolean,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> onImagePicked(uri) }

    val previewBitmap = remember(imageUri) {
        imageUri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (exception: Exception) {
                null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Title (optional)", color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("What are you building today?", color = Color.Gray) },
            minLines = 8,
            maxLines = 14,
            shape = RoundedCornerShape(10.dp),
            colors = appTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = linkUrl,
            onValueChange = onLinkChange,
            placeholder = { Text("Link (GitHub, Linkedin)", color = Color.Gray) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri
            ),
            shape = RoundedCornerShape(10.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        if (previewBitmap != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    bitmap = previewBitmap,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                IconButton(
                    onClick = { onImagePicked(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove image", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add image",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSubmit,
                enabled = !isPosting && (title.isNotBlank() || text.isNotBlank() || linkUrl.isNotBlank() || imageUri != null),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (isPosting) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                } else {
                    Text("Post", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
