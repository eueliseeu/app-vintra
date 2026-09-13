package com.vintra.app.ui.jobs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.domain.model.JobTag
import com.vintra.app.ui.components.CenterToast
import com.vintra.app.ui.components.VintraTopBar
import com.vintra.app.ui.components.VintraTopBarViewModel
import com.vintra.app.ui.components.appTextFieldColors
import com.vintra.app.ui.theme.ButtonSave
import com.vintra.app.ui.theme.FieldBackground
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateJobScreen(
    onClose: () -> Unit,
    onSuccess: () -> Unit,
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: CreateJobViewModel = hiltViewModel(),
    topBarViewModel: VintraTopBarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val topBarState by topBarViewModel.uiState.collectAsState()
    val isEdit = state.isEditMode

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::onCompanyPhotoPicked) }

    LaunchedEffect(state.published) {
        if (state.published) {
            viewModel.consumePublished()
            onSuccess()
        }
    }

    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = if (isEdit) "Edit Job" else "New Job",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                FieldLabel("Company name")
                TextField(
                    value = state.companyName,
                    onValueChange = viewModel::onCompanyNameChange,
                    enabled = !isEdit,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("Company name", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Company username")
                TextField(
                    value = state.companyUsername,
                    onValueChange = viewModel::onCompanyUsernameChange,
                    enabled = !isEdit,
                    singleLine = true,
                    leadingIcon = { Text("@", color = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("linkedinoficial", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Company photo")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(FieldBackground, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Company photo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when {
                            state.companyPhotoUri != null -> "New photo selected"
                            state.existingPhotoBase64 != null -> "Current photo kept"
                            else -> "Optional"
                        },
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Job title")
                TextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("Web Application Developer", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Description")
                TextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("Describe the role...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Link (required)")
                TextField(
                    value = state.linkUrl,
                    onValueChange = viewModel::onLinkUrlChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("https://...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Button label")
                TextField(
                    value = state.buttonLabel,
                    onValueChange = viewModel::onButtonLabelChange,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = appTextFieldColors(),
                    placeholder = { Text("Open", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldLabel("Tags")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JobTag.entries.forEach { tag ->
                        val selected = tag in state.selectedTags
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.toggleTag(tag) },
                            label = { Text(tag.label, fontSize = 13.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = FieldBackground,
                                labelColor = Color.White.copy(alpha = 0.7f),
                                selectedContainerColor = ButtonSave.copy(alpha = 0.25f),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = viewModel::publish,
                    enabled = !state.isPublishing,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonSave,
                        contentColor = Color.Black,
                        disabledContainerColor = ButtonSave.copy(alpha = 0.4f),
                        disabledContentColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isPublishing) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (isEdit) "Update job" else "Publish job",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        CenterToast(message = state.toastMessage)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 13.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
}