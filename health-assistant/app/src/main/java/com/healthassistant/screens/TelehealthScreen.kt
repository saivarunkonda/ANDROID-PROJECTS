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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class TelehealthConsultation(
    val doctorName: String,
    val specialty: String,
    val date: String,
    val time: String,
    val status: ConsultationStatus,
    val type: ConsultationType
)

enum class ConsultationStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class ConsultationType {
    VIDEO, CHAT, PHONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelehealthScreen(navController: NavController) {
    val consultations = remember {
        listOf(
            TelehealthConsultation(
                doctorName = "Dr. Sarah Johnson",
                specialty = "Cardiology",
                date = "Today",
                time = "2:30 PM",
                status = ConsultationStatus.SCHEDULED,
                type = ConsultationType.VIDEO
            ),
            TelehealthConsultation(
                doctorName = "Dr. Michael Chen",
                specialty = "General Practice",
                date = "Tomorrow",
                time = "10:00 AM",
                status = ConsultationStatus.SCHEDULED,
                type = ConsultationType.VIDEO
            ),
            TelehealthConsultation(
                doctorName = "Dr. Emily Rodriguez",
                specialty = "Mental Health",
                date = "Dec 15",
                time = "3:00 PM",
                status = ConsultationStatus.SCHEDULED,
                type = ConsultationType.CHAT
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telehealth") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Start emergency call */ }) {
                        Icon(Icons.Default.Emergency, contentDescription = "Emergency")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Schedule new consultation */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Schedule Consultation")
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
                // Quick Actions
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
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Start video call */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = "Video Call")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Video Call")
                            }
                            
                            OutlinedButton(
                                onClick = { /* Start chat */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chat")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chat")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Upcoming Consultations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(consultations) { consultation ->
                ConsultationCard(consultation = consultation)
            }

            item {
                // Emergency Contacts
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Emergency Contacts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Emergency Services: 911",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "24/7 Health Hotline: 1-800-HEALTH",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultationCard(consultation: TelehealthConsultation) {
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
            // Doctor Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Doctor",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Consultation details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = consultation.doctorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = consultation.specialty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    Text(
                        text = "${consultation.date} at ${consultation.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConsultationTypeIcon(type = consultation.type)
                }
            }
            
            // Action button
            when (consultation.status) {
                ConsultationStatus.SCHEDULED -> {
                    Button(
                        onClick = { /* Start consultation */ },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Join")
                    }
                }
                ConsultationStatus.IN_PROGRESS -> {
                    Button(
                        onClick = { /* Rejoin consultation */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Rejoin")
                    }
                }
                else -> {
                    Text(
                        text = consultation.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun ConsultationTypeIcon(type: ConsultationType) {
    val icon = when (type) {
        ConsultationType.VIDEO -> Icons.Default.VideoCall
        ConsultationType.CHAT -> Icons.Default.Chat
        ConsultationType.PHONE -> Icons.Default.Call
    }
    val contentDescription = when (type) {
        ConsultationType.VIDEO -> "Video consultation"
        ConsultationType.CHAT -> "Chat consultation"
        ConsultationType.PHONE -> "Phone consultation"
    }
    
    Icon(
        icon,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp)
    )
}
