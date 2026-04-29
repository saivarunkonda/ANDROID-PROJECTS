package com.healthassistant.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthassistant.health.HealthRepository
import com.healthassistant.health.SyncResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthDataViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthDataUiState())
    val uiState: StateFlow<HealthDataUiState> = _uiState.asStateFlow()

    init {
        checkHealthConnectAvailability()
        loadPermissions()
    }

    private fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val isAvailable = healthRepository.checkHealthConnectAvailability()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isHealthConnectAvailable = isAvailable,
                    error = if (!isAvailable) "Health Connect is not available on this device" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            try {
                val permissions = healthRepository.getGrantedPermissions()
                _uiState.value = _uiState.value.copy(grantedPermissions = permissions)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun syncHealthData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            
            try {
                val result = healthRepository.syncHealthData()
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncResult = result,
                    error = if (!result.success) result.message else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSyncResult() {
        _uiState.value = _uiState.value.copy(syncResult = null)
    }
}

data class HealthDataUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isHealthConnectAvailable: Boolean = false,
    val grantedPermissions: Set<String> = emptySet(),
    val syncResult: SyncResult? = null,
    val error: String? = null
)
