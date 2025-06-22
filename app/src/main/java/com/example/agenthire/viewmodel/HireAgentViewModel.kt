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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class HireAgentViewModel(
    private val context: Context,
    private val apiKey: String
) : ViewModel() {
    
    private val repository = HireAgentRepository(context, apiKey)
    private val documentProcessor = DocumentProcessor(context)
    
    // UI State
    private val _uiState = MutableStateFlow(MultiResumeUiState())
    val uiState: StateFlow<MultiResumeUiState> = _uiState.asStateFlow()
    
    // Batch Analysis State
    private val _batchAnalysisState = MutableStateFlow(BatchAnalysisState())
    val batchAnalysisState: StateFlow<BatchAnalysisState> = _batchAnalysisState.asStateFlow()
    
    companion object {
        const val MIN_RESUMES = 1
        const val MAX_RESUMES = 10
        const val MIN_JOB_DESCRIPTION_LENGTH = 50
    }
    
    /**
     * Handle multiple resume file selection
     */
    fun onResumesSelected(uris: List<Uri>) {
        val currentState = _uiState.value
        
        // Validate number of files
        if (uris.isEmpty()) {
            _uiState.value = currentState.copy(
                resumeError = "Please select at least one resume file"
            )
            return
        }
        
        // Get currently selected files and check total count
        val existingFiles = currentState.selectedResumeFiles
        val totalFilesAfterAdding = existingFiles.size + uris.size
        
        if (totalFilesAfterAdding > MAX_RESUMES) {
            _uiState.value = currentState.copy(
                resumeError = "Maximum $MAX_RESUMES resumes allowed. You have ${existingFiles.size} files and tried to add ${uris.size} more."
            )
            return
        }
        
        val newResumeFiles = mutableListOf<ResumeFile>()
        var hasError = false
        var errorMessage = ""
        
        // Validate each NEW file and check for duplicates
        for (uri in uris) {
            // Check if file is already selected
            if (existingFiles.any { it.uri == uri }) {
                continue // Skip duplicate files
            }
            
            if (!documentProcessor.isSupportedDocument(uri)) {
                hasError = true
                errorMessage = "Unsupported file format detected. Please select PDF, DOCX, DOC, or TXT files only."
                break
        }
        
        val fileSize = documentProcessor.getFileSize(uri)
        if (fileSize > DocumentProcessor.MAX_FILE_SIZE) {
                hasError = true
                errorMessage = "One or more files exceed the 10MB limit."
                break
            }
            
            val fileName = getFileNameFromUri(uri)
            newResumeFiles.add(
                ResumeFile(
                    uri = uri,
                    fileName = fileName,
                    fileSize = fileSize
                )
            )
        }
        
        if (hasError) {
            _uiState.value = currentState.copy(
                resumeError = errorMessage
            )
            return
        }
        
        // Combine existing files with new files
        val allFiles = existingFiles + newResumeFiles
        
        _uiState.value = currentState.copy(
            selectedResumeFiles = allFiles,
            resumeError = null,
            resumesValidated = allFiles.isNotEmpty()
        )
    }
    
    /**
     * Remove a specific resume file
     */
    fun removeResumeFile(resumeFile: ResumeFile) {
        val currentState = _uiState.value
        val updatedFiles = currentState.selectedResumeFiles.filterNot { it.uri == resumeFile.uri }
        
        _uiState.value = currentState.copy(
            selectedResumeFiles = updatedFiles,
            resumesValidated = updatedFiles.isNotEmpty(),
            resumeError = if (updatedFiles.isEmpty()) "Please select at least one resume file" else null
        )
    }
    
    /**
     * Handle job description input - ONLY validates text, no processing
     */
    fun onJobDescriptionChanged(jobDescription: String) {
        val trimmedText = jobDescription.trim()
        val isValid = trimmedText.length >= MIN_JOB_DESCRIPTION_LENGTH
        
        _uiState.value = _uiState.value.copy(
            jobDescriptionText = jobDescription,
            jobDescriptionError = if (!isValid && trimmedText.isNotEmpty()) {
                "Please provide a more detailed job description (minimum $MIN_JOB_DESCRIPTION_LENGTH characters)"
            } else null,
            jobDescriptionValidated = isValid
        )
    }
    
    /**
     * Start batch analysis of all resumes
     */
    fun startBatchAnalysis() {
        val currentState = _uiState.value
        
        // Validate inputs
        if (currentState.selectedResumeFiles.isEmpty()) {
            _uiState.value = currentState.copy(resumeError = "Please select at least one resume file")
            return
        }
        
        if (!currentState.jobDescriptionValidated) {
            _uiState.value = currentState.copy(
                jobDescriptionError = "Please provide a detailed job description (minimum $MIN_JOB_DESCRIPTION_LENGTH characters)"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                // Start batch analysis
                _batchAnalysisState.value = BatchAnalysisState(
                    isAnalyzing = true,
                    totalCount = currentState.selectedResumeFiles.size
                )
                _uiState.value = currentState.copy(currentStep = MultiAnalysisStep.ANALYZING)
                
                // Step 1: Parse job description first
                updateBatchProgress("Analyzing job description and requirements...", 0, 0f)
                delay(2000)
                
                val jobResult = repository.parseJobDescription(currentState.jobDescriptionText.trim())
                if (jobResult.isFailure) {
                    throw Exception("Failed to parse job description: ${jobResult.exceptionOrNull()?.message}")
                }
                val jobDescription = jobResult.getOrThrow()
                
                // Step 2: Process all resumes in parallel for text extraction
                updateBatchProgress("Extracting text from all resume files...", 0, 10f)
                delay(1500)
                
                val processedResumes = mutableListOf<ResumeFile>()
                
                // Extract text from all resumes
                for ((index, resumeFile) in currentState.selectedResumeFiles.withIndex()) {
                    updateBatchProgress(
                        "Extracting text from ${resumeFile.fileName}...", 
                        index + 1, 
                        10f + (20f * (index + 1) / currentState.selectedResumeFiles.size)
                    )
                    
                    val textResult = documentProcessor.extractTextFromDocument(resumeFile.uri)
                    if (textResult.isFailure) {
                        processedResumes.add(
                            resumeFile.copy(
                                error = "Failed to extract text: ${textResult.exceptionOrNull()?.message}"
                            )
                        )
                    } else {
                        processedResumes.add(
                            resumeFile.copy(
                                extractedText = textResult.getOrThrow(),
                                isProcessed = true
                            )
                        )
                    }
                    delay(500) // Small delay for progress visibility
                }
                
                // Step 3: Parse resumes with AI (sequential to avoid rate limits)
                val candidateAnalyses = mutableListOf<CandidateAnalysis>()
                val successfulResumes = processedResumes.filter { it.isProcessed && it.extractedText != null }
                
                for ((index, resumeFile) in successfulResumes.withIndex()) {
                    updateBatchProgress(
                        "Analyzing resume structure for ${resumeFile.fileName}...", 
                        index + 1, 
                        30f + (30f * (index + 1) / successfulResumes.size)
                    )
                    delay(1000)
                    
                    val resumeResult = repository.parseResume(resumeFile.extractedText!!)
                    if (resumeResult.isFailure) {
                        continue // Skip this resume but continue with others
                    }
                    val resumeData = resumeResult.getOrThrow()
                    
                    // Step 4: Perform candidate analysis
                    updateBatchProgress(
                        "Matching ${resumeData.name} to job requirements...", 
                        index + 1, 
                        60f + (30f * (index + 1) / successfulResumes.size)
                    )
                    delay(1500)
                    
                val analysisResult = repository.analyzeCandidateForJob(resumeData, jobDescription)
                    if (analysisResult.isSuccess) {
                        candidateAnalyses.add(analysisResult.getOrThrow())
                    }
                }
                
                // Step 5: Rank candidates by overall score
                updateBatchProgress("Ranking candidates by overall match score...", candidateAnalyses.size, 95f)
                delay(1000)
                
                val rankedCandidates = candidateAnalyses
                    .sortedByDescending { it.overallScore }
                    .mapIndexed { index, analysis ->
                        val resumeFile = processedResumes.find { it.extractedText != null && 
                                                                 analysis.resumeData.name.contains(getFileNameFromUri(it.uri).take(10), ignoreCase = true) }
                            ?: processedResumes.first()
                        
                        RankedCandidate(
                            resumeFile = resumeFile,
                            candidateAnalysis = analysis,
                            rank = index + 1,
                            overallScore = analysis.overallScore
                        )
                    }
                
                // Final step
                updateBatchProgress("Finalizing analysis report...", rankedCandidates.size, 100f)
                delay(1000)
                
                // Success - show ranked results
                _batchAnalysisState.value = BatchAnalysisState(
                    isAnalyzing = false,
                    processedCount = rankedCandidates.size,
                    totalCount = currentState.selectedResumeFiles.size,
                    rankedResults = rankedCandidates
                )
                _uiState.value = _uiState.value.copy(currentStep = MultiAnalysisStep.RANKED_RESULTS)
                
            } catch (e: Exception) {
                // Error occurred
                _batchAnalysisState.value = BatchAnalysisState(
                    isAnalyzing = false,
                    error = e.message ?: "Batch analysis failed. Please try again."
                )
                _uiState.value = _uiState.value.copy(currentStep = MultiAnalysisStep.INPUT)
            }
        }
    }
    
    /**
     * Show detailed analysis for a specific candidate
     */
    fun showCandidateDetail(rankedCandidate: RankedCandidate) {
        _uiState.value = _uiState.value.copy(
            currentStep = MultiAnalysisStep.DETAILED_VIEW,
            selectedCandidate = rankedCandidate
        )
    }
    
    /**
     * Go back to ranked results
     */
    fun backToRankedResults() {
        _uiState.value = _uiState.value.copy(
            currentStep = MultiAnalysisStep.RANKED_RESULTS,
            selectedCandidate = null
        )
    }
    
    private fun updateBatchProgress(message: String, processed: Int, percentage: Float) {
        _batchAnalysisState.value = _batchAnalysisState.value.copy(
            currentProcessing = message,
            processedCount = processed,
            progressPercentage = percentage / 100f
        )
    }
    
    /**
     * Reset to start a new analysis
     */
    fun startNewAnalysis() {
        _uiState.value = MultiResumeUiState()
        _batchAnalysisState.value = BatchAnalysisState()
    }
    
    /**
     * Clear all resume selections
     */
    fun clearAllResumes() {
        _uiState.value = _uiState.value.copy(
            selectedResumeFiles = emptyList(),
            resumesValidated = false,
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
     * Check if batch analysis can be started
     */
    fun canStartAnalysis(): Boolean {
        val state = _uiState.value
        return state.resumesValidated && 
               state.jobDescriptionValidated && 
               !_batchAnalysisState.value.isAnalyzing &&
               state.selectedResumeFiles.isNotEmpty()
    }
    
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex("_display_name")
                if (displayNameIndex >= 0) {
                    it.getString(displayNameIndex)
                } else {
                    "Resume File"
                }
            } else {
                "Resume File"
            }
        } ?: "Resume File"
    }
}

// NEW UI State Model for Multiple Resumes
data class MultiResumeUiState(
    val currentStep: MultiAnalysisStep = MultiAnalysisStep.INPUT,
    
    // Resume state
    val selectedResumeFiles: List<ResumeFile> = emptyList(),
    val resumesValidated: Boolean = false,
    val resumeError: String? = null,
    
    // Job description state
    val jobDescriptionText: String = "",
    val jobDescriptionValidated: Boolean = false,
    val jobDescriptionError: String? = null,
    
    // Selected candidate for detailed view
    val selectedCandidate: RankedCandidate? = null
)

// Keep the old models for backward compatibility if needed
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

enum class AnalysisStep {
    INPUT,
    ANALYZING,
    RESULTS
} 