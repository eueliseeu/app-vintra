package com.vintra.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CreateButtonShape = RoundedCornerShape(5.dp)

@Composable
fun FloatingCreateButton(
    onCreatePost: () -> Unit,
    onCreateJob: (() -> Unit)? = null,
    canCreateJob: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CreateButtonShape)
                .background(Color.White)
                .border(width = 1.5.dp, color = Color.Black, shape = CreateButtonShape)
                .clickable {
                    if (canCreateJob && onCreateJob != null) {
                        expanded = true
                    } else {
                        onCreatePost()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Create",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color(0xFF131313)
        ) {
            DropdownMenuItem(
                text = { Text("New Post", color = Color.White) },
                onClick = {
                    expanded = false
                    onCreatePost()
                }
            )
            if (canCreateJob && onCreateJob != null) {
                DropdownMenuItem(
                    text = { Text("New Jobs", color = Color.White) },
                    onClick = {
                        expanded = false
                        onCreateJob()
                    }
                )
            }
        }
    }
}