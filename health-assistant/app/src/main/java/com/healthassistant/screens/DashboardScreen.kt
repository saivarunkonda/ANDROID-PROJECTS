package com.healthassistant.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthassistant.ui.theme.StatusGood
import com.healthassistant.ui.theme.StatusWarning
import com.healthassistant.ui.theme.StatusCritical

data class HealthMetric(
    val title: String,
    val value: String,
    val unit: String,
    val status: Color,
    val icon: ImageVector,
    val trend: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    var healthMetrics by remember {
        mutableStateOf(
            listOf(
                HealthMetric(
                    title = "Steps Today",
                    value = "8,432",
                    unit = "steps",
                    status = StatusGood,
                    icon = Icons.Default.DirectionsWalk,
                    trend = "+12%"
                ),
                HealthMetric(
                    title = "Heart Rate",
                    value = "72",
                    unit = "bpm",
                    status = StatusGood,
                    icon = Icons.Default.Favorite,
                    trend = "Normal"
                ),
                HealthMetric(
                    title = "Sleep",
                    value = "7.5",
                    unit = "hours",
                    status = StatusGood,
                    icon = Icons.Default.Hotel,
                    trend = "+0.5h"
                ),
                HealthMetric(
                    title = "Active Calories",
                    value = "320",
                    unit = "kcal",
                    status = StatusWarning,
                    icon = Icons.Default.LocalFireDepartment,
                    trend = "-8%"
                ),
                HealthMetric(
                    title = "Blood Oxygen",
                    value = "98",
                    unit = "%",
                    status = StatusGood,
                    icon = Icons.Default.Air,
                    trend = "Stable"
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Assistant") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Health Data") },
                    label = { Text("Health Data") },
                    selected = false,
                    onClick = { navController.navigate("health_data") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VideoCall, contentDescription = "Telehealth") },
                    label = { Text("Telehealth") },
                    selected = false,
                    onClick = { navController.navigate("telehealth") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Anomalies") },
                    label = { Text("Anomalies") },
                    selected = false,
                    onClick = { navController.navigate("anomaly_detection") }
                )
            }
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
                // Welcome Section
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
                            text = "Welcome back!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your health overview for today",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Health Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(healthMetrics) { metric ->
                HealthMetricCard(metric = metric)
            }

            item {
                // Quick Actions
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("health_data") }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = "View Details",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "View Details",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("telehealth") }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.VideoCall,
                                contentDescription = "Start Consultation",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Consultation",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthMetricCard(metric: HealthMetric) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with status color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    metric.icon,
                    contentDescription = metric.title,
                    tint = metric.status,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Metric details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = metric.unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            
            // Trend
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = metric.trend,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (metric.trend.startsWith("+")) StatusGood else StatusWarning
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .padding(top = 4.dp)
                        .background(
                            metric.status,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}
