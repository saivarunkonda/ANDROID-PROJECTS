package com.healthassistant.di

import android.content.Context
import androidx.work.WorkManager
import com.healthassistant.export.HealthDataExporter
import com.healthassistant.health.HealthConnectManager
import com.healthassistant.health.HealthRepository
import com.healthassistant.ml.AnomalyDetectionManager
import com.healthassistant.telehealth.JitsiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(healthConnectManager: HealthConnectManager): HealthRepository {
        return HealthRepository(healthConnectManager)
    }

    @Provides
    @Singleton
    fun provideAnomalyDetectionManager(@ApplicationContext context: Context): AnomalyDetectionManager {
        return AnomalyDetectionManager(context)
    }

    @Provides
    @Singleton
    fun provideJitsiManager(@ApplicationContext context: Context): JitsiManager {
        return JitsiManager(context)
    }

    @Provides
    @Singleton
    fun provideHealthDataExporter(@ApplicationContext context: Context): HealthDataExporter {
        return HealthDataExporter(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
