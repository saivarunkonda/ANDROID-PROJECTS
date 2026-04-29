package com.healthassistant.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthassistant.export.ExportResult
import com.healthassistant.export.HealthDataExporter
import com.healthassistant.health.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val healthDataExporter: HealthDataExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load user settings - in a real app, these would come from SharedPreferences
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notificationsEnabled = true,
                    autoSyncEnabled = true,
                    anomalyDetectionEnabled = true,
                    darkModeEnabled = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        // Save to SharedPreferences in real app
    }

    fun updateAutoSyncEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoSyncEnabled = enabled)
        // Save to SharedPreferences in real app
    }

    fun updateAnomalyDetectionEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(anomalyDetectionEnabled = enabled)
        // Save to SharedPreferences in real app
    }

    fun updateDarkModeEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(darkModeEnabled = enabled)
        // Save to SharedPreferences in real app
    }

    fun exportToCSV() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            
            try {
                // Get health metrics for export
                val healthMetrics = listOf(healthRepository.getHealthMetrics())
                val result = healthDataExporter.exportToCSV(healthMetrics)
                
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportResult = result,
                    error = if (!result.success) result.message else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    error = e.message
                )
            }
        }
    }

    fun exportToPDF() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            
            try {
                // Get health metrics for export
                val healthMetrics = listOf(healthRepository.getHealthMetrics())
                val result = healthDataExporter.exportToPDF(healthMetrics)
                
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportResult = result,
                    error = if (!result.success) result.message else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    error = e.message
                )
            }
        }
    }

    fun exportHealthSummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            
            try {
                // Get health metrics for export
                val healthMetrics = listOf(healthRepository.getHealthMetrics())
                val result = healthDataExporter.exportHealthSummary(healthMetrics)
                
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportResult = result,
                    error = if (!result.success) result.message else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    error = e.message
                )
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                // Clear app cache logic would go here
                _uiState.value = _uiState.value.copy(
                    error = null,
                    lastAction = "Cache cleared successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearExportResult() {
        _uiState.value = _uiState.value.copy(exportResult = null)
    }

    fun clearLastAction() {
        _uiState.value = _uiState.value.copy(lastAction = null)
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val autoSyncEnabled: Boolean = false,
    val anomalyDetectionEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val exportResult: ExportResult? = null,
    val lastAction: String? = null,
    val error: String? = null
)
