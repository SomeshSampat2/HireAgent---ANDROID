package com.example.agenthire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agenthire.ui.components.HireAgentScreen
import com.example.agenthire.ui.theme.AgentHireTheme
import com.example.agenthire.viewmodel.HireAgentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgentHireTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }
                
                // API key is loaded from local.properties via BuildConfig
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                val viewModel: HireAgentViewModel = viewModel {
                    HireAgentViewModel(context, apiKey)
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    HireAgentScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}