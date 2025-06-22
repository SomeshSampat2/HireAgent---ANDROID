package com.example.agenthire.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agenthire.ui.theme.*
import com.example.agenthire.viewmodel.HireAgentUiState
import com.example.agenthire.viewmodel.MultiResumeUiState
import com.example.agenthire.data.models.ResumeFile
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.unit.sp
import android.net.Uri

/**
 * Modern Progress Indicator with animations
 */
@Composable
fun ModernStepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepTitles: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep - 1
                
                val animatedColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> Success
                        isCurrent -> Primary60
                        else -> MaterialTheme.colorScheme.outline
                    },
                    animationSpec = tween(300),
                    label = "step_color_$index"
                )
                
                val animatedScale by animateFloatAsState(
                    targetValue = if (isCurrent) 1.2f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "step_scale_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(animatedScale)
                        .background(animatedColor, CircleShape)
                )
                
                if (index < totalSteps - 1) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .background(
                                if (isCompleted) Success else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
        
        // Step titles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            stepTitles.forEachIndexed { index, title ->
                val isActive = index == currentStep - 1
                val textColor by animateColorAsState(
                    targetValue = if (isActive) Primary60 else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(300),
                    label = "text_color_$index"
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Modern Resume Upload Card with glassmorphism effect
 */
@Composable
fun ModernResumeUploadCard(
    uiState: HireAgentUiState,
    onSelectFile: () -> Unit,
    onClearFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
    
    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary60, Secondary60)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Resume Upload",
                        style = CustomTextStyles.SectionHeader,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Upload your resume for AI analysis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status indicator
                AnimatedVisibility(
                    visible = uiState.resumeValidated,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Success, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Validated",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            if (uiState.selectedResumeUri == null) {
                // Upload area
                ModernCard(
                    onClick = onSelectFile,
                    elevation = 0.dp,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Primary60.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Primary60
                            )
                        }
                        
                        Text(
                            text = "Drop files here or click to browse",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "Supports PDF, DOCX, DOC, and TXT files • Max 10MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // File selected state
                SlideInContent(visible = true) {
                    ModernCard(
                        elevation = 0.dp,
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.resumeValidated)
                                Success.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (uiState.resumeValidated) Success else Neutral60,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (uiState.resumeValidated) 
                                            Icons.Default.CheckCircle 
                                        else 
                                            Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = uiState.resumeFileName ?: "Selected file",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Text(
                                        text = if (uiState.resumeValidated) 
                                            "✓ File validated - ready for analysis" 
                                        else 
                                            "Validating file...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (uiState.resumeValidated) 
                                            Success 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            IconButton(
                                onClick = onClearFile,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove file",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Error state
            AnimatedVisibility(
                visible = uiState.resumeError != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.resumeError?.let { error ->
                    ModernCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Job Description Card with enhanced text input
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernJobDescriptionCard(
    uiState: HireAgentUiState,
    onJobDescriptionChanged: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Secondary60, Primary60)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Job Description",
                        style = CustomTextStyles.SectionHeader,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Describe the role requirements and qualifications",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status indicator
                AnimatedVisibility(
                    visible = uiState.jobDescriptionValidated,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Success, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Validated",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
                
                // Clear button
                if (uiState.jobDescriptionText.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Text input with modern styling
            OutlinedTextField(
                value = uiState.jobDescriptionText,
                onValueChange = onJobDescriptionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 240.dp),
                placeholder = {
                    Text(
                        text = "Enter detailed job description including:\n• Role responsibilities\n• Required skills and experience\n• Qualifications and education\n• Company culture and benefits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val characterCount = uiState.jobDescriptionText.length
                        val isValid = characterCount >= 50
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (isValid) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isValid) Success else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isValid) "Sufficient detail provided" else "Minimum 50 characters required",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isValid) Success else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            text = "$characterCount/50",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isValid) Success else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                isError = uiState.jobDescriptionError != null,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary60,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            // Validation success state
            AnimatedVisibility(
                visible = uiState.jobDescriptionValidated,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                ModernCard(
                    colors = CardDefaults.cardColors(
                        containerColor = Success.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Job description validated - ready for analysis",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Success,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Error state
            AnimatedVisibility(
                visible = uiState.jobDescriptionError != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.jobDescriptionError?.let { error ->
                    ModernCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Analysis Button with gradient and animations
 */
@Composable
fun ModernAnalysisButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonGradient = Brush.linearGradient(
        colors = if (enabled) {
            listOf(Primary60, Secondary60)
        } else {
            listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant
            )
        }
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(300),
        label = "button_elevation"
    )
    
    GlassmorphismCard(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        backgroundColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Animated icon
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (enabled) {
                    PulsingLoadingIndicator(
                        size = 80.dp,
                        color = Primary60.copy(alpha = 0.2f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (enabled) buttonGradient else Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = if (enabled) "Ready for Batch AI Analysis" else "Complete Requirements",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (enabled) {
                    "All requirements met. Start comprehensive multi-candidate analysis with AI-powered insights, ranking, and hiring recommendations."
                } else {
                    "Please upload resume files and provide a detailed job description to continue with the analysis."
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Start Batch Analysis",
                            style = CustomTextStyles.ButtonText,
                            color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern Requirements Card with beautiful layout
 */
@Composable
fun ModernRequirementsCard(
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Primary60,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Requirements & Features",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Requirements list
            val requirements = listOf(
                "File Formats" to "Supports PDF, DOCX, DOC, and TXT files",
                "File Size" to "Maximum file size of 10MB",
                "AI Analysis" to "Comprehensive candidate scoring and matching",
                "Detailed Reports" to "Skills assessment, experience evaluation, and hiring recommendations",
                "Secure Processing" to "Your data is processed securely and privately"
            )
            
            requirements.forEach { (title, description) ->
                ModernRequirementItem(
                    title = title,
                    description = description
                )
            }
        }
    }
}

@Composable
private fun ModernRequirementItem(
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    Primary60.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Primary60, CircleShape)
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Multi-Resume Analysis Button showing resume count
 */
@Composable
fun MultiResumeAnalysisButton(
    onClick: () -> Unit,
    enabled: Boolean,
    resumeCount: Int,
    modifier: Modifier = Modifier
) {
    val buttonGradient = Brush.linearGradient(
        colors = if (enabled) {
            listOf(Primary60, Secondary60)
        } else {
            listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant
            )
        }
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(300),
        label = "button_elevation"
    )
    
    GlassmorphismCard(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        backgroundColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Animated icon with resume count
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (enabled) {
                    PulsingLoadingIndicator(
                        size = 80.dp,
                        color = Primary60.copy(alpha = 0.2f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (enabled) buttonGradient else Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Resume count badge
                if (enabled && resumeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.error,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = resumeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            Text(
                text = if (enabled) {
                    if (resumeCount > 1) "Ready for Batch AI Analysis" else "Ready for AI Analysis"
                } else {
                    "Complete Requirements"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (enabled) {
                    if (resumeCount > 1) {
                        "All requirements met. Start comprehensive analysis of $resumeCount candidates with AI-powered insights, ranking, and hiring recommendations."
                    } else {
                        "All requirements met. Start comprehensive candidate analysis with AI-powered insights, scoring, and hiring recommendations."
                    }
                } else {
                    "Please upload resume files and provide a detailed job description to continue with the analysis."
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (resumeCount > 1) {
                                "Analyze $resumeCount Resumes"
                            } else {
                                "Start AI Analysis"
                            },
                            style = CustomTextStyles.ButtonText,
                            color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Multi-Resume Input Step with support for multiple file selection
 */
@Composable
fun MultiResumeInputStep(
    uiState: MultiResumeUiState,
    onResumesSelected: (List<Uri>) -> Unit,
    onJobDescriptionChanged: (String) -> Unit,
    onStartAnalysis: () -> Unit,
    onClearResumes: () -> Unit,
    onClearJobDescription: () -> Unit,
    onRemoveResumeFile: (ResumeFile) -> Unit,
    canStartAnalysis: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Multi-file launcher
    val multipleFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onResumesSelected(uris)
        }
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            // Progress Indicator
            ModernStepProgressIndicator(
                currentStep = 1,
                totalSteps = 3,
                stepTitles = listOf("Input", "Analysis", "Results")
            )
        }
        
        item {
            // Multiple Resume Upload Card
            MultiResumeUploadCard(
                uiState = uiState,
                onSelectFiles = { multipleFileLauncher.launch("*/*") },
                onClearFiles = onClearResumes,
                onRemoveFile = onRemoveResumeFile,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            // Job Description Input
            ModernJobDescriptionCard(
                uiState = uiState,
                onJobDescriptionChanged = onJobDescriptionChanged,
                onClearJobDescription = onClearJobDescription,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            // Start Analysis Button - Always visible
            MultiResumeAnalysisButton(
                onClick = onStartAnalysis,
                enabled = canStartAnalysis,
                resumeCount = uiState.selectedResumeFiles.size,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Multi-Resume Upload Card with file list display
 */
@Composable
fun MultiResumeUploadCard(
    uiState: MultiResumeUiState,
    onSelectFiles: () -> Unit,
    onClearFiles: () -> Unit,
    onRemoveFile: (ResumeFile) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary60, Secondary60)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Resume Upload",
                        style = CustomTextStyles.SectionHeader,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Upload 1-10 resumes for batch analysis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status indicator
                AnimatedVisibility(
                    visible = uiState.resumesValidated && uiState.selectedResumeFiles.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Badge(
                        containerColor = Success,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "${uiState.selectedResumeFiles.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            if (uiState.selectedResumeFiles.isEmpty()) {
                // Upload area
                ModernCard(
                    onClick = onSelectFiles,
                    elevation = 0.dp,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select Resume Files",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "PDF, DOCX, DOC, TXT • Max 10MB each",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // File list
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected Files (${uiState.selectedResumeFiles.size}/10)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (uiState.selectedResumeFiles.size < 10) {
                                TextButton(onClick = onSelectFiles) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add More")
                                }
                            } else {
                                Text(
                                    text = "Maximum reached",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            
                            TextButton(onClick = onClearFiles) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear All")
                            }
                        }
                    }
                    
                    // File list
                    uiState.selectedResumeFiles.forEach { resumeFile ->
                        ResumeFileItem(
                            resumeFile = resumeFile,
                            onRemove = { onRemoveFile(resumeFile) }
                        )
                    }
                }
            }
            
            // Error message
            AnimatedVisibility(
                visible = !uiState.resumeError.isNullOrEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.resumeError?.let { error ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Resume File Item
 */
@Composable
fun ResumeFileItem(
    resumeFile: ResumeFile,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // File type icon
                val fileIcon = when {
                    resumeFile.fileName.endsWith(".pdf", ignoreCase = true) -> Icons.Default.PictureAsPdf
                    resumeFile.fileName.endsWith(".docx", ignoreCase = true) || 
                    resumeFile.fileName.endsWith(".doc", ignoreCase = true) -> Icons.Default.Description
                    else -> Icons.Default.TextSnippet
                }
                
                Icon(
                    fileIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = resumeFile.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${(resumeFile.fileSize / 1024).toInt()} KB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove file",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Modern Job Description Card (updated for multi-resume)
 */
@Composable
fun ModernJobDescriptionCard(
    uiState: MultiResumeUiState,
    onJobDescriptionChanged: (String) -> Unit,
    onClearJobDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Secondary60, Primary60)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Job Description",
                        style = CustomTextStyles.SectionHeader,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Describe the role requirements and qualifications",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status indicator
                AnimatedVisibility(
                    visible = uiState.jobDescriptionValidated,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Success, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Validated",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Text field
            OutlinedTextField(
                value = uiState.jobDescriptionText,
                onValueChange = onJobDescriptionChanged,
                label = { Text("Job Description") },
                placeholder = { Text("Enter detailed job description, requirements, and qualifications...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.jobDescriptionError.isNullOrEmpty(),
                trailingIcon = {
                    if (uiState.jobDescriptionText.isNotEmpty()) {
                        IconButton(onClick = onClearJobDescription) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
            
            // Character count and error
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Error message
                AnimatedVisibility(
                    visible = !uiState.jobDescriptionError.isNullOrEmpty(),
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    uiState.jobDescriptionError?.let { error ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Character count
                Text(
                    text = "${uiState.jobDescriptionText.length}/50+ chars",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.jobDescriptionText.length >= 50) {
                        Success
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
} 