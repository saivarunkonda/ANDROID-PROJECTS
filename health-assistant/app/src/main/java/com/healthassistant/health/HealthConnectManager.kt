package com.healthassistant.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Health permissions required
    val HEALTH_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(BloodGlucoseRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class)
    )

    suspend fun checkAvailability(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    suspend fun requestPermissions(): Set<String> {
        return try {
            healthConnectClient.permissionController.getGrantedPermissions()
        } catch (e: Exception) {
            emptySet()
        }
    }

    suspend fun getTodaySteps(): Double {
        return try {
            val now = Instant.now()
            val startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            
            val request = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
            
            val result = healthConnectClient.aggregate(request)
            result[StepsRecord.COUNT_TOTAL]?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    suspend fun getRecentHeartRate(): Double? {
        return try {
            val now = Instant.now()
            val twentyFourHoursAgo = now.minusSeconds(24 * 60 * 60)
            
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(twentyFourHoursAgo, now),
                ascendingOrder = false
            )
            
            val response = healthConnectClient.readRecords(request)
            response.records.firstOrNull()?.samples?.lastOrNull()?.beatsPerMinute?.toDouble()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSleepData(): SleepData? {
        return try {
            val now = Instant.now()
            val lastNight = now.minusSeconds(24 * 60 * 60)
            
            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(lastNight, now),
                ascendingOrder = false
            )
            
            val response = healthConnectClient.readRecords(request)
            val sleepRecord = response.records.firstOrNull()
            
            sleepRecord?.let {
                val duration = it.endTime.epochSecond - it.startTime.epochSecond
                SleepData(
                    duration = duration / 3600.0, // Convert to hours
                    quality = calculateSleepQuality(it)
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getActiveCalories(): Double {
        return try {
            val now = Instant.now()
            val startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            
            val request = AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
            
            val result = healthConnectClient.aggregate(request)
            result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    suspend fun getBloodPressure(): BloodPressureData? {
        return try {
            val now = Instant.now()
            val lastWeek = now.minusSeconds(7 * 24 * 60 * 60)
            
            val request = ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = TimeRangeFilter.between(lastWeek, now),
                ascendingOrder = false
            )
            
            val response = healthConnectClient.readRecords(request)
            val record = response.records.firstOrNull()
            
            record?.let {
                BloodPressureData(
                    systolic = it.systolic.inMillimetersOfMercury,
                    diastolic = it.diastolic.inMillimetersOfMercury,
                    timestamp = it.time
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getOxygenSaturation(): Double? {
        return try {
            val now = Instant.now()
            val lastHour = now.minusSeconds(60 * 60)
            
            val request = ReadRecordsRequest(
                recordType = OxygenSaturationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(lastHour, now),
                ascendingOrder = false
            )
            
            val response = healthConnectClient.readRecords(request)
            response.records.firstOrNull()?.percentage?.value
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWeight(): Double? {
        return try {
            val now = Instant.now()
            val lastMonth = now.minusSeconds(30L * 24 * 60 * 60)
            
            val request = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(lastMonth, now),
                ascendingOrder = false
            )
            
            val response = healthConnectClient.readRecords(request)
            response.records.firstOrNull()?.weight?.inKilograms
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateSleepQuality(sleepRecord: SleepSessionRecord): Double {
        // Simple sleep quality calculation based on duration and stages
        val durationHours = (sleepRecord.endTime.epochSecond - sleepRecord.startTime.epochSecond) / 3600.0
        
        return when {
            durationHours >= 7 && durationHours <= 9 -> 0.85 // Good
            durationHours >= 6 && durationHours <= 10 -> 0.70 // Fair
            durationHours >= 5 && durationHours <= 11 -> 0.50 // Poor
            else -> 0.25 // Very poor
        }
    }

    fun getHealthDataUpdates(): Flow<HealthDataUpdate> {
        // This would typically use Health Connect's change messages
        // For now, return an empty flow
        return flowOf()
    }
}

data class HealthDataUpdate(
    val recordType: String,
    val timestamp: Instant
)

data class SleepData(
    val duration: Double, // in hours
    val quality: Double // 0.0 to 1.0
)

data class BloodPressureData(
    val systolic: Double,
    val diastolic: Double,
    val timestamp: Instant
)
