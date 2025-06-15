package com.example.agenthire.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agenthire.data.models.AnalysisState
import com.example.agenthire.viewmodel.AnalysisStep
import com.example.agenthire.viewmodel.HireAgentUiState
import com.example.agenthire.viewmodel.HireAgentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HireAgentScreen(
    viewModel: HireAgentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val analysisState by viewModel.analysisState.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "HireAgent",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AI-Powered Hiring Assistant",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            actions = {
                if (uiState.currentStep == AnalysisStep.RESULTS) {
                    IconButton(onClick = { viewModel.startNewAnalysis() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "New Analysis",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )
        
        // Main Content
        when (uiState.currentStep) {
            AnalysisStep.INPUT -> {
                InputStep(
                    uiState = uiState,
                    onResumeSelected = viewModel::onResumeSelected,
                    onJobDescriptionChanged = viewModel::onJobDescriptionChanged,
                    onStartAnalysis = viewModel::startAnalysis,
                    onClearResume = viewModel::clearResume,
                    onClearJobDescription = viewModel::clearJobDescription,
                    canStartAnalysis = viewModel.canStartAnalysis(),
                    modifier = Modifier.weight(1f)
                )
            }
            AnalysisStep.ANALYZING -> {
                AnalyzingStep(
                    progressMessage = analysisState.progressMessage,
                    modifier = Modifier.weight(1f)
                )
            }
            AnalysisStep.RESULTS -> {
                analysisState.analysisResult?.let { result ->
                    ResultsStep(
                        analysisResult = result,
                        onNewAnalysis = viewModel::startNewAnalysis,
                        modifier = Modifier.weight(1f)
                    )
                } ?: run {
                    analysisState.error?.let { errorMessage ->
                        ErrorStep(
                            error = errorMessage,
                            onRetry = { viewModel.startNewAnalysis() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputStep(
    uiState: HireAgentUiState,
    onResumeSelected: (Uri) -> Unit,
    onJobDescriptionChanged: (String) -> Unit,
    onStartAnalysis: () -> Unit,
    onClearResume: () -> Unit,
    onClearJobDescription: () -> Unit,
    canStartAnalysis: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onResumeSelected(it) }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress Indicator
        StepProgressIndicator(
            currentStep = 1,
            totalSteps = 3,
            stepTitles = listOf("Input", "Analysis", "Results")
        )
        
        // Resume Upload Section
        ResumeUploadCard(
            uiState = uiState,
            onSelectFile = { filePickerLauncher.launch("*/*") },
            onClearFile = onClearResume
        )
        
        // Job Description Section
        JobDescriptionCard(
            uiState = uiState,
            onJobDescriptionChanged = onJobDescriptionChanged,
            onClear = onClearJobDescription
        )
        
        // Analysis Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (canStartAnalysis) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (canStartAnalysis) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                Text(
                    text = "Ready for Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canStartAnalysis) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                Text(
                    text = if (canStartAnalysis) {
                        "All requirements met. Start comprehensive candidate analysis."
                    } else {
                        "Please upload a resume and provide a detailed job description to continue."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (canStartAnalysis) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Button(
                    onClick = onStartAnalysis,
                    enabled = canStartAnalysis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Start AI Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        
        // Requirements Section
        RequirementsCard()
    }
}

@Composable
private fun AnalyzingStep(
    progressMessage: String = "",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Progress Indicator
                StepProgressIndicator(
                    currentStep = 2,
                    totalSteps = 3,
                    stepTitles = listOf("Input", "Analysis", "Results")
                )
                
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "AI Analysis in Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                if (progressMessage.isNotEmpty()) {
                    Text(
                        text = progressMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Our AI is analyzing the candidate's qualifications, experience, and fit for the role. This may take a few moments.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Analysis Steps
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalysisStepItem("Extracting text from resume", progressMessage.contains("Extracting"))
                    AnalysisStepItem("Analyzing resume structure", progressMessage.contains("resume structure"))
                    AnalysisStepItem("Analyzing job requirements", progressMessage.contains("job requirements"))
                    AnalysisStepItem("Matching candidate to job", progressMessage.contains("Matching"))
                }
            }
        }
    }
}

@Composable
private fun AnalysisStepItem(
    text: String,
    completed: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            if (completed) Icons.Default.CheckCircle else Icons.Default.Schedule,
            contentDescription = null,
            tint = if (completed) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (completed) 
                MaterialTheme.colorScheme.onSurface 
            else 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ErrorStep(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "Analysis Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Try Again")
                }
            }
        }
    }
} 