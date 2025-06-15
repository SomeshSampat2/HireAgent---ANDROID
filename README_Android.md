# HireAgent Android App

A comprehensive AI-powered hiring assistant Android application that analyzes resumes against job descriptions using Google's Gemini API. This app provides detailed candidate evaluation with scoring, skills matching, and hiring recommendations.

## Features

### 🔍 **AI-Powered Resume Analysis**
- **Document Support**: Upload resumes in PDF, DOCX, or TXT format (up to 10MB)
- **Text Extraction**: Advanced document processing for accurate text extraction
- **Structured Parsing**: AI extracts structured data including:
  - Contact information (name, email, phone, location)
  - Professional summary
  - Skills and technologies
  - Work experience with responsibilities
  - Education background
  - Social profile URLs (LinkedIn, GitHub, Portfolio)

### 💼 **Job Description Processing**
- **Natural Language Processing**: Analyzes job descriptions to extract:
  - Required and preferred skills
  - Experience level requirements
  - Education requirements
  - Key responsibilities
  - Technical requirements
  - Soft skills needed

### 📊 **Comprehensive Candidate Scoring**
- **Overall Match Score**: Composite score out of 100
- **Detailed Breakdown**:
  - Skills Match (%)
  - Experience Relevance (%)
  - Education Fit (%)
  - Job-Specific Alignment (%)

### 📈 **Analysis Dashboard**
- **Multiple Views**:
  - **Overview**: Quick summary and overall scores
  - **Skills**: Detailed skills matching analysis
  - **Experience**: Experience and education evaluation
  - **Interview**: Focus areas and recommendations
  - **Decision**: Hiring recommendation with reasoning

### 🎯 **Smart Recommendations**
- **Interview Focus Areas**: Suggested topics for candidate interviews
- **Onboarding Recommendations**: Areas for new hire training
- **Salary Fit Assessment**: Compensation alignment analysis
- **Hiring Decision**: AI-powered recommendation (HIRE/CONSIDER/REJECT)

## Setup Instructions

### Prerequisites

1. **Android Studio**: Latest version with Kotlin support
2. **Minimum SDK**: API 24 (Android 7.0)
3. **Gemini API Key**: Get your free API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

### Installation Steps

