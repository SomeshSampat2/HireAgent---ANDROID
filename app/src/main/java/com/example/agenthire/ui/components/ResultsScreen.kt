package com.example.agenthire.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenthire.data.models.CandidateAnalysis
import com.example.agenthire.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ResultsStep(
    analysisResult: CandidateAnalysis,
    onNewAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Skills", "Experience", "Interview", "Decision")
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Progress Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
        
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        // Tab Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                when (selectedTab) {
                    0 -> OverviewTab(analysisResult)
                    1 -> SkillsTab(analysisResult)
                    2 -> ExperienceTab(analysisResult)
                    3 -> InterviewTab(analysisResult)
                    4 -> DecisionTab(analysisResult)
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(analysisResult: CandidateAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Job Information Card
        JobInfoCard(analysisResult)
        
        // Overall Score Card
        OverallScoreCard(analysisResult)
        
        // Score Breakdown
        ScoreBreakdownCard(analysisResult)
        
        // Quick Summary
        QuickSummaryCard(analysisResult)
    }
}

@Composable
private fun JobInfoCard(analysisResult: CandidateAnalysis) {
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
                        .size(56.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Success, SuccessLight)
                            ),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Analysis Complete",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Comprehensive candidate evaluation finished",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Primary60
                    )
                    Text(
                        text = analysisResult.jobDescription.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (analysisResult.jobDescription.company.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Secondary60
                        )
                        Text(
                            text = analysisResult.jobDescription.company,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = analysisResult.resumeData.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallScoreCard(analysisResult: CandidateAnalysis) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
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
                        Icons.Default.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Overall Match Score",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Background circle
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                
                CircularProgressIndicator(
                    progress = { (analysisResult.overallScore / 100).toFloat() },
                    modifier = Modifier.size(140.dp),
                    strokeWidth = 12.dp,
                    color = getScoreColor(analysisResult.overallScore),
                    trackColor = Color.Transparent
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${analysisResult.overallScore.roundToInt()}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = getScoreColor(analysisResult.overallScore)
                    )
                    Text(
                        text = "/ 100",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = getScoreColor(analysisResult.overallScore).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = getScoreDescription(analysisResult.overallScore),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = getScoreColor(analysisResult.overallScore),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun ScoreBreakdownCard(analysisResult: CandidateAnalysis) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with gradient icon
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
                        Icons.Default.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Score Breakdown",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            ModernScoreBarItem(
                label = "Skills Match",
                score = analysisResult.scoreBreakdown.skillsMatch,
                icon = Icons.Default.Star,
                gradient = Brush.linearGradient(listOf(Primary60, Primary80))
            )
            
            ModernScoreBarItem(
                label = "Experience Relevance",
                score = analysisResult.scoreBreakdown.experienceRelevance,
                icon = Icons.Default.Timeline,
                gradient = Brush.linearGradient(listOf(Secondary60, Secondary80))
            )
            
            ModernScoreBarItem(
                label = "Education Fit",
                score = analysisResult.scoreBreakdown.educationFit,
                icon = Icons.Default.School,
                gradient = Brush.linearGradient(listOf(Success, SuccessLight))
            )
            
            ModernScoreBarItem(
                label = "Job Alignment",
                score = analysisResult.scoreBreakdown.jobSpecificAlignment,
                icon = Icons.Default.TrendingUp,
                gradient = Brush.linearGradient(listOf(Warning, WarningLight))
            )
        }
    }
}

@Composable
private fun ModernScoreBarItem(
    label: String,
    score: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (score / 100).toFloat(),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "score_progress"
    )
    
    ModernCard(
        elevation = 0.dp,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(gradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "${score.roundToInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Modern progress bar with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(gradient, RoundedCornerShape(6.dp))
                )
            }
            
            // Score description
            Text(
                text = getScoreDescription(score),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QuickSummaryCard(analysisResult: CandidateAnalysis) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                colors = listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.secondary)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Summarize,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Quick Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Strengths
            if (analysisResult.analysisDetails.strengthsForRole.isNotEmpty()) {
                ModernSummarySection(
                    title = "Key Strengths",
                    items = analysisResult.analysisDetails.strengthsForRole.take(3),
                    icon = Icons.Default.TrendingUp,
                    color = Success,
                    backgroundColor = Success.copy(alpha = 0.1f)
                )
            }
            
            // Areas for improvement
            if (analysisResult.analysisDetails.weaknessesForRole.isNotEmpty()) {
                ModernSummarySection(
                    title = "Areas to Address",
                    items = analysisResult.analysisDetails.weaknessesForRole.take(3),
                    icon = Icons.Default.Warning,
                    color = Warning,
                    backgroundColor = Warning.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun ModernSummarySection(
    title: String,
    items: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                                .padding(top = 6.dp)
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillsTab(analysisResult: CandidateAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Matching Skills
        SkillsMatchCard(
            title = "Matching Required Skills",
            skills = analysisResult.analysisDetails.skillsAnalysis.matchingRequiredSkills,
            color = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.CheckCircle
        )
        
        // Missing Skills
        SkillsMatchCard(
            title = "Missing Required Skills",
            skills = analysisResult.analysisDetails.skillsAnalysis.missingRequiredSkills,
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Default.Cancel
        )
        
        // Preferred Skills
        SkillsMatchCard(
            title = "Matching Preferred Skills",
            skills = analysisResult.analysisDetails.skillsAnalysis.matchingPreferredSkills,
            color = MaterialTheme.colorScheme.tertiary,
            icon = Icons.Default.Star
        )
    }
}

@Composable
private fun SkillsMatchCard(
    title: String,
    skills: List<String>,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Column {
                    Text(
                        text = title,
                        style = CustomTextStyles.CardTitle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${skills.size} skills",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (skills.isEmpty()) {
                ModernCard(
                    elevation = 0.dp,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "None identified",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Improved skills layout with proper wrapping
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(skills) { skill ->
                        ModernSkillChip(
                            skill = skill,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernSkillChip(
    skill: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = skill,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ExperienceTab(analysisResult: CandidateAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Experience Overview
        ExperienceOverviewCard(analysisResult.analysisDetails.experienceMatch)
        
        // Education Analysis
        EducationAnalysisCard(analysisResult.analysisDetails.educationAnalysis)
    }
}

@Composable
private fun ExperienceOverviewCard(experienceMatch: com.example.agenthire.data.models.ExperienceMatch) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with icon
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
                        Icons.Default.WorkHistory,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Experience Analysis",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Experience metrics with better layout
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernInfoCard(
                    label = "Years of Experience",
                    value = experienceMatch.relevantExperienceYears,
                    color = Primary60
                )
                
                ModernInfoCard(
                    label = "Experience Level Fit",
                    value = experienceMatch.experienceLevelFit,
                    color = Secondary60
                )
                
                ModernInfoCard(
                    label = "Industry Relevance",
                    value = experienceMatch.industryRelevance,
                    color = Success
                )
            }
            
            if (experienceMatch.matchingResponsibilities.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Matching Responsibilities:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    experienceMatch.matchingResponsibilities.forEach { responsibility ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = responsibility,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EducationAnalysisCard(educationAnalysis: com.example.agenthire.data.models.EducationAnalysis) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Success,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Education Analysis",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (educationAnalysis.meetsRequirements) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (educationAnalysis.meetsRequirements) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (educationAnalysis.meetsRequirements) "Meets Requirements" else "Does Not Meet Requirements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (educationAnalysis.meetsRequirements) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            if (educationAnalysis.relevantDegrees.isNotEmpty()) {
                InfoSection("Relevant Degrees", educationAnalysis.relevantDegrees)
            }
            
            if (educationAnalysis.additionalCertificationsNeeded.isNotEmpty()) {
                InfoSection("Additional Certifications Needed", educationAnalysis.additionalCertificationsNeeded)
            }
        }
    }
}

@Composable
private fun InterviewTab(analysisResult: CandidateAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Interview Focus Areas
        InterviewFocusCard(analysisResult.analysisDetails.interviewFocusAreas)
        
        // Onboarding Recommendations
        OnboardingCard(analysisResult.analysisDetails.onboardingRecommendations)
        
        // Salary Assessment
        SalaryAssessmentCard(analysisResult.analysisDetails.salaryFitAssessment)
    }
}

@Composable
private fun InterviewFocusCard(focusAreas: List<String>) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Interview Focus Areas",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                focusAreas.forEach { area ->
                    ModernCard(
                        elevation = 0.dp,
                        colors = CardDefaults.cardColors(
                            containerColor = Primary60.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Primary60, CircleShape)
                            )
                            Text(
                                text = area,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary60,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard(recommendations: List<String>) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Secondary60,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Onboarding Recommendations",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                recommendations.forEach { recommendation ->
                    ModernCard(
                        elevation = 0.dp,
                        colors = CardDefaults.cardColors(
                            containerColor = Secondary60.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = Secondary60
                            )
                            Text(
                                text = recommendation,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Secondary60,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryAssessmentCard(assessment: String) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                colors = listOf(Success, SuccessLight)
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Salary Fit Assessment",
                    style = CustomTextStyles.SectionHeader,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            ModernCard(
                elevation = 0.dp,
                colors = CardDefaults.cardColors(
                    containerColor = Success.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = assessment,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Success,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DecisionTab(analysisResult: CandidateAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HiringDecisionCard(analysisResult.hiringRecommendation)
    }
}

@Composable
private fun HiringDecisionCard(recommendation: com.example.agenthire.data.models.HiringRecommendation) {
    val decisionColor = getHiringDecisionColor(recommendation.decision)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = decisionColor.copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, decisionColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                getHiringDecisionIcon(recommendation.decision),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = decisionColor
            )
            
            Text(
                text = "Hiring Recommendation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = decisionColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = recommendation.decision,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
            
            Text(
                text = "Confidence: ${recommendation.confidenceLevel}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = decisionColor
            )
            
            Text(
                text = recommendation.reasoning,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

// Helper functions
@Composable
private fun ModernInfoCard(
    label: String,
    value: String,
    color: Color
) {
    ModernCard(
        elevation = 0.dp,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, items: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        items.forEach { item ->
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun getScoreColor(score: Double): Color {
    return when {
        score >= 80 -> MaterialTheme.colorScheme.primary
        score >= 60 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun getScoreCardColor(score: Double): Color {
    return when {
        score >= 80 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        score >= 60 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    }
}

private fun getScoreDescription(score: Double): String {
    return when {
        score >= 90 -> "Excellent Match"
        score >= 80 -> "Strong Match"
        score >= 70 -> "Good Match"
        score >= 60 -> "Fair Match"
        else -> "Poor Match"
    }
}

@Composable
private fun getHiringDecisionColor(decision: String): Color {
    return when (decision.uppercase()) {
        "STRONG HIRE" -> MaterialTheme.colorScheme.primary
        "HIRE" -> MaterialTheme.colorScheme.secondary
        "CONSIDER" -> MaterialTheme.colorScheme.tertiary
        "REJECT" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
}

private fun getHiringDecisionIcon(decision: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (decision.uppercase()) {
        "STRONG HIRE" -> Icons.Default.ThumbUp
        "HIRE" -> Icons.Default.CheckCircle
        "CONSIDER" -> Icons.Default.Schedule
        "REJECT" -> Icons.Default.ThumbDown
        else -> Icons.Default.Help
    }
} 