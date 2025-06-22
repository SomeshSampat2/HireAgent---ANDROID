package com.example.agenthire.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenthire.data.models.*
import com.example.agenthire.ui.theme.*
import kotlin.math.roundToInt

/**
 * Modern Batch Analyzing Step with progress indicators
 */
@Composable
fun ModernBatchAnalyzingStep(
    batchAnalysisState: BatchAnalysisState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Progress Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            ModernStepProgressIndicator(
                currentStep = 2,
                totalSteps = 3,
                stepTitles = listOf("Input", "Analysis", "Results"),
                modifier = Modifier.padding(0.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main Analysis Card
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Icon and Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Primary60, Secondary60)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = "AI Analysis in Progress",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Progress Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Overall Progress Bar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Overall Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${batchAnalysisState.processedCount}/${batchAnalysisState.totalCount} resumes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(batchAnalysisState.progressPercentage * 100).roundToInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = { batchAnalysisState.progressPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Primary60,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                    
                    // Current Step
                    AnimatedContent(
                        targetState = batchAnalysisState.currentProcessing,
                        transitionSpec = {
                            slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith
                            slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                        },
                        label = "current_step"
                    ) { currentStep ->
                        if (currentStep.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary60.copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 3.dp,
                                        color = Primary60
                                    )
                                    Text(
                                        text = currentStep,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
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

/**
 * Ranked Results Screen showing candidates sorted by score
 */
@Composable
fun RankedResultsScreen(
    rankedCandidates: List<RankedCandidate>,
    onCandidateClick: (RankedCandidate) -> Unit,
    onNewAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Progress Indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                ModernStepProgressIndicator(
                    currentStep = 3,
                    totalSteps = 3,
                    stepTitles = listOf("Input", "Analysis", "Results"),
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
        
        item {
            // Header Card
            GlassmorphismCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                                Icons.Default.Leaderboard,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Candidate Rankings",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${rankedCandidates.size} candidates analyzed and ranked by overall match score",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Candidate List
        itemsIndexed(rankedCandidates) { index, candidate ->
            RankedCandidateCard(
                rankedCandidate = candidate,
                onClick = { onCandidateClick(candidate) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            // New Analysis Button
            Spacer(modifier = Modifier.height(16.dp))
            
            ModernAnalysisButton(
                onClick = onNewAnalysis,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Ranked Candidate Card showing candidate info and score
 */
@Composable
fun RankedCandidateCard(
    rankedCandidate: RankedCandidate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analysis = rankedCandidate.candidateAnalysis
    val resumeData = analysis.resumeData
    
    ModernCard(
        onClick = onClick,
        modifier = modifier,
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        when (rankedCandidate.rank) {
                            1 -> Brush.linearGradient(colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000)))
                            2 -> Brush.linearGradient(colors = listOf(Color(0xFFC0C0C0), Color(0xFF9E9E9E)))
                            3 -> Brush.linearGradient(colors = listOf(Color(0xFFCD7F32), Color(0xFF8D6E63)))
                            else -> Brush.linearGradient(colors = listOf(Primary60, Secondary60))
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${rankedCandidate.rank}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Candidate Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = resumeData.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (resumeData.email.isNotEmpty()) {
                    Text(
                        text = resumeData.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Quick Skills Preview
                if (resumeData.skills.isNotEmpty()) {
                    Text(
                        text = resumeData.skills.take(3).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Score and Details Column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Overall Score
                Box(
                    modifier = Modifier
                        .background(
                            getScoreColor(rankedCandidate.overallScore).copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${rankedCandidate.overallScore.roundToInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(rankedCandidate.overallScore)
                    )
                }
                
                // View Details Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Candidate Detail Screen showing full analysis
 */
@Composable
fun CandidateDetailScreen(
    rankedCandidate: RankedCandidate,
    onBack: () -> Unit,
    onNewAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar with Back Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back to rankings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rankedCandidate.candidateAnalysis.resumeData.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rank #${rankedCandidate.rank} • ${rankedCandidate.overallScore.roundToInt()}% Match",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                TextButton(onClick = onNewAnalysis) {
                    Text("New Analysis")
                }
            }
        }
        
        // Full Analysis Results
        ResultsStep(
            analysisResult = rankedCandidate.candidateAnalysis,
            onNewAnalysis = onNewAnalysis,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Get color based on score value
 */
@Composable
private fun getScoreColor(score: Double): Color {
    return when {
        score >= 80 -> Success
        score >= 60 -> Color(0xFFFF9800) // Orange
        score >= 40 -> Color(0xFFFFEB3B) // Yellow  
        else -> MaterialTheme.colorScheme.error
    }
}

/**
 * Get score card background color
 */
@Composable
private fun getScoreCardColor(score: Double): Color {
    return when {
        score >= 80 -> Success.copy(alpha = 0.1f)
        score >= 60 -> Color(0xFFFF9800).copy(alpha = 0.1f)
        score >= 40 -> Color(0xFFFFEB3B).copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
    }
} 