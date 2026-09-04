package com.vintra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vintra.app.core.connection.ConnectionStatus
import com.vintra.app.core.connection.ConnectionTestViewModel
import com.vintra.app.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        // TODO: Tirar essa tela de teste e colocar a Splash quando a infra inicial estiver 100% validada.
                        ConnectionTestScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionTestScreen(
    viewModel: ConnectionTestViewModel = hiltViewModel()
) {
    val status by viewModel.status.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.vintra),
            modifier = Modifier
                .size(96.dp)
                .padding(bottom = 16.dp),
            contentDescription = "Logo do App"
        )

        Text(
            text = "Vintra",
            style = MaterialTheme.typography.titleMedium
        )

        Column(modifier = Modifier.padding(top = 16.dp)) {
            when (val currentStatus = status) {
                is ConnectionStatus.Loading -> CircularProgressIndicator()
                is ConnectionStatus.Success -> Text(
                    text = "${currentStatus.message}",
                    style = MaterialTheme.typography.bodyMedium
                )
                is ConnectionStatus.Error -> Text(
                    text = "Erro: ${currentStatus.message}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
