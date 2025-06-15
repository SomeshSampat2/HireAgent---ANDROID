package com.example.agenthire.utils

import android.content.Context
import android.net.Uri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.IOException
import java.io.InputStream

class DocumentProcessor(private val context: Context) {
    
    /**
     * Extract text from various document formats
     */
    suspend fun extractTextFromDocument(uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Unable to open file"))
            
            val mimeType = context.contentResolver.getType(uri)
            val fileName = getFileName(uri)
            
            val text = when {
                mimeType?.contains("pdf") == true || fileName?.endsWith(".pdf") == true -> {
                    extractTextFromPdf(inputStream)
                }
                mimeType?.contains("wordprocessingml") == true || fileName?.endsWith(".docx") == true -> {
                    extractTextFromDocx(inputStream)
                }
                mimeType?.contains("msword") == true || fileName?.endsWith(".doc") == true -> {
                    extractTextFromDoc(inputStream)
                }
                mimeType?.startsWith("text/") == true || fileName?.endsWith(".txt") == true -> {
                    extractTextFromTxt(inputStream)
                }
                else -> {
                    // Try to read as plain text as fallback
                    extractTextFromTxt(inputStream)
                }
            }
            
            inputStream.close()
            
            if (text.isBlank()) {
                Result.failure(Exception("No text could be extracted from the document"))
            } else {
                Result.success(text.trim())
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("Error processing document: ${e.message}"))
        }
    }
    
    private fun extractTextFromPdf(inputStream: InputStream): String {
        return try {
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)
            val text = StringBuilder()
            
            for (i in 1..pdfDocument.numberOfPages) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i)))
                text.append("\n")
            }
            
            pdfDocument.close()
            text.toString()
        } catch (e: Exception) {
            throw Exception("Failed to extract text from PDF: ${e.message}")
        }
    }
    
    private fun extractTextFromDocx(inputStream: InputStream): String {
        return try {
            val document = XWPFDocument(inputStream)
            val text = StringBuilder()
            
            document.paragraphs.forEach { paragraph ->
                text.append(paragraph.text)
                text.append("\n")
            }
            
            // Also extract text from tables
            document.tables.forEach { table ->
                table.rows.forEach { row ->
                    row.tableCells.forEach { cell ->
                        text.append(cell.text)
                        text.append(" ")
                    }
                    text.append("\n")
                }
            }
            
            document.close()
            text.toString()
        } catch (e: Exception) {
            throw Exception("Failed to extract text from DOCX: ${e.message}")
        }
    }
    
    private fun extractTextFromDoc(inputStream: InputStream): String {
        return try {
            val document = HWPFDocument(inputStream)
            val text = document.documentText
            document.close()
            text
        } catch (e: Exception) {
            throw Exception("Failed to extract text from DOC: ${e.message}")
        }
    }
    
    private fun extractTextFromTxt(inputStream: InputStream): String {
        return try {
            inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
        } catch (e: Exception) {
            throw Exception("Failed to extract text from TXT: ${e.message}")
        }
    }
    
    private fun getFileName(uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex("_display_name")
                    if (displayNameIndex >= 0) {
                        return it.getString(displayNameIndex)
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Validate if the file is a supported document type
     */
    fun isSupportedDocument(uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        val fileName = getFileName(uri)
        
        return when {
            mimeType?.contains("pdf") == true -> true
            mimeType?.contains("wordprocessingml") == true -> true
            mimeType?.contains("msword") == true -> true
            mimeType?.startsWith("text/") == true -> true
            fileName?.endsWith(".pdf", ignoreCase = true) == true -> true
            fileName?.endsWith(".docx", ignoreCase = true) == true -> true
            fileName?.endsWith(".doc", ignoreCase = true) == true -> true
            fileName?.endsWith(".txt", ignoreCase = true) == true -> true
            else -> false
        }
    }
    
    /**
     * Get file size in bytes
     */
    fun getFileSize(uri: Uri): Long {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex("_size")
                    if (sizeIndex >= 0) {
                        return it.getLong(sizeIndex)
                    }
                }
            }
            0L
        } catch (e: Exception) {
            0L
        }
    }
    
    companion object {
        const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10MB
        
        val SUPPORTED_MIME_TYPES = setOf(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "text/plain"
        )
    }
} 