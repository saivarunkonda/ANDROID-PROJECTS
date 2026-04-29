package com.healthassistant.telehealth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import java.net.URL
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo

import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class JitsiManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: Flow<CallState> = _callState.asStateFlow()
    
    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: Flow<List<Participant>> = _participants.asStateFlow()
    
    init {
        initializeJitsi()
    }
    
    private fun initializeJitsi() {
        try {
            val serverURL = URL("https://meet.jit.si")
            val defaultOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .build()
            
            JitsiMeet.setDefaultConferenceOptions(defaultOptions)
        } catch (e: Exception) {
            // Handle initialization error
        }
    }
    
    fun startVideoCall(
        activity: ComponentActivity,
        roomName: String,
        userName: String,
        doctorName: String = "Doctor"
    ) {
        try {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomName)
                .setSubject("Health Consultation with $doctorName")
                .setUserInfo(JitsiMeetUserInfo().apply { 
                    displayName = userName
                })
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .build()
            
            JitsiMeetActivity.launch(activity as Context, options)
            _callState.value = CallState.Connecting
            
        } catch (e: Exception) {
            _callState.value = CallState.Error("Failed to start video call: ${e.message}")
        }
    }
    
    fun startAudioCall(
        activity: ComponentActivity,
        roomName: String,
        userName: String,
        doctorName: String = "Doctor"
    ) {
        try {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomName)
                .setSubject("Audio Consultation with $doctorName")
                .setUserInfo(JitsiMeetUserInfo().apply { 
                    displayName = userName
                })
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setAudioOnly(true)
                .build()
            
            JitsiMeetActivity.launch(activity as Context, options)
            _callState.value = CallState.Connecting
            
        } catch (e: Exception) {
            _callState.value = CallState.Error("Failed to start audio call: ${e.message}")
        }
    }
    
    fun endCall() {
        _callState.value = CallState.Idle
        _participants.value = emptyList()
    }
    
    fun generateRoomName(): String {
        return "health-${System.currentTimeMillis()}"
    }
    
    fun getCallState(): CallState {
        return _callState.value
    }
    
    fun updateCallState(state: CallState) {
        _callState.value = state
    }
    
    fun updateParticipants(participantList: List<Participant>) {
        _participants.value = participantList
    }
}

// Call state enum
sealed class CallState {
    object Idle : CallState()
    object Connecting : CallState()
    object Connected : CallState()
    object Ended : CallState()
    data class Error(val message: String) : CallState()
}

// Participant data class
data class Participant(
    val id: String,
    val name: String,
    val isLocal: Boolean,
    val isAudioMuted: Boolean,
    val isVideoMuted: Boolean
)

// Consultation data class
data class Consultation(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val scheduledTime: Long,
    val duration: Int, // in minutes
    val type: ConsultationType,
    val status: ConsultationStatus,
    val roomName: String
)

enum class ConsultationType {
    VIDEO, AUDIO, CHAT
}

enum class ConsultationStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}
