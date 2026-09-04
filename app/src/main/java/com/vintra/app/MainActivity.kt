package com.vintra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vintra.app.ui.auth.LoginScreen
import com.vintra.app.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // TODO: navegar para a Home quando o Navigation Compose for adicionado
                    }
                )
            }
        }
    }
}