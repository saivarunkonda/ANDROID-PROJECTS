package com.healthassistant.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthassistant.telehealth.CallState
import com.healthassistant.telehealth.Consultation
import com.healthassistant.telehealth.ConsultationStatus
import com.healthassistant.telehealth.ConsultationType
import com.healthassistant.telehealth.JitsiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TelehealthViewModel @Inject constructor(
    private val jitsiManager: JitsiManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TelehealthUiState())
    val uiState: StateFlow<TelehealthUiState> = _uiState.asStateFlow()

    init {
        loadUpcomingConsultations()
        observeCallState()
    }

    private fun loadUpcomingConsultations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Sample consultations - in real app, these would come from a repository
                val consultations = listOf(
                    Consultation(
                        id = "1",
                        doctorName = "Dr. Sarah Johnson",
                        specialty = "Cardiology",
                        scheduledTime = System.currentTimeMillis() + 3600000, // 1 hour from now
                        duration = 30,
                        type = ConsultationType.VIDEO,
                        status = ConsultationStatus.SCHEDULED,
                        roomName = "health-${System.currentTimeMillis()}"
                    ),
                    Consultation(
                        id = "2",
                        doctorName = "Dr. Michael Chen",
                        specialty = "General Practice",
                        scheduledTime = System.currentTimeMillis() + 86400000, // 24 hours from now
                        duration = 45,
                        type = ConsultationType.VIDEO,
                        status = ConsultationStatus.SCHEDULED,
                        roomName = "health-${System.currentTimeMillis() + 1}"
                    )
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    consultations = consultations
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun observeCallState() {
        // In a real implementation, you would collect the flow from JitsiManager
        // For now, we'll just use the current state
        _uiState.value = _uiState.value.copy(
            callState = jitsiManager.getCallState()
        )
    }

    fun startVideoCall(consultation: Consultation, userName: String) {
        viewModelScope.launch {
            try {
                // This would need to be called from an Activity context
                // For now, we'll just update the state
                _uiState.value = _uiState.value.copy(
                    error = "Video call requires Activity context. Please use the TelehealthManager directly."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun startAudioCall(consultation: Consultation, userName: String) {
        viewModelScope.launch {
            try {
                // This would need to be called from an Activity context
                _uiState.value = _uiState.value.copy(
                    error = "Audio call requires Activity context. Please use the TelehealthManager directly."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun endCall() {
        viewModelScope.launch {
            try {
                jitsiManager.endCall()
                _uiState.value = _uiState.value.copy(callState = CallState.Idle)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun scheduleConsultation(
        doctorName: String,
        specialty: String,
        scheduledTime: Long,
        duration: Int,
        type: ConsultationType
    ) {
        viewModelScope.launch {
            try {
                val newConsultation = Consultation(
                    id = (System.currentTimeMillis()).toString(),
                    doctorName = doctorName,
                    specialty = specialty,
                    scheduledTime = scheduledTime,
                    duration = duration,
                    type = type,
                    status = ConsultationStatus.SCHEDULED,
                    roomName = jitsiManager.generateRoomName()
                )
                
                val currentConsultations = _uiState.value.consultations.toMutableList()
                currentConsultations.add(newConsultation)
                
                _uiState.value = _uiState.value.copy(
                    consultations = currentConsultations,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TelehealthUiState(
    val isLoading: Boolean = false,
    val consultations: List<Consultation> = emptyList(),
    val callState: CallState = CallState.Idle,
    val error: String? = null
)
