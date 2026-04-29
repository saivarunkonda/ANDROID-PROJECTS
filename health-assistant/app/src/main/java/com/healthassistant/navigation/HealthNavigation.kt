package com.healthassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.healthassistant.screens.DashboardScreen
import com.healthassistant.screens.HealthDataScreen
import com.healthassistant.screens.TelehealthScreen
import com.healthassistant.screens.SettingsScreen
import com.healthassistant.screens.AnomalyDetectionScreen

@Composable
fun HealthNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        composable("health_data") {
            HealthDataScreen(navController = navController)
        }
        composable("telehealth") {
            TelehealthScreen(navController = navController)
        }
        composable("anomaly_detection") {
            AnomalyDetectionScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}
