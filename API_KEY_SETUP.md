# API Key Setup Guide

## Overview
The Gemini API key is now securely configured using Android's best practices with `local.properties` and `BuildConfig`.

## Setup Steps

### 1. Get Your Gemini API Key
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key

### 2. Configure the API Key
1. Open `local.properties` file in the project root
2. Replace `YOUR_ACTUAL_GEMINI_API_KEY` with your actual API key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```

### 3. Build and Run
- The API key will be automatically injected into `BuildConfig.GEMINI_API_KEY`
- No code changes needed - the app will read it automatically

## Security Features
- ✅ API key stored in `local.properties` (not committed to version control)
- ✅ Accessed through `BuildConfig` at compile time
- ✅ No hardcoded secrets in source code
- ✅ Follows Android security best practices

## File Structure
```
AgentHire2/
├── local.properties          # Contains API key (NOT in version control)
├── app/build.gradle.kts      # Reads from local.properties
└── app/src/main/java/com/example/agenthire/
    └── MainActivity.kt       # Uses BuildConfig.GEMINI_API_KEY
```

## How It Works
1. **Build Time**: Gradle reads `GEMINI_API_KEY` from `local.properties`
2. **Compile Time**: Value is injected into `BuildConfig.GEMINI_API_KEY`
3. **Runtime**: App accesses the key via `BuildConfig.GEMINI_API_KEY`

## Important Notes
- ⚠️ Never commit `local.properties` to version control
- ⚠️ Each developer needs their own API key
- ⚠️ For production, use secure key management systems
- ✅ The `local.properties` file is already added to `.gitignore` 