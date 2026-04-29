package com.healthassistant.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.healthassistant.ui.theme.StatusCritical
import com.healthassistant.ui.theme.StatusWarning
import com.healthassistant.ui.theme.StatusGood

data class AnomalyAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: AnomalySeverity,
    val metric: String,
    val detectedAt: String,
    val value: String,
    val normalRange: String
)

enum class AnomalySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnomalyDetectionScreen(navController: NavController) {
    val alerts = remember {
        listOf(
            AnomalyAlert(
                id = "1",
                title = "Elevated Heart Rate",
                description = "Heart rate detected above normal resting range",
                severity = AnomalySeverity.MEDIUM,
                metric = "Heart Rate",
                detectedAt = "10:30 AM",
                value = "95 bpm",
                normalRange = "60-80 bpm"
            ),
            AnomalyAlert(
                id = "2",
                title = "Irregular Sleep Pattern",
                description = "Sleep quality significantly lower than usual",
                severity = AnomalySeverity.LOW,
                metric = "Sleep Quality",
                detectedAt = "8:00 AM",
                value = "62%",
                normalRange = "75-95%"
            ),
            AnomalyAlert(
                id = "3",
                title = "High Blood Pressure Detected",
                description = "Systolic pressure above recommended range",
                severity = AnomalySeverity.HIGH,
                metric = "Blood Pressure",
                detectedAt = "Yesterday",
                value = "145/90 mmHg",
                normalRange = "120/80 mmHg"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anomaly Detection") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* View settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // ML Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ML Model Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Last scan: 2 min ago",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        LinearProgressIndicator(
                            progress = 0.85f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            item {
                // Statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusCritical
                            )
                            Text(
                                text = "Active Alerts",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "98.5%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusGood
                            )
                            Text(
                                text = "Accuracy",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "24",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hours Scanned",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Recent Anomalies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(alerts) { alert ->
                AnomalyAlertCard(alert = alert)
            }

            item {
                // Model Info
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "About Anomaly Detection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Our on-device ML model continuously analyzes your health data to detect unusual patterns. All processing happens locally on your device to ensure privacy.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { /* View model details */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Learn More")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnomalyAlertCard(alert: AnomalyAlert) {
    val severityColor = when (alert.severity) {
        AnomalySeverity.LOW -> StatusGood
        AnomalySeverity.MEDIUM -> StatusWarning
        AnomalySeverity.HIGH -> Color(0xFFFF6B35)
        AnomalySeverity.CRITICAL -> StatusCritical
    }

    val severityIcon = when (alert.severity) {
        AnomalySeverity.LOW -> Icons.Default.Info
        AnomalySeverity.MEDIUM -> Icons.Default.Warning
        AnomalySeverity.HIGH -> Icons.Default.Error
        AnomalySeverity.CRITICAL -> Icons.Default.CrisisAlert
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (alert.severity) {
                AnomalySeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Alert info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            severityIcon,
                            contentDescription = "Severity",
                            tint = severityColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = alert.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current: ${alert.value}",
                                style = MaterialTheme.typography.labelSmall,
                                color = severityColor,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Normal: ${alert.normalRange}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Text(
                            text = alert.detectedAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Dismiss alert */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
                Button(
                    onClick = { /* View details */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}
