package com.vintra.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.R
import com.vintra.app.core.util.formatDate
import com.vintra.app.domain.model.ProfileEditability
import com.vintra.app.ui.components.CenterToast
import com.vintra.app.ui.components.appTextFieldColors
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2500L
private val EXTRA_TOP_SPACING = 40.dp

@Composable
fun ProfileSetupScreen(
    onSaved: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSaved) {
        onSaved()
        return
    }

    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            viewModel.clearToast()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val isLocked = uiState.editability is ProfileEditability.Locked

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
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 24.dp + EXTRA_TOP_SPACING, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.vintra),
                    contentDescription = "Vintra logo",
                    modifier = Modifier.height(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vintra",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            val warningText = if (isLocked) {
                val lockedUntil = (uiState.editability as ProfileEditability.Locked).editableAtMillis
                "Your information can only be edited again on ${formatDate(lockedUntil)}."
            } else {
                "Your information can only be edited again after 90 days."
            }
            Text(
                text = warningText,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1.5f)) {
                    Text("Your name", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        enabled = !isLocked,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = appTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = uiState.birthDateText,
                        onValueChange = viewModel::onBirthDateTextChange,
                        enabled = !isLocked,
                        singleLine = true,
                        placeholder = { Text("dd/mm/yyyy", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        colors = appTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("username", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                enabled = !isLocked,
                singleLine = true,
                leadingIcon = { Text("@", color = Color.Gray) },
                shape = RoundedCornerShape(10.dp),
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            UsernameStatusLabel(status = uiState.usernameStatus)

            Spacer(modifier = Modifier.height(20.dp))

            Text("Your e-mail", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = uiState.email,
                onValueChange = {},
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Your nationality", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = uiState.nationality,
                onValueChange = {},
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::save,
                enabled = !isLocked && !uiState.isSaving,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text(text = "Save information", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        CenterToast(message = uiState.toastMessage)
    }
}

@Composable
private fun UsernameStatusLabel(status: UsernameCheckStatus) {
    val labelData = when (status) {
        UsernameCheckStatus.IDLE -> null
        UsernameCheckStatus.CHECKING -> "Checking..." to Color.Gray
        UsernameCheckStatus.AVAILABLE -> "Available" to Color(0xFF4CAF50)
        UsernameCheckStatus.TAKEN -> "Username already taken" to MaterialTheme.colorScheme.error
        UsernameCheckStatus.INVALID -> "Use only letters and numbers (3-20 characters)" to MaterialTheme.colorScheme.error
    }
    if (labelData != null) {
        val (text, color) = labelData
        Text(text = text, color = color, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}