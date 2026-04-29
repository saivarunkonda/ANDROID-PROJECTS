package com.healthassistant.health

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) {
    
    suspend fun getHealthMetrics(): HealthMetrics {
        return try {
            val steps = healthConnectManager.getTodaySteps()
            val heartRate = healthConnectManager.getRecentHeartRate()
            val sleepData = healthConnectManager.getSleepData()
            val activeCalories = healthConnectManager.getActiveCalories()
            val bloodPressure = healthConnectManager.getBloodPressure()
            val oxygenSaturation = healthConnectManager.getOxygenSaturation()
            val weight = healthConnectManager.getWeight()
            
            HealthMetrics(
                steps = steps,
                heartRate = heartRate,
                sleepHours = sleepData?.duration ?: 0.0,
                sleepQuality = sleepData?.quality ?: 0.0,
                activeCalories = activeCalories,
                systolicBP = bloodPressure?.systolic,
                diastolicBP = bloodPressure?.diastolic,
                oxygenSaturation = oxygenSaturation,
                weight = weight,
                lastSync = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            HealthMetrics(
                steps = 0.0,
                heartRate = null,
                sleepHours = 0.0,
                sleepQuality = 0.0,
                activeCalories = 0.0,
                systolicBP = null,
                diastolicBP = null,
                oxygenSaturation = null,
                weight = null,
                lastSync = System.currentTimeMillis()
            )
        }
    }
    
    suspend fun checkHealthConnectAvailability(): Boolean {
        return healthConnectManager.checkAvailability()
    }
    
    suspend fun getGrantedPermissions(): Set<String> {
        return healthConnectManager.requestPermissions().map { it.toString() }.toSet()
    }
    
    fun getHealthDataUpdates(): Flow<HealthMetrics> {
        return flow {
            while (true) {
                emit(getHealthMetrics())
                kotlinx.coroutines.delay(60000) // Update every minute
            }
        }
    }
    
    suspend fun syncHealthData(): SyncResult {
        return try {
            val metrics = getHealthMetrics()
            SyncResult(
                success = true,
                message = "Health data synced successfully",
                metrics = metrics
            )
        } catch (e: Exception) {
            SyncResult(
                success = false,
                message = "Failed to sync health data: ${e.message}",
                metrics = null
            )
        }
    }
}

data class HealthMetrics(
    val steps: Double,
    val heartRate: Double?,
    val sleepHours: Double,
    val sleepQuality: Double,
    val activeCalories: Double,
    val systolicBP: Double?,
    val diastolicBP: Double?,
    val oxygenSaturation: Double?,
    val weight: Double?,
    val lastSync: Long
)

data class SyncResult(
    val success: Boolean,
    val message: String,
    val metrics: HealthMetrics?
)
