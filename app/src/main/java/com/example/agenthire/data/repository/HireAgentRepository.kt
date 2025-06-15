package com.example.agenthire.data.repository

import android.content.Context
import com.example.agenthire.data.api.GeminiApiService
import com.example.agenthire.data.models.*
import com.example.agenthire.utils.DocumentProcessor
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.Uri

class HireAgentRepository(
    private val context: Context,
    private val apiKey: String
) {
    private val documentProcessor = DocumentProcessor(context)
    private val gson = Gson()
    
    private val geminiApi: GeminiApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        Retrofit.Builder()
            .baseUrl(GeminiApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
    
    /**
     * Parse resume from text and extract structured data
     */
    suspend fun parseResume(resumeText: String): Result<ResumeData> {
        return withContext(Dispatchers.IO) {
            try {
                
                // Create prompt for Gemini to parse resume
                val prompt = createResumeParsingPrompt(resumeText)
                
                // Make API call to Gemini
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(temperature = 0.1, maxOutputTokens = 3000)
                )
                
                val response = geminiApi.generateContent(apiKey, request)
                
                if (response.isSuccessful && response.body() != null) {
                    val geminiResponse = response.body()!!
                    val content = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (content != null) {
                        val cleanedContent = cleanJsonResponse(content)
                        val resumeData = gson.fromJson(cleanedContent, ResumeData::class.java)
                        Result.success(resumeData)
                    } else {
                        Result.failure(Exception("No content received from Gemini API"))
                    }
                } else {
                    Result.failure(Exception("API call failed: ${response.message()}"))
                }
                
            } catch (e: JsonSyntaxException) {
                Result.failure(Exception("Failed to parse resume data: Invalid JSON format"))
            } catch (e: Exception) {
                Result.failure(Exception("Error parsing resume: ${e.message}"))
            }
        }
    }
    
    /**
     * Parse job description text and extract structured data
     */
    suspend fun parseJobDescription(jobDescriptionText: String): Result<JobDescription> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = createJobDescriptionParsingPrompt(jobDescriptionText)
                
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(temperature = 0.1, maxOutputTokens = 3000)
                )
                
                val response = geminiApi.generateContent(apiKey, request)
                
                if (response.isSuccessful && response.body() != null) {
                    val geminiResponse = response.body()!!
                    val content = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (content != null) {
                        val cleanedContent = cleanJsonResponse(content)
                        val jobDescription = gson.fromJson(cleanedContent, JobDescription::class.java)
                        Result.success(jobDescription)
                    } else {
                        Result.failure(Exception("No content received from Gemini API"))
                    }
                } else {
                    Result.failure(Exception("API call failed: ${response.message()}"))
                }
                
            } catch (e: JsonSyntaxException) {
                Result.failure(Exception("Failed to parse job description: Invalid JSON format"))
            } catch (e: Exception) {
                Result.failure(Exception("Error parsing job description: ${e.message}"))
            }
        }
    }
    
    /**
     * Perform comprehensive candidate analysis
     */
    suspend fun analyzeCandidateForJob(
        resumeData: ResumeData,
        jobDescription: JobDescription
    ): Result<CandidateAnalysis> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = createCandidateAnalysisPrompt(resumeData, jobDescription)
                
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(temperature = 0.1, maxOutputTokens = 4000)
                )
                
                val response = geminiApi.generateContent(apiKey, request)
                
                if (response.isSuccessful && response.body() != null) {
                    val geminiResponse = response.body()!!
                    val content = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (content != null) {
                        val cleanedContent = cleanJsonResponse(content)
                        val analysisResult = gson.fromJson(cleanedContent, CandidateAnalysisResponse::class.java)
                        
                        // Build the complete analysis
                        val candidateAnalysis = CandidateAnalysis(
                            overallScore = analysisResult.overallScore,
                            scoreBreakdown = analysisResult.scoreBreakdown,
                            jobDescription = jobDescription,
                            resumeData = resumeData,
                            analysisDetails = analysisResult.analysisDetails,
                            hiringRecommendation = analysisResult.hiringRecommendation
                        )
                        
                        Result.success(candidateAnalysis)
                    } else {
                        Result.failure(Exception("No content received from Gemini API"))
                    }
                } else {
                    Result.failure(Exception("API call failed: ${response.message()}"))
                }
                
            } catch (e: JsonSyntaxException) {
                Result.failure(Exception("Failed to parse analysis results: Invalid JSON format"))
            } catch (e: Exception) {
                Result.failure(Exception("Error analyzing candidate: ${e.message}"))
            }
        }
    }
    
    private fun createResumeParsingPrompt(resumeText: String): String {
        return """
        You are an expert HR analyst. Extract comprehensive resume information from the following resume text and return it in JSON format.
        
        CRITICAL: Be thorough and extract ALL relevant details including contact information, skills, experience, education, and any URLs.
        
        Required JSON structure:
        {
            "name": "Full name",
            "email": "Email address",
            "phone": "Phone number", 
            "location": "Location/Address",
            "summary": "Professional summary or objective",
            "skills": ["skill1", "skill2", "skill3", ...],
            "experience": [
                {
                    "title": "Job title",
                    "company": "Company name",
                    "duration": "Duration (e.g., 2020-2023)",
                    "description": "Role description",
                    "responsibilities": ["responsibility1", "responsibility2", ...]
                }
            ],
            "education": [
                {
                    "degree": "Degree type and field",
                    "institution": "Institution name",
                    "year": "Graduation year",
                    "details": "Additional details like GPA, honors, etc."
                }
            ],
            "extractedUrls": {
                "linkedinUrl": "LinkedIn URL if found",
                "githubUrl": "GitHub URL if found", 
                "portfolioUrl": "Portfolio/website URL if found"
            }
        }
        
        EXTRACTION GUIDELINES:
        1. Extract ALL technical skills, soft skills, programming languages, frameworks, tools
        2. Capture complete work experience with detailed responsibilities
        3. Include all educational background and certifications
        4. Find and extract any LinkedIn, GitHub, portfolio, or personal website URLs
        5. If information is missing, use empty strings for strings or empty arrays for lists
        6. Be thorough in extracting responsibilities and achievements from experience
        
        Resume Text:
        $resumeText
        """.trimIndent()
    }
    
    private fun createJobDescriptionParsingPrompt(jobDescriptionText: String): String {
        return """
        You are an expert HR analyst. Extract comprehensive job description information from the following job description text and return it in JSON format.
        
        CRITICAL: Be thorough and extract ALL relevant details including technical requirements, soft skills, experience requirements, and qualifications.
        
        Required JSON structure:
        {
            "title": "Exact job title from posting",
            "company": "Company name (if mentioned, otherwise 'Not specified')",
            "description": "Complete job description including responsibilities, duties, and role overview",
            "requiredSkills": ["skill1", "skill2", "skill3", ...],
            "preferredSkills": ["preferred_skill1", "preferred_skill2", ...],
            "experienceLevel": "Entry/Junior/Mid/Senior/Lead/Principal",
            "educationRequirements": ["Bachelor's degree", "Master's preferred", "relevant certifications", ...],
            "location": "Job location (remote, hybrid, or specific city) or 'Not specified'",
            "employmentType": "Full-time/Part-time/Contract/Freelance or 'Not specified'",
            "salaryRange": "Salary information if available or 'Not specified'",
            "industry": "Industry sector or 'Not specified'",
            "keyResponsibilities": ["responsibility1", "responsibility2", ...],
            "technicalRequirements": ["technical_req1", "technical_req2", ...],
            "softSkills": ["communication", "teamwork", "leadership", ...]
        }
        
        EXTRACTION GUIDELINES:
        1. Extract ALL technical skills, frameworks, programming languages, tools mentioned
        2. Identify both required (must-have) and preferred (nice-to-have) skills
        3. Capture the complete role description and responsibilities
        4. Note experience level requirements (years of experience, seniority level)
        5. Include educational requirements and certifications
        6. Extract soft skills and behavioral requirements
        7. If information is missing, use "Not specified" for strings or empty arrays for lists
        8. Infer job title if not explicitly stated based on responsibilities and requirements
        
        Job Description Text:
        $jobDescriptionText
        """.trimIndent()
    }
    
    private fun createCandidateAnalysisPrompt(
        resumeData: ResumeData,
        jobDescription: JobDescription
    ): String {
        return """
        You are an expert HR analyst and recruiter. Perform a comprehensive analysis of this candidate for the specific job role and return detailed results in JSON format.
        
        CANDIDATE RESUME DATA:
        Name: ${resumeData.name}
        Email: ${resumeData.email}
        Location: ${resumeData.location}
        Summary: ${resumeData.summary}
        Skills: ${resumeData.skills.joinToString(", ")}
        Experience: ${resumeData.experience.joinToString("\n") { "- ${it.title} at ${it.company} (${it.duration}): ${it.description}" }}
        Education: ${resumeData.education.joinToString(", ") { "${it.degree} from ${it.institution} (${it.year})" }}
        
        JOB REQUIREMENTS:
        Title: ${jobDescription.title}
        Company: ${jobDescription.company}
        Description: ${jobDescription.description}
        Required Skills: ${jobDescription.requiredSkills.joinToString(", ")}
        Preferred Skills: ${jobDescription.preferredSkills.joinToString(", ")}
        Experience Level: ${jobDescription.experienceLevel}
        Education Requirements: ${jobDescription.educationRequirements.joinToString(", ")}
        Location: ${jobDescription.location}
        
        Provide a comprehensive analysis in the following JSON format:
        {
            "overallScore": 85.5,
            "scoreBreakdown": {
                "totalScore": 85.5,
                "skillsMatch": 88.0,
                "experienceRelevance": 82.0,
                "educationFit": 95.0,
                "jobSpecificAlignment": 87.0
            },
            "analysisDetails": {
                "jobSpecificScoring": {
                    "requiredSkillsMatch": 88.0,
                    "experienceRelevance": 82.0,
                    "educationFit": 95.0,
                    "jobSpecificAlignment": 87.0
                },
                "skillsAnalysis": {
                    "matchingRequiredSkills": ["skill1", "skill2"],
                    "missingRequiredSkills": ["skill3", "skill4"],
                    "matchingPreferredSkills": ["skill5"],
                    "missingPreferredSkills": ["skill6"]
                },
                "strengthsForRole": ["strength1", "strength2", "strength3"],
                "weaknessesForRole": ["weakness1", "weakness2"],
                "experienceMatch": {
                    "relevantExperienceYears": "5 years",
                    "matchingResponsibilities": ["responsibility1", "responsibility2"],
                    "experienceLevelFit": "Good match for mid-level position",
                    "industryRelevance": "High - strong background in relevant industry"
                },
                "educationAnalysis": {
                    "meetsRequirements": true,
                    "relevantDegrees": ["Computer Science"],
                    "additionalCertificationsNeeded": ["AWS Certification"]
                },
                "interviewFocusAreas": ["Technical skills assessment", "Problem-solving approach"],
                "onboardingRecommendations": ["Provide training on company-specific tools"],
                "salaryFitAssessment": "Candidate expectations likely align with role level and market rate"
            },
            "hiringRecommendation": {
                "decision": "HIRE",
                "confidenceLevel": "High",
                "reasoning": "Strong technical background with relevant experience. Good cultural fit and shows growth potential."
            }
        }
        
        ANALYSIS GUIDELINES:
        1. Score each category out of 100 based on job-specific requirements
        2. Provide specific, actionable insights in each section
        3. Be thorough in skills matching - identify exact matches and gaps
        4. Consider experience relevance, not just years
        5. Hiring decisions: STRONG HIRE (90+), HIRE (75-89), CONSIDER (60-74), REJECT (<60)
        6. Confidence levels: High (80%+), Medium (60-79%), Low (<60%)
        7. Focus on job-specific alignment rather than general qualifications
        """.trimIndent()
    }
    
    private fun cleanJsonResponse(content: String): String {
        var cleaned = content.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json").trim()
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```").trim()
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```").trim()
        }
        return cleaned
    }
    
    // Temporary response model for parsing
    private data class CandidateAnalysisResponse(
        val overallScore: Double,
        val scoreBreakdown: ScoreBreakdown,
        val analysisDetails: AnalysisDetails,
        val hiringRecommendation: HiringRecommendation
    )
} 