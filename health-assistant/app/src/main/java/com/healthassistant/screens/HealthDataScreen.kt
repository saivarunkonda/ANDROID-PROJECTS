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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class HealthDataCategory(
    val name: String,
    val icon: ImageVector,
    val description: String,
    val lastSync: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(navController: NavController) {
    val categories = remember {
        listOf(
            HealthDataCategory(
                name = "Physical Activity",
                icon = Icons.Default.DirectionsWalk,
                description = "Steps, distance, calories burned",
                lastSync = "2 minutes ago"
            ),
            HealthDataCategory(
                name = "Heart Health",
                icon = Icons.Default.Favorite,
                description = "Heart rate, blood pressure, ECG",
                lastSync = "5 minutes ago"
            ),
            HealthDataCategory(
                name = "Sleep",
                icon = Icons.Default.Hotel,
                description = "Sleep duration, quality, stages",
                lastSync = "1 hour ago"
            ),
            HealthDataCategory(
                name = "Nutrition",
                icon = Icons.Default.Restaurant,
                description = "Calories, nutrients, hydration",
                lastSync = "30 minutes ago"
            ),
            HealthDataCategory(
                name = "Mental Health",
                icon = Icons.Default.Psychology,
                description = "Stress levels, mood tracking",
                lastSync = "15 minutes ago"
            ),
            HealthDataCategory(
                name = "Vitals",
                icon = Icons.Default.MonitorHeart,
                description = "Temperature, oxygen, weight",
                lastSync = "10 minutes ago"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                // Sync Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Health Connect Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Connected and syncing",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Button(
                            onClick = { /* Sync now */ }
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = "Sync")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Now")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Data Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(categories) { category ->
                HealthDataCategoryCard(category = category)
            }

            item {
                // Export Options
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Export Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Export CSV */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.TableChart, contentDescription = "CSV")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CSV")
                            }
                            
                            OutlinedButton(
                                onClick = { /* Export PDF */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PDF")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataCategoryCard(category: HealthDataCategory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to category details */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Category details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Last sync: ${category.lastSync}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Arrow
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
