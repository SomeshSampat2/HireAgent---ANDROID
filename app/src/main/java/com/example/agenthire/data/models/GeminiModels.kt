package com.example.agenthire.data.models

import com.google.gson.annotations.SerializedName

// Gemini API Request Models
data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig? = null
)

data class Content(
    @SerializedName("parts")
    val parts: List<Part>
)

data class Part(
    @SerializedName("text")
    val text: String
)

data class GenerationConfig(
    @SerializedName("temperature")
    val temperature: Double = 0.1,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 3000
)

// Gemini API Response Models
data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>
)

data class Candidate(
    @SerializedName("content")
    val content: Content
)

// Application Domain Models
data class JobDescription(
    val title: String,
    val company: String,
    val description: String,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val experienceLevel: String,
    val educationRequirements: List<String>,
    val location: String,
    val employmentType: String = "Not specified",
    val salaryRange: String = "Not specified",
    val industry: String = "Not specified",
    val keyResponsibilities: List<String> = emptyList(),
    val technicalRequirements: List<String> = emptyList(),
    val softSkills: List<String> = emptyList()
)

data class ResumeData(
    val name: String,
    val email: String,
    val phone: String,
    val location: String,
    val summary: String,
    val skills: List<String>,
    val experience: List<Experience>,
    val education: List<Education>,
    val extractedUrls: ExtractedUrls?
)

data class Experience(
    val title: String,
    val company: String,
    val duration: String,
    val description: String,
    val responsibilities: List<String>
)

data class Education(
    val degree: String,
    val institution: String,
    val year: String,
    val details: String
)

data class ExtractedUrls(
    val linkedinUrl: String? = null,
    val githubUrl: String? = null,
    val portfolioUrl: String? = null
)

data class CandidateAnalysis(
    val overallScore: Double,
    val scoreBreakdown: ScoreBreakdown,
    val jobDescription: JobDescription,
    val resumeData: ResumeData,
    val analysisDetails: AnalysisDetails,
    val hiringRecommendation: HiringRecommendation
)

data class ScoreBreakdown(
    val totalScore: Double,
    val skillsMatch: Double,
    val experienceRelevance: Double,
    val educationFit: Double,
    val jobSpecificAlignment: Double
)

data class AnalysisDetails(
    val jobSpecificScoring: JobSpecificScoring,
    val skillsAnalysis: SkillsAnalysis,
    val strengthsForRole: List<String>,
    val weaknessesForRole: List<String>,
    val experienceMatch: ExperienceMatch,
    val educationAnalysis: EducationAnalysis,
    val interviewFocusAreas: List<String>,
    val onboardingRecommendations: List<String>,
    val salaryFitAssessment: String
)

data class JobSpecificScoring(
    val requiredSkillsMatch: Double,
    val experienceRelevance: Double,
    val educationFit: Double,
    val jobSpecificAlignment: Double
)

data class SkillsAnalysis(
    val matchingRequiredSkills: List<String>,
    val missingRequiredSkills: List<String>,
    val matchingPreferredSkills: List<String>,
    val missingPreferredSkills: List<String>
)

data class ExperienceMatch(
    val relevantExperienceYears: String,
    val matchingResponsibilities: List<String>,
    val experienceLevelFit: String,
    val industryRelevance: String
)

data class EducationAnalysis(
    val meetsRequirements: Boolean,
    val relevantDegrees: List<String>,
    val additionalCertificationsNeeded: List<String>
)

data class HiringRecommendation(
    val decision: String,
    val confidenceLevel: String,
    val reasoning: String
)

// API State Management
sealed class ApiState<T> {
    class Loading<T> : ApiState<T>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class Error<T>(val message: String) : ApiState<T>()
}

// Analysis State
data class AnalysisState(
    val isAnalyzing: Boolean = false,
    val analysisResult: CandidateAnalysis? = null,
    val error: String? = null
) 