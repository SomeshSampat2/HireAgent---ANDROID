package com.example.agenthire.ui.components

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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            StepProgressIndicator(
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Work,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Job Analysis Complete",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Analysis for ${analysisResult.jobDescription.title}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (analysisResult.jobDescription.company.isNotEmpty()) {
                Text(
                    text = "at ${analysisResult.jobDescription.company}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Text(
                text = "Candidate: ${analysisResult.resumeData.name}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun OverallScoreCard(analysisResult: CandidateAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getScoreCardColor(analysisResult.overallScore)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Overall Match Score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = { (analysisResult.overallScore / 100).toFloat() },
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 8.dp,
                    color = getScoreColor(analysisResult.overallScore),
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${analysisResult.overallScore.roundToInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(analysisResult.overallScore),
                        fontSize = 36.sp
                    )
                    Text(
                        text = "out of 100",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Text(
                text = getScoreDescription(analysisResult.overallScore),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScoreBreakdownCard(analysisResult: CandidateAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Score Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            ScoreBarItem(
                label = "Skills Match",
                score = analysisResult.scoreBreakdown.skillsMatch,
                icon = Icons.Default.Star
            )
            
            ScoreBarItem(
                label = "Experience Relevance",
                score = analysisResult.scoreBreakdown.experienceRelevance,
                icon = Icons.Default.Timeline
            )
            
            ScoreBarItem(
                label = "Education Fit",
                score = analysisResult.scoreBreakdown.educationFit,
                icon = Icons.Default.School
            )
            
            ScoreBarItem(
                label = "Job Alignment",
                score = analysisResult.scoreBreakdown.jobSpecificAlignment,
                icon = Icons.Default.TrendingUp
            )
        }
    }
}

@Composable
private fun ScoreBarItem(
    label: String,
    score: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = getScoreColor(score)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = "${score.roundToInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = getScoreColor(score)
            )
        }
        
        LinearProgressIndicator(
            progress = { (score / 100).toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getScoreColor(score),
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun QuickSummaryCard(analysisResult: CandidateAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Quick Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Strengths
            if (analysisResult.analysisDetails.strengthsForRole.isNotEmpty()) {
                SummarySection(
                    title = "Key Strengths",
                    items = analysisResult.analysisDetails.strengthsForRole.take(3),
                    icon = Icons.Default.TrendingUp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Areas for improvement
            if (analysisResult.analysisDetails.weaknessesForRole.isNotEmpty()) {
                SummarySection(
                    title = "Areas to Address",
                    items = analysisResult.analysisDetails.weaknessesForRole.take(3),
                    icon = Icons.Default.Warning,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    title: String,
    items: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                        .padding(top = 8.dp)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (skills.isEmpty()) {
                Text(
                    text = "None identified",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(skills) { skill ->
                        AssistChip(
                            onClick = { },
                            label = { Text(skill) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = color.copy(alpha = 0.2f),
                                labelColor = color
                            )
                        )
                    }
                }
            }
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Experience Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            InfoRow("Years of Experience", experienceMatch.relevantExperienceYears)
            InfoRow("Experience Level Fit", experienceMatch.experienceLevelFit)
            InfoRow("Industry Relevance", experienceMatch.industryRelevance)
            
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Education Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Interview Focus Areas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            focusAreas.forEach { area ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(top = 8.dp)
                    )
                    Text(
                        text = area,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard(recommendations: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Onboarding Recommendations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            recommendations.forEach { recommendation ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SalaryAssessmentCard(assessment: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Salary Fit Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = assessment,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
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
        border = BorderStroke(2.dp, decisionColor)
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
                )
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
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
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