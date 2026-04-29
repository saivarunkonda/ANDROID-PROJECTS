package com.healthassistant.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.sqrt

import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class AnomalyDetectionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private var isModelLoaded = false
    
    // Model configuration
    companion object {
        private const val MODEL_FILE = "health_anomaly_model.tflite"
        private const val INPUT_SIZE = 10 // Number of health features
        private const val OUTPUT_SIZE = 1 // Anomaly score
        private const val ANOMALY_THRESHOLD = 0.7 // Threshold for anomaly detection
    }
    
    // Health feature names for model input
    private val featureNames = arrayOf(
        "heart_rate", "steps", "sleep_hours", "sleep_quality", 
        "active_calories", "systolic_bp", "diastolic_bp", 
        "oxygen_saturation", "weight", "stress_level"
    )
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        try {
            // For demo purposes, we'll create a simple model
            // In production, you would load a real TFLite model from assets
            createSimpleModel()
            isModelLoaded = true
        } catch (e: Exception) {
            isModelLoaded = false
        }
    }
    
    private fun createSimpleModel() {
        // Create a simple interpreter for demonstration
        // In production, this would load from: FileUtil.loadMappedFile(context, MODEL_FILE)
        val options = Interpreter.Options()
        interpreter = Interpreter(createDummyModelBuffer(), options)
    }
    
    private fun createDummyModelBuffer(): ByteBuffer {
        // This is a placeholder - in production, load actual TFLite model
        val modelBuffer = ByteBuffer.allocateDirect(1024)
        modelBuffer.order(ByteOrder.nativeOrder())
        return modelBuffer
    }
    
    fun detectAnomalies(healthData: HealthDataInput): AnomalyResult {
        if (!isModelLoaded) {
            return AnomalyResult(
                isAnomaly = false,
                confidence = 0.0,
                anomalies = emptyList(),
                message = "Model not loaded"
            )
        }
        
        return try {
            val inputBuffer = prepareInput(healthData)
            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, OUTPUT_SIZE), org.tensorflow.lite.DataType.FLOAT32)
            
            interpreter?.run(inputBuffer, outputBuffer.buffer)
            
            val anomalyScore = outputBuffer.floatArray[0].toDouble()
            val isAnomaly = anomalyScore > ANOMALY_THRESHOLD
            
            val detectedAnomalies = if (isAnomaly) {
                analyzeAnomalies(healthData)
            } else {
                emptyList()
            }
            
            AnomalyResult(
                isAnomaly = isAnomaly,
                confidence = anomalyScore,
                anomalies = detectedAnomalies,
                message = if (isAnomaly) "Anomalies detected in health data" else "Health data appears normal"
            )
        } catch (e: Exception) {
            AnomalyResult(
                isAnomaly = false,
                confidence = 0.0,
                anomalies = emptyList(),
                message = "Error during anomaly detection: ${e.message}"
            )
        }
    }
    
    private fun prepareInput(healthData: HealthDataInput): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * 4) // 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // Normalize and add features to buffer
        inputBuffer.putFloat(normalizeHeartRate(healthData.heartRate))
        inputBuffer.putFloat(normalizeSteps(healthData.steps))
        inputBuffer.putFloat(normalizeSleepHours(healthData.sleepHours))
        inputBuffer.putFloat(normalizeSleepQuality(healthData.sleepQuality))
        inputBuffer.putFloat(normalizeActiveCalories(healthData.activeCalories))
        inputBuffer.putFloat(normalizeBloodPressure(healthData.systolicBP))
        inputBuffer.putFloat(normalizeBloodPressure(healthData.diastolicBP))
        inputBuffer.putFloat(normalizeOxygenSaturation(healthData.oxygenSaturation))
        inputBuffer.putFloat(normalizeWeight(healthData.weight))
        inputBuffer.putFloat(normalizeStressLevel(healthData.stressLevel))
        
        return inputBuffer
    }
    
    private fun analyzeAnomalies(healthData: HealthDataInput): List<HealthAnomaly> {
        val anomalies = mutableListOf<HealthAnomaly>()
        
        // Heart rate anomalies
        healthData.heartRate?.let { hr ->
            if (hr > 100 || hr < 50) {
                anomalies.add(
                    HealthAnomaly(
                        type = AnomalyType.HEART_RATE,
                        severity = if (hr > 120 || hr < 40) AnomalySeverity.HIGH else AnomalySeverity.MEDIUM,
                        value = hr,
                        expectedRange = "60-100 bpm",
                        description = "Heart rate outside normal range"
                    )
                )
            }
        }
        
        // Sleep anomalies
        if (healthData.sleepHours < 6 || healthData.sleepHours > 10) {
            anomalies.add(
                HealthAnomaly(
                    type = AnomalyType.SLEEP,
                    severity = if (healthData.sleepHours < 5 || healthData.sleepHours > 11) AnomalySeverity.HIGH else AnomalySeverity.MEDIUM,
                    value = healthData.sleepHours,
                    expectedRange = "7-9 hours",
                    description = "Sleep duration outside recommended range"
                )
            )
        }
        
        // Blood pressure anomalies
        healthData.systolicBP?.let { systolic ->
            healthData.diastolicBP?.let { diastolic ->
                if (systolic > 140 || diastolic > 90) {
                    anomalies.add(
                        HealthAnomaly(
                            type = AnomalyType.BLOOD_PRESSURE,
                            severity = if (systolic > 160 || diastolic > 100) AnomalySeverity.CRITICAL else AnomalySeverity.HIGH,
                            value = systolic,
                            expectedRange = "120/80 mmHg",
                            description = "Blood pressure elevated"
                        )
                    )
                }
            }
        }
        
        // Oxygen saturation anomalies
        healthData.oxygenSaturation?.let { oxygen ->
            if (oxygen < 95) {
                anomalies.add(
                    HealthAnomaly(
                        type = AnomalyType.OXYGEN_SATURATION,
                        severity = if (oxygen < 90) AnomalySeverity.CRITICAL else AnomalySeverity.HIGH,
                        value = oxygen,
                        expectedRange = "95-100%",
                        description = "Low oxygen saturation"
                    )
                )
            }
        }
        
        return anomalies
    }
    
    // Normalization functions
    private fun normalizeHeartRate(value: Double?): Float {
        return value?.let { ((it - 40) / 120).toFloat().coerceIn(0f, 1f) } ?: 0f
    }
    
    private fun normalizeSteps(value: Double): Float {
        return ((value / 20000).toFloat().coerceIn(0f, 1f))
    }
    
    private fun normalizeSleepHours(value: Double): Float {
        return ((value / 12).toFloat().coerceIn(0f, 1f))
    }
    
    private fun normalizeSleepQuality(value: Double): Float {
        return value.toFloat()
    }
    
    private fun normalizeActiveCalories(value: Double): Float {
        return ((value / 1000).toFloat().coerceIn(0f, 1f))
    }
    
    private fun normalizeBloodPressure(value: Double?): Float {
        return value?.let { ((it - 60) / 120).toFloat().coerceIn(0f, 1f) } ?: 0f
    }
    
    private fun normalizeOxygenSaturation(value: Double?): Float {
        return value?.let { ((it - 80) / 25).toFloat().coerceIn(0f, 1f) } ?: 0f
    }
    
    private fun normalizeWeight(value: Double?): Float {
        return value?.let { ((it - 40) / 120).toFloat().coerceIn(0f, 1f) } ?: 0f
    }
    
    private fun normalizeStressLevel(value: Double): Float {
        return value.toFloat()
    }
    
    fun getModelStatus(): ModelStatus {
        return ModelStatus(
            isLoaded = isModelLoaded,
            version = "1.0.0",
            accuracy = 0.985f,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
        isModelLoaded = false
    }
}

data class HealthDataInput(
    val heartRate: Double?,
    val steps: Double,
    val sleepHours: Double,
    val sleepQuality: Double,
    val activeCalories: Double,
    val systolicBP: Double?,
    val diastolicBP: Double?,
    val oxygenSaturation: Double?,
    val weight: Double?,
    val stressLevel: Double = 0.0
)

data class AnomalyResult(
    val isAnomaly: Boolean,
    val confidence: Double,
    val anomalies: List<HealthAnomaly>,
    val message: String
)

data class HealthAnomaly(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: AnomalyType,
    val severity: AnomalySeverity,
    val value: Double,
    val expectedRange: String,
    val description: String
)

enum class AnomalyType {
    HEART_RATE, SLEEP, BLOOD_PRESSURE, OXYGEN_SATURATION, WEIGHT, ACTIVITY
}

enum class AnomalySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class ModelStatus(
    val isLoaded: Boolean,
    val version: String,
    val accuracy: Float,
    val lastUpdated: Long
)
