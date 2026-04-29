package com.healthassistant.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthassistant.health.HealthMetrics
import com.healthassistant.health.HealthRepository
import com.healthassistant.ml.AnomalyDetectionManager
import com.healthassistant.ml.AnomalyResult
import com.healthassistant.ml.HealthAnomaly
import com.healthassistant.ml.HealthDataInput
import com.healthassistant.ml.ModelStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnomalyDetectionViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val anomalyDetectionManager: AnomalyDetectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnomalyDetectionUiState())
    val uiState: StateFlow<AnomalyDetectionUiState> = _uiState.asStateFlow()

    init {
        loadModelStatus()
        runAnomalyDetection()
    }

    private fun loadModelStatus() {
        viewModelScope.launch {
            try {
                val modelStatus = anomalyDetectionManager.getModelStatus()
                _uiState.value = _uiState.value.copy(modelStatus = modelStatus)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun runAnomalyDetection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            
            try {
                val healthMetrics = healthRepository.getHealthMetrics()
                val healthDataInput = HealthDataInput(
                    heartRate = healthMetrics.heartRate,
                    steps = healthMetrics.steps,
                    sleepHours = healthMetrics.sleepHours,
                    sleepQuality = healthMetrics.sleepQuality,
                    activeCalories = healthMetrics.activeCalories,
                    systolicBP = healthMetrics.systolicBP,
                    diastolicBP = healthMetrics.diastolicBP,
                    oxygenSaturation = healthMetrics.oxygenSaturation,
                    weight = healthMetrics.weight
                )
                
                val result = anomalyDetectionManager.detectAnomalies(healthDataInput)
                
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    currentResult = result,
                    lastAnalysisTime = System.currentTimeMillis(),
                    error = null
                )
                
                // Add to history
                addToHistory(result)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    error = e.message
                )
            }
        }
    }

    private fun addToHistory(result: AnomalyResult) {
        val history = _uiState.value.anomalyHistory.toMutableList()
        history.add(AnomalyHistoryItem(
            timestamp = System.currentTimeMillis(),
            result = result
        ))
        
        // Keep only last 50 items
        if (history.size > 50) {
            history.removeAt(0)
        }
        
        _uiState.value = _uiState.value.copy(anomalyHistory = history)
    }

    fun runNewAnalysis() {
        runAnomalyDetection()
    }

    fun dismissAnomaly(anomaly: HealthAnomaly) {
        val currentAlerts = _uiState.value.activeAlerts.toMutableList()
        currentAlerts.removeAll { it.id == anomaly.id }
        _uiState.value = _uiState.value.copy(activeAlerts = currentAlerts)
    }

    fun dismissAllAlerts() {
        _uiState.value = _uiState.value.copy(activeAlerts = emptyList())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateActiveAlerts(alerts: List<HealthAnomaly>) {
        _uiState.value = _uiState.value.copy(activeAlerts = alerts)
    }
}

data class AnomalyDetectionUiState(
    val isAnalyzing: Boolean = false,
    val modelStatus: ModelStatus? = null,
    val currentResult: AnomalyResult? = null,
    val activeAlerts: List<HealthAnomaly> = emptyList(),
    val anomalyHistory: List<AnomalyHistoryItem> = emptyList(),
    val lastAnalysisTime: Long? = null,
    val error: String? = null
)

data class AnomalyHistoryItem(
    val timestamp: Long,
    val result: AnomalyResult
)
