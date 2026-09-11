package com.vintra.app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vintra.app.R
import com.vintra.app.ui.theme.titleColor

private val TOP_BAR_LOGO_SIZE = 60.dp
private val TOP_BAR_AVATAR_SIZE = 40.dp

@Composable
fun VintraTopBar(
    photoBase64: String?,
    isUploadingPhoto: Boolean,
    username: String,
    isVerified: Boolean,
    onPhotoPicked: (Uri) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onPhotoPicked) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            // Ajuste o top para subir/descer o TopBar (ex: 16.dp, 24.dp, 32.dp)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.vintra),
            contentDescription = "Vintra logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(TOP_BAR_LOGO_SIZE)
        )
        Text(
            text = "Global",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = titleColor
        )
        Box {
            ProfileAvatar(
                base64 = photoBase64,
                isUploading = isUploadingPhoto,
                size = TOP_BAR_AVATAR_SIZE,
                onClick = { isMenuExpanded = true }
            )

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFF131313)
            ) {
                if (username.isNotBlank()) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@$username",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                if (isVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    VerifiedBadge(size = 16.dp)
                                }
                            }
                        },
                        onClick = {},
                        enabled = false
                    )
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                DropdownMenuItem(
                    text = { Text("Profile", color = Color.White) },
                    onClick = {
                        isMenuExpanded = false
                        onProfileClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Change photo", color = Color.White) },
                    enabled = !isUploadingPhoto,
                    onClick = {
                        isMenuExpanded = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout", color = Color.White) },
                    onClick = {
                        isMenuExpanded = false
                        onLogout()
                    }
                )
            }
        }
    }
}