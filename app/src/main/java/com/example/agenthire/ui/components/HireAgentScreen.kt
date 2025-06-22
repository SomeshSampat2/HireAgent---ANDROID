package com.example.agenthire.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agenthire.data.models.*
import com.example.agenthire.viewmodel.*
import com.example.agenthire.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HireAgentScreen(
    viewModel: HireAgentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val batchAnalysisState by viewModel.batchAnalysisState.collectAsStateWithLifecycle()
    
    // Modern gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern App Bar with Gradient
            ModernTopAppBar(
                currentStep = uiState.currentStep,
                onNewAnalysis = { viewModel.startNewAnalysis() }
            )
            
            // Animated Content Transition
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (targetState.ordinal > initialState.ordinal) 300 else -300 },
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + fadeIn(animationSpec = tween(400)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { if (targetState.ordinal > initialState.ordinal) -300 else 300 },
                        animationSpec = tween(400, easing = EaseInCubic)
                    ) + fadeOut(animationSpec = tween(400))
                },
                modifier = Modifier.weight(1f),
                label = "screen_transition"
            ) { step ->
                when (step) {
                    MultiAnalysisStep.INPUT -> {
                        SlideInContent(visible = true) {
                            MultiResumeInputStep(
                                uiState = uiState,
                                onResumesSelected = viewModel::onResumesSelected,
                                onJobDescriptionChanged = viewModel::onJobDescriptionChanged,
                                onStartAnalysis = viewModel::startBatchAnalysis,
                                onClearResumes = viewModel::clearAllResumes,
                                onClearJobDescription = viewModel::clearJobDescription,
                                onRemoveResumeFile = viewModel::removeResumeFile,
                                canStartAnalysis = uiState.resumesValidated && 
                                                 uiState.jobDescriptionValidated && 
                                                 !batchAnalysisState.isAnalyzing &&
                                                 uiState.selectedResumeFiles.isNotEmpty(),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    MultiAnalysisStep.ANALYZING -> {
                        SlideInContent(visible = true) {
                            ModernBatchAnalyzingStep(
                                batchAnalysisState = batchAnalysisState,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    MultiAnalysisStep.RANKED_RESULTS -> {
                        batchAnalysisState.rankedResults.let { results ->
                            SlideInContent(visible = true) {
                                RankedResultsScreen(
                                    rankedCandidates = results,
                                    onCandidateClick = viewModel::showCandidateDetail,
                                    onNewAnalysis = viewModel::startNewAnalysis,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    MultiAnalysisStep.DETAILED_VIEW -> {
                        uiState.selectedCandidate?.let { candidate ->
                            SlideInContent(visible = true) {
                                CandidateDetailScreen(
                                    rankedCandidate = candidate,
                                    onBack = viewModel::backToRankedResults,
                                    onNewAnalysis = viewModel::startNewAnalysis,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } ?: run {
                            batchAnalysisState.error?.let { errorMessage ->
                                SlideInContent(visible = true) {
                                    ModernErrorStep(
                                        error = errorMessage,
                                        onRetry = { viewModel.startNewAnalysis() },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    currentStep: MultiAnalysisStep,
    onNewAnalysis: () -> Unit
) {
    // Gradient background for app bar
    val appBarGradient = Brush.horizontalGradient(
        colors = listOf(Primary60, Secondary60)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(appBarGradient)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Title with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
                
                Column {
                    Text(
                        text = "HireAgent",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "AI-Powered Hiring Assistant",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
            
            // Action Button
            if (currentStep == MultiAnalysisStep.RANKED_RESULTS || currentStep == MultiAnalysisStep.DETAILED_VIEW) {
                ScaleInContent(visible = true) {
                    ModernFloatingActionButton(
                        onClick = onNewAnalysis,
                        modifier = Modifier.size(48.dp),
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "New Analysis",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernInputStep(
    uiState: HireAgentUiState,
    onResumeSelected: (android.net.Uri) -> Unit,
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Hero Section
        GradientBackground(
            gradient = Brush.radialGradient(
                colors = listOf(
                    Primary60.copy(alpha = 0.1f),
                    Secondary60.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = 800f
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.FindInPage,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Primary60
                )
                
                Text(
                    text = "Smart Candidate Analysis",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Upload resume and job description for AI-powered analysis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Progress Indicator
        ModernStepProgressIndicator(
            currentStep = 1,
            totalSteps = 3,
            stepTitles = listOf("Input", "Analysis", "Results")
        )
        
        // Resume Upload Section
        SlideInContent(visible = true) {
            ModernResumeUploadCard(
                uiState = uiState,
                onSelectFile = { filePickerLauncher.launch("*/*") },
                onClearFile = onClearResume
            )
        }
        
        // Job Description Section
        SlideInContent(visible = true) {
            ModernJobDescriptionCard(
                uiState = uiState,
                onJobDescriptionChanged = onJobDescriptionChanged,
                onClear = onClearJobDescription
            )
        }
        
        // Analysis Button
        ScaleInContent(visible = canStartAnalysis) {
            ModernAnalysisButton(
                onClick = onStartAnalysis,
                enabled = canStartAnalysis,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Requirements Section
        SlideInContent(visible = true) {
            ModernRequirementsCard()
        }
    }
}

@Composable
private fun ModernAnalyzingStep(
    progressMessage: String = "",
    modifier: Modifier = Modifier
) {
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphismCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Progress Indicator
                ModernStepProgressIndicator(
                    currentStep = 2,
                    totalSteps = 3,
                    stepTitles = listOf("Input", "Analysis", "Results")
                )
                

                
                Text(
                    text = "AI Analysis in Progress",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (progressMessage.isNotEmpty()) {
                    Text(
                        text = progressMessage,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = Primary60,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Our AI is analyzing the candidate's qualifications, experience, and fit for the role. This comprehensive analysis includes skills matching, experience evaluation, and hiring recommendations.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Analysis Steps with Animation
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernAnalysisStepItem(
                        text = "Extracting text from resume",
                        completed = progressMessage.contains("Analyzing resume") || 
                                   progressMessage.contains("Analyzing job") || 
                                   progressMessage.contains("Matching candidate") ||
                                   progressMessage.contains("Finalizing"),
                        active = progressMessage.contains("Extracting")
                    )
                    ModernAnalysisStepItem(
                        text = "Analyzing resume structure and content",
                        completed = progressMessage.contains("Analyzing job") || 
                                   progressMessage.contains("Matching candidate") ||
                                   progressMessage.contains("Finalizing"),
                        active = progressMessage.contains("Analyzing resume")
                    )
                    ModernAnalysisStepItem(
                        text = "Analyzing job description and requirements",
                        completed = progressMessage.contains("Matching candidate") ||
                                   progressMessage.contains("Finalizing"),
                        active = progressMessage.contains("Analyzing job")
                    )
                    ModernAnalysisStepItem(
                        text = "Matching candidate profile to job requirements",
                        completed = progressMessage.contains("Finalizing"),
                        active = progressMessage.contains("Matching candidate")
                    )
                    ModernAnalysisStepItem(
                        text = "Finalizing comprehensive analysis report",
                        completed = false,
                        active = progressMessage.contains("Finalizing")
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernAnalysisStepItem(
    text: String,
    completed: Boolean,
    active: Boolean
) {
    val animatedColor by animateColorAsState(
        targetValue = when {
            completed -> Success
            active -> Primary60
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(300),
        label = "step_color"
    )
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                completed -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(24.dp)
                    )
                }
                active -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Primary60
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = animatedColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = animatedColor,
            fontWeight = if (active || completed) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun ModernErrorStep(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ModernCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                Text(
                    text = "Analysis Failed",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Try Again",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
} 