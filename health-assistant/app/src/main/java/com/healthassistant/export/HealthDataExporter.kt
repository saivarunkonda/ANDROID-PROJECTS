package com.healthassistant.export

import android.content.Context
import android.os.Environment
import androidx.core.content.FileProvider
import com.healthassistant.health.HealthMetrics
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthDataExporter @Inject constructor(
    private val context: Context
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val fileNameDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    suspend fun exportToCSV(healthMetrics: List<HealthMetrics>): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "health_data_${fileNameDateFormat.format(Date())}.csv"
                val file = createFile(fileName)
                
                CSVWriter(FileWriter(file)).use { csvWriter ->
                    // Write header
                    csvWriter.writeNext(
                        arrayOf(
                            "Timestamp",
                            "Steps",
                            "Heart Rate (bpm)",
                            "Sleep Hours",
                            "Sleep Quality (%)",
                            "Active Calories",
                            "Systolic BP (mmHg)",
                            "Diastolic BP (mmHg)",
                            "Oxygen Saturation (%)",
                            "Weight (kg)"
                        )
                    )
                    
                    // Write data
                    healthMetrics.forEach { metrics ->
                        csvWriter.writeNext(
                            arrayOf(
                                dateFormat.format(Date(metrics.lastSync)),
                                metrics.steps.toString(),
                                metrics.heartRate?.toString() ?: "",
                                metrics.sleepHours.toString(),
                                (metrics.sleepQuality * 100).toString(),
                                metrics.activeCalories.toString(),
                                metrics.systolicBP?.toString() ?: "",
                                metrics.diastolicBP?.toString() ?: "",
                                metrics.oxygenSaturation?.toString() ?: "",
                                metrics.weight?.toString() ?: ""
                            )
                        )
                    }
                }
                
                ExportResult(
                    success = true,
                    filePath = file.absolutePath,
                    fileName = fileName,
                    mimeType = "text/csv",
                    message = "Health data exported successfully to CSV"
                )
                
            } catch (e: Exception) {
                ExportResult(
                    success = false,
                    filePath = null,
                    fileName = null,
                    mimeType = null,
                    message = "Failed to export CSV: ${e.message}"
                )
            }
        }
    }
    
    suspend fun exportToPDF(healthMetrics: List<HealthMetrics>): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "health_report_${fileNameDateFormat.format(Date())}.pdf"
                val file = createFile(fileName)
                
                val pdfWriter = PdfWriter(file)
                val pdfDocument = PdfDocument(pdfWriter)
                val document = Document(pdfDocument)
                
                // Add title
                document.add(
                    Paragraph("Health Data Report")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(20f)
                        .setMarginBottom(20f)
                )
                
                // Add generation date
                document.add(
                    Paragraph("Generated on: ${dateFormat.format(Date())}")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(30f)
                )
                
                // Add summary statistics
                addSummarySection(document, healthMetrics)
                
                // Add detailed data table
                addDataTable(document, healthMetrics)
                
                // Add recommendations
                addRecommendationsSection(document, healthMetrics)
                
                document.close()
                pdfDocument.close()
                
                ExportResult(
                    success = true,
                    filePath = file.absolutePath,
                    fileName = fileName,
                    mimeType = "application/pdf",
                    message = "Health report generated successfully"
                )
                
            } catch (e: Exception) {
                ExportResult(
                    success = false,
                    filePath = null,
                    fileName = null,
                    mimeType = null,
                    message = "Failed to generate PDF: ${e.message}"
                )
            }
        }
    }
    
    private fun addSummarySection(document: Document, healthMetrics: List<HealthMetrics>) {
        document.add(
            Paragraph("Summary Statistics")
                .setFontSize(16f)
                .setMarginBottom(10f)
        )
        
        val summaryTable = Table(floatArrayOf(2f, 1f, 1f)).useAllAvailableWidth()
        
        // Add header row
        summaryTable.addCell("Metric")
        summaryTable.addCell("Average")
        summaryTable.addCell("Latest")
        
        // Calculate averages and add data
        if (healthMetrics.isNotEmpty()) {
            val avgSteps = healthMetrics.map { it.steps }.average()
            val avgHeartRate = healthMetrics.mapNotNull { it.heartRate }.average()
            val avgSleepHours = healthMetrics.map { it.sleepHours }.average()
            val avgActiveCalories = healthMetrics.map { it.activeCalories }.average()
            
            val latest = healthMetrics.lastOrNull()
            
            summaryTable.addCell("Steps")
            summaryTable.addCell(String.format("%.0f", avgSteps))
            summaryTable.addCell(latest?.steps?.toString() ?: "N/A")
            
            summaryTable.addCell("Heart Rate")
            summaryTable.addCell(String.format("%.0f", avgHeartRate))
            summaryTable.addCell(latest?.heartRate?.toString() ?: "N/A")
            
            summaryTable.addCell("Sleep Hours")
            summaryTable.addCell(String.format("%.1f", avgSleepHours))
            summaryTable.addCell(String.format("%.1f", latest?.sleepHours ?: 0.0))
            
            summaryTable.addCell("Active Calories")
            summaryTable.addCell(String.format("%.0f", avgActiveCalories))
            summaryTable.addCell(latest?.activeCalories?.toString() ?: "N/A")
        }
        
        document.add(summaryTable.setMarginBottom(20f))
    }
    
    private fun addDataTable(document: Document, healthMetrics: List<HealthMetrics>) {
        document.add(
            Paragraph("Detailed Health Data")
                .setFontSize(16f)
                .setMarginBottom(10f)
        )
        
        val dataTable = Table(floatArrayOf(
            1.5f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f
        )).useAllAvailableWidth()
        
        // Add header row
        dataTable.addCell("Date/Time")
        dataTable.addCell("Steps")
        dataTable.addCell("HR")
        dataTable.addCell("Sleep")
        dataTable.addCell("Quality")
        dataTable.addCell("Calories")
        dataTable.addCell("Sys BP")
        dataTable.addCell("Dia BP")
        dataTable.addCell("O2")
        dataTable.addCell("Weight")
        
        // Add data rows
        healthMetrics.takeLast(20).forEach { metrics ->
            dataTable.addCell(dateFormat.format(Date(metrics.lastSync)))
            dataTable.addCell(String.format("%.0f", metrics.steps))
            dataTable.addCell(metrics.heartRate?.let { String.format("%.0f", it) } ?: "N/A")
            dataTable.addCell(String.format("%.1f", metrics.sleepHours))
            dataTable.addCell(String.format("%.0f%%", metrics.sleepQuality * 100))
            dataTable.addCell(String.format("%.0f", metrics.activeCalories))
            dataTable.addCell(metrics.systolicBP?.let { String.format("%.0f", it) } ?: "N/A")
            dataTable.addCell(metrics.diastolicBP?.let { String.format("%.0f", it) } ?: "N/A")
            dataTable.addCell(metrics.oxygenSaturation?.let { String.format("%.0f%%", it) } ?: "N/A")
            dataTable.addCell(metrics.weight?.let { String.format("%.1f", it) } ?: "N/A")
        }
        
        document.add(dataTable.setMarginBottom(20f))
    }
    
    private fun addRecommendationsSection(document: Document, healthMetrics: List<HealthMetrics>) {
        document.add(
            Paragraph("Health Recommendations")
                .setFontSize(16f)
                .setMarginBottom(10f)
        )
        
        val recommendations = generateRecommendations(healthMetrics)
        
        recommendations.forEach { recommendation ->
            document.add(
                Paragraph("• $recommendation")
                    .setMarginLeft(20f)
                    .setMarginBottom(5f)
            )
        }
    }
    
    private fun generateRecommendations(healthMetrics: List<HealthMetrics>): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (healthMetrics.isNotEmpty()) {
            val latest = healthMetrics.lastOrNull()
            val avgSteps = healthMetrics.map { it.steps }.average()
            val avgSleepHours = healthMetrics.map { it.sleepHours }.average()
            
            // Steps recommendations
            when {
                avgSteps < 5000 -> recommendations.add("Consider increasing daily physical activity. Aim for at least 10,000 steps per day.")
                avgSteps < 8000 -> recommendations.add("Good progress! Try to increase daily steps to reach the 10,000 step goal.")
                else -> recommendations.add("Excellent! You're meeting the recommended daily step count.")
            }
            
            // Sleep recommendations
            when {
                avgSleepHours < 6 -> recommendations.add("Sleep duration is below recommended levels. Aim for 7-9 hours of sleep per night.")
                avgSleepHours > 10 -> recommendations.add("You may be getting too much sleep. Consider maintaining 7-9 hours for optimal health.")
                else -> recommendations.add("Good sleep duration! Keep maintaining consistent sleep patterns.")
            }
            
            // Heart rate recommendations
            latest?.heartRate?.let { hr ->
                when {
                    hr < 60 -> recommendations.add("Resting heart rate is low. This may indicate good cardiovascular fitness, but consult your doctor if you have concerns.")
                    hr > 100 -> recommendations.add("Resting heart rate is elevated. Consider stress management techniques and consult your healthcare provider.")
                    else -> recommendations.add("Resting heart rate is within normal range.")
                }
            }
            
            // Blood pressure recommendations
            latest?.systolicBP?.let { systolic ->
                latest?.diastolicBP?.let { diastolic ->
                    when {
                        systolic > 140 || diastolic > 90 -> recommendations.add("Blood pressure is elevated. Consider lifestyle changes and consult your healthcare provider.")
                        systolic < 90 || diastolic < 60 -> recommendations.add("Blood pressure is low. If you experience symptoms, consult your healthcare provider.")
                        else -> recommendations.add("Blood pressure is within normal range.")
                    }
                }
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Continue maintaining your current health habits and regular monitoring.")
        }
        
        return recommendations
    }
    
    private fun createFile(fileName: String): File {
        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "HealthAssistant")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return File(downloadsDir, fileName)
    }
    
    fun getFileUri(filePath: String): android.net.Uri? {
        return try {
            val file = File(filePath)
            FileProvider.getUriForFile(
                context,
                "${context.applicationContext.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun exportHealthSummary(healthMetrics: List<HealthMetrics>): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "health_summary_${fileNameDateFormat.format(Date())}.txt"
                val file = createFile(fileName)
                
                file.writeText(generateHealthSummary(healthMetrics))
                
                ExportResult(
                    success = true,
                    filePath = file.absolutePath,
                    fileName = fileName,
                    mimeType = "text/plain",
                    message = "Health summary exported successfully"
                )
                
            } catch (e: Exception) {
                ExportResult(
                    success = false,
                    filePath = null,
                    fileName = null,
                    mimeType = null,
                    message = "Failed to export summary: ${e.message}"
                )
            }
        }
    }
    
    private fun generateHealthSummary(healthMetrics: List<HealthMetrics>): String {
        val summary = StringBuilder()
        
        summary.appendLine("HEALTH DATA SUMMARY")
        summary.appendLine("===================")
        summary.appendLine("Generated: ${dateFormat.format(Date())}")
        summary.appendLine()
        
        if (healthMetrics.isNotEmpty()) {
            val latest = healthMetrics.lastOrNull()
            val avgSteps = healthMetrics.map { it.steps }.average()
            val avgHeartRate = healthMetrics.mapNotNull { it.heartRate }.average()
            val avgSleepHours = healthMetrics.map { it.sleepHours }.average()
            val avgActiveCalories = healthMetrics.map { it.activeCalories }.average()
            
            summary.appendLine("RECENT HEALTH METRICS")
            summary.appendLine("---------------------")
            summary.appendLine("Latest Steps: ${latest?.steps}")
            summary.appendLine("Average Daily Steps: ${String.format("%.0f", avgSteps)}")
            summary.appendLine()
            summary.appendLine("Latest Heart Rate: ${latest?.heartRate ?: "N/A"} bpm")
            summary.appendLine("Average Heart Rate: ${String.format("%.0f", avgHeartRate)} bpm")
            summary.appendLine()
            summary.appendLine("Latest Sleep: ${String.format("%.1f", latest?.sleepHours ?: 0.0)} hours")
            summary.appendLine("Average Sleep: ${String.format("%.1f", avgSleepHours)} hours")
            summary.appendLine()
            summary.appendLine("Latest Active Calories: ${latest?.activeCalories}")
            summary.appendLine("Average Active Calories: ${String.format("%.0f", avgActiveCalories)}")
            summary.appendLine()
            
            if (latest?.systolicBP != null && latest?.diastolicBP != null) {
                summary.appendLine("Latest Blood Pressure: ${latest.systolicBP}/${latest.diastolicBP} mmHg")
                summary.appendLine()
            }
            
            if (latest?.oxygenSaturation != null) {
                summary.appendLine("Latest Oxygen Saturation: ${latest.oxygenSaturation}%")
                summary.appendLine()
            }
            
            if (latest?.weight != null) {
                summary.appendLine("Latest Weight: ${String.format("%.1f", latest.weight)} kg")
                summary.appendLine()
            }
            
            summary.appendLine("RECOMMENDATIONS")
            summary.appendLine("---------------")
            generateRecommendations(healthMetrics).forEach { recommendation ->
                summary.appendLine("• $recommendation")
            }
        } else {
            summary.appendLine("No health data available for analysis.")
        }
        
        return summary.toString()
    }
}

data class ExportResult(
    val success: Boolean,
    val filePath: String?,
    val fileName: String?,
    val mimeType: String?,
    val message: String
)
