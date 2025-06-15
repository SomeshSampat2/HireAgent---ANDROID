package com.example.agenthire.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthire.data.models.*
import kotlinx.coroutines.delay
import com.example.agenthire.data.repository.HireAgentRepository
import com.example.agenthire.utils.DocumentProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HireAgentViewModel(
    private val context: Context,
    private val apiKey: String
) : ViewModel() {
    
    private val repository = HireAgentRepository(context, apiKey)
    private val documentProcessor = DocumentProcessor(context)
    
    // UI State
    private val _uiState = MutableStateFlow(HireAgentUiState())
    val uiState: StateFlow<HireAgentUiState> = _uiState.asStateFlow()
    
    // Analysis State
    private val _analysisState = MutableStateFlow(AnalysisState())
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()
    
    /**
     * Handle resume file selection - ONLY validates file, no processing
     */
    fun onResumeSelected(uri: Uri) {
        // Only validate file format and size, don't process yet
        if (!documentProcessor.isSupportedDocument(uri)) {
            _uiState.value = _uiState.value.copy(
                resumeError = "Unsupported file format. Please select PDF, DOCX, DOC, or TXT files."
            )
            return
        }
        
        val fileSize = documentProcessor.getFileSize(uri)
        if (fileSize > DocumentProcessor.MAX_FILE_SIZE) {
            _uiState.value = _uiState.value.copy(
                resumeError = "File size too large. Maximum size is 10MB."
            )
            return
        }
        
        // File is valid - store it for later processing
        _uiState.value = _uiState.value.copy(
            selectedResumeUri = uri,
            resumeFileName = getFileNameFromUri(uri),
            resumeError = null,
            resumeValidated = true
        )
    }
    
    /**
     * Handle job description input - ONLY validates text, no processing
     */
    fun onJobDescriptionChanged(jobDescription: String) {
        val trimmedText = jobDescription.trim()
        val isValid = trimmedText.length >= 50
        
        _uiState.value = _uiState.value.copy(
            jobDescriptionText = jobDescription,
            jobDescriptionError = if (!isValid && trimmedText.isNotEmpty()) {
                "Please provide a more detailed job description (minimum 50 characters)"
            } else null,
            jobDescriptionValidated = isValid
        )
    }
    
    /**
     * Start comprehensive analysis - ALL processing happens here
     */
    fun startAnalysis() {
        val currentState = _uiState.value
        
        // Validate inputs
        if (currentState.selectedResumeUri == null) {
            _uiState.value = currentState.copy(resumeError = "Please select a resume file")
            return
        }
        
        if (!currentState.jobDescriptionValidated) {
            _uiState.value = currentState.copy(
                jobDescriptionError = "Please provide a detailed job description (minimum 50 characters)"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                // Start analysis
                _analysisState.value = AnalysisState(isAnalyzing = true)
                _uiState.value = currentState.copy(currentStep = AnalysisStep.ANALYZING)
                
                // Step 1: Extract text from resume document
                updateAnalysisProgress("Extracting text from resume...")
                delay(2000) // Show progress for 2 seconds
                val textResult = documentProcessor.extractTextFromDocument(currentState.selectedResumeUri!!)
                if (textResult.isFailure) {
                    throw Exception("Failed to extract text from resume: ${textResult.exceptionOrNull()?.message}")
                }
                val resumeText = textResult.getOrThrow()
                
                // Step 2: Parse resume with AI
                updateAnalysisProgress("Analyzing resume structure and content...")
                delay(3000) // Show progress for 3 seconds
                val resumeResult = repository.parseResume(resumeText)
                if (resumeResult.isFailure) {
                    throw Exception("Failed to parse resume: ${resumeResult.exceptionOrNull()?.message}")
                }
                val resumeData = resumeResult.getOrThrow()
                
                // Step 3: Parse job description with AI
                updateAnalysisProgress("Analyzing job description and requirements...")
                delay(2500) // Show progress for 2.5 seconds
                val jobResult = repository.parseJobDescription(currentState.jobDescriptionText.trim())
                if (jobResult.isFailure) {
                    throw Exception("Failed to parse job description: ${jobResult.exceptionOrNull()?.message}")
                }
                val jobDescription = jobResult.getOrThrow()
                
                // Step 4: Perform comprehensive candidate analysis
                updateAnalysisProgress("Matching candidate profile to job requirements...")
                delay(3500) // Show progress for 3.5 seconds
                val analysisResult = repository.analyzeCandidateForJob(resumeData, jobDescription)
                if (analysisResult.isFailure) {
                    throw Exception("Failed to analyze candidate: ${analysisResult.exceptionOrNull()?.message}")
                }
                
                // Step 5: Finalizing report
                updateAnalysisProgress("Finalizing comprehensive analysis report...")
                delay(2000) // Show final progress for 2 seconds
                
                // Success - show results
                _analysisState.value = AnalysisState(
                    isAnalyzing = false,
                    analysisResult = analysisResult.getOrThrow()
                )
                _uiState.value = _uiState.value.copy(currentStep = AnalysisStep.RESULTS)
                
            } catch (e: Exception) {
                // Error occurred
                _analysisState.value = AnalysisState(
                    isAnalyzing = false,
                    error = e.message ?: "Analysis failed. Please try again."
                )
                _uiState.value = _uiState.value.copy(currentStep = AnalysisStep.INPUT)
            }
        }
    }
    
    private fun updateAnalysisProgress(message: String) {
        _analysisState.value = _analysisState.value.copy(progressMessage = message)
    }
    
    /**
     * Reset to start a new analysis
     */
    fun startNewAnalysis() {
        _uiState.value = HireAgentUiState()
        _analysisState.value = AnalysisState()
    }
    
    /**
     * Clear resume selection
     */
    fun clearResume() {
        _uiState.value = _uiState.value.copy(
            selectedResumeUri = null,
            resumeFileName = null,
            resumeValidated = false,
            resumeError = null
        )
    }
    
    /**
     * Clear job description
     */
    fun clearJobDescription() {
        _uiState.value = _uiState.value.copy(
            jobDescriptionText = "",
            jobDescriptionValidated = false,
            jobDescriptionError = null
        )
    }
    
    /**
     * Check if analysis can be started
     */
    fun canStartAnalysis(): Boolean {
        val state = _uiState.value
        return state.resumeValidated && 
               state.jobDescriptionValidated && 
               !_analysisState.value.isAnalyzing
    }
    
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex("_display_name")
                if (displayNameIndex >= 0) {
                    it.getString(displayNameIndex)
                } else {
                    "Selected file"
                }
            } else {
                "Selected file"
            }
        } ?: "Selected file"
    }
}

// UI State Model
data class HireAgentUiState(
    val currentStep: AnalysisStep = AnalysisStep.INPUT,
    
    // Resume state
    val selectedResumeUri: Uri? = null,
    val resumeFileName: String? = null,
    val resumeValidated: Boolean = false,
    val resumeError: String? = null,
    
    // Job description state
    val jobDescriptionText: String = "",
    val jobDescriptionValidated: Boolean = false,
    val jobDescriptionError: String? = null
)

// Analysis State Model
data class AnalysisState(
    val isAnalyzing: Boolean = false,
    val progressMessage: String = "",
    val analysisResult: CandidateAnalysis? = null,
    val error: String? = null
)

enum class AnalysisStep {
    INPUT,
    ANALYZING,
    RESULTS
} 