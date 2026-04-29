package com.healthassistant.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthassistant.health.HealthMetrics
import com.healthassistant.health.HealthRepository
import com.healthassistant.ml.AnomalyDetectionManager
import com.healthassistant.ml.HealthDataInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val anomalyDetectionManager: AnomalyDetectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadHealthData()
    }

    fun loadHealthData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val healthMetrics = healthRepository.getHealthMetrics()
                val anomalyResult = anomalyDetectionManager.detectAnomalies(
                    HealthDataInput(
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
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    healthMetrics = healthMetrics,
                    anomalyResult = anomalyResult,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refreshData() {
        loadHealthData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val healthMetrics: HealthMetrics? = null,
    val anomalyResult: com.healthassistant.ml.AnomalyResult? = null,
    val error: String? = null
)