1. **Clone or Download** the project
2. **Open in Android Studio**
3. **Add your Gemini API Key**:
   - Open `local.properties` file in the project root
   - Replace `YOUR_ACTUAL_GEMINI_API_KEY` with your actual API key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```
   - **Note**: This file is not committed to version control for security

4. **Sync Project** with Gradle files
5. **Build and Run** the app

### Getting a Gemini API Key

1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key
5. Paste it in the `local.properties` file

## How to Use

### Step 1: Upload Resume
1. Tap "Select Resume File" 
2. Choose a PDF, DOCX, or TXT file from your device
3. Wait for the AI to process and extract information
4. ✅ Green checkmark indicates successful processing

### Step 2: Enter Job Description
1. Type or paste the job description in the text field
2. Include requirements, responsibilities, and qualifications
3. Minimum 50 characters required
4. ✅ Automatic processing when criteria met

### Step 3: Start Analysis
1. Tap "Start AI Analysis" button
2. Wait for the comprehensive analysis (30-60 seconds)
3. View detailed results across multiple tabs

### Step 4: Review Results
Navigate through the tabs to see:
- **Overview**: Scores and quick summary
- **Skills**: Matching and missing skills
- **Experience**: Relevance analysis
- **Interview**: Suggested focus areas
- **Decision**: Final hiring recommendation

## App Architecture

### 🏗️ **Clean Architecture**
- **MVVM Pattern**: ViewModel manages UI state and business logic
- **Repository Pattern**: Centralized data handling
- **Compose UI**: Modern declarative UI framework

### 📦 **Key Components**
- **Document Processor**: Extracts text from various file formats
- **Gemini API Service**: Direct integration with Google's AI
- **UI Components**: Modular, reusable interface elements
- **State Management**: Reactive UI updates with StateFlow

### 🔧 **Dependencies**
- **Retrofit**: HTTP client for API calls
- **Gson**: JSON parsing
- **Apache POI**: Document processing (DOCX, DOC)
- **iText**: PDF text extraction
- **Compose**: Modern UI toolkit
- **Material 3**: Latest design system

## API Usage

The app directly calls Google's Gemini API using these endpoints:
- **Model**: `gemini-2.0-flash`
- **Endpoint**: `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`
- **Authentication**: API key via query parameter

### Request Flow
1. **Resume Processing**: Extract text → AI parsing → Structured data
2. **Job Analysis**: Text analysis → Requirements extraction
3. **Candidate Matching**: Compare data → Generate scores → Provide recommendations

## Design Features

### 🎨 **Modern UI/UX**
- **Material 3 Design**: Latest Google design guidelines
- **Responsive Layout**: Optimized for various screen sizes
- **Accessibility**: Screen reader compatible
- **Dark/Light Theme**: System theme support

### 📱 **Mobile-First Design**
- **Touch-Friendly**: Large tap targets and intuitive gestures
- **Progressive Disclosure**: Information revealed step-by-step
- **Visual Feedback**: Loading states and success indicators
- **Error Handling**: User-friendly error messages

### 🎯 **Key UI Elements**
- **Progress Indicator**: Shows current step (Input → Analysis → Results)
- **Score Visualizations**: Circular and linear progress indicators
- **Interactive Tabs**: Easy navigation between result sections
- **Chip Components**: Skills displayed as interactive chips
- **Cards Layout**: Information organized in digestible cards

## Permissions

The app requires the following permissions:
- `INTERNET`: For API calls to Gemini
- `READ_EXTERNAL_STORAGE`: For file access

## File Support

| Format | Extension | Max Size | Notes |
|--------|-----------|----------|-------|
| PDF | .pdf | 10MB | Full text extraction |
| Word Document | .docx | 10MB | Text and tables |
| Legacy Word | .doc | 10MB | Basic text extraction |
| Plain Text | .txt | 10MB | Direct text reading |

## Troubleshooting

### Common Issues

1. **"API call failed"**
   - Check your internet connection
   - Verify API key is correct
   - Ensure API key has Gemini access

2. **"Failed to process resume"**
   - Check file format (PDF/DOCX/TXT only)
   - Ensure file size is under 10MB
   - Try with a different file

3. **"No text could be extracted"**
   - File may be image-based PDF
   - Try OCR preprocessing
   - Use text-searchable documents

### Performance Tips

- **File Size**: Smaller files process faster
- **Network**: Use Wi-Fi for best performance
- **Memory**: Close other apps during analysis

## Future Enhancements

- [ ] **OCR Support**: Image-based PDF processing
- [ ] **Batch Processing**: Multiple resumes at once
- [ ] **Export Results**: PDF/Email sharing
- [ ] **Comparison Mode**: Side-by-side candidate comparison
- [ ] **Template Library**: Pre-built job description templates
- [ ] **Analytics Dashboard**: Hiring trends and insights

## Security & Privacy

- **Local Processing**: Files processed locally before API calls
- **No Data Storage**: No resume or job data stored on device
- **API Security**: Secure HTTPS communication
- **Permissions**: Minimal permissions requested

## Support

For issues or questions:
1. Check this README first
2. Review common troubleshooting steps
3. Ensure API key is properly configured
4. Verify internet connectivity

## License

This project is for demonstration purposes. Ensure compliance with:
- Google's Gemini API Terms of Service
- Your organization's data privacy policies
- Applicable employment and privacy laws

---

**Note**: This app is designed for HR professionals and hiring managers to streamline the candidate evaluation process. Always use AI recommendations as one factor in hiring decisions, not as the sole determinant. 