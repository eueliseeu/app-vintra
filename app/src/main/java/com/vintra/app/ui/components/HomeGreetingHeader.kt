package com.vintra.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vintra.app.core.util.formatCurrencyParts

@Composable
fun HomeGreetingHeader(
    greeting: String,
    firstName: String,
    amountCents: Long,
    modifier: Modifier = Modifier
) {
    val amount = formatCurrencyParts(amountCents)

    Column(modifier = modifier) {
        Text(
            text = "$greeting, $firstName",
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = "Controlled by ",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "You",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = amount.integerPart,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Text(
                text = ",${amount.centsPart}",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        }
    }
}