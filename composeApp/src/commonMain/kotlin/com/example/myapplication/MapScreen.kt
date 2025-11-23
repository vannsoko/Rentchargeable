package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfilePage(
    username: String,
    carList: List<String>,
    onClose: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Picture (use vector or emoji or real profile pic URI)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                username,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(20.dp))
            Text("My Cars:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (carList.isEmpty()) {
                Text("No cars registered.", style = MaterialTheme.typography.bodyMedium)
            } else {
                carList.forEach { car ->
                    Text(car, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onProfileClicked: () -> Unit
) {
    val mockStations = listOf(
        Station(1, true, "48.137154", "11.575382", 10),
        Station(2, false, "48.1643", "11.6053", 12),
        Station(3, true, "48.1306", "11.5855", 13),
        Station(4, true, "48.1457", "11.5736", 14),
        Station(5, false, "48.1486", "11.5625", 15),
        Station(6, true, "48.1472", "11.5580", 16),
        Station(7, true, "48.1321", "11.6304", 17),
        Station(8, true, "48.1663", "11.5473", 18),
        Station(9, false, "48.1247", "11.5445", 19),
        Station(10, true, "48.1281", "11.5797", 20),
        Station(11, true, "48.1377", "11.5975", 21),
        Station(12, false, "48.1402", "11.5520", 22),
        Station(13, true, "48.1511", "11.6321", 23),
        Station(14, true, "48.1305", "11.5209", 24),
        Station(15, false, "48.1442", "11.5601", 25)
    )

    var selectedLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showProfile by remember { mutableStateOf(false) }

    // For this demo: mock (replace with your actual values!)
    val userName = "test_user"
    val cars = listOf("Toyota Prius", "Volkswagen Golf", "Audi e-tron")

    if (showProfile) {
        ProfilePage(
            username = userName,
            carList = cars,
            onClose = { showProfile = false }
        )
    } else {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        "Available Chargers Nearby",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    LazyColumn {
                        items(mockStations) { station ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    "Station #${station.id}  (Owner: User ${station.userId})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Lat: ${station.latitude}, Lng: ${station.longitude}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    if (station.status) "Available" else "Unavailable",
                                    color = if (station.status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            sheetPeekHeight = 128.dp,
            topBar = {
                TopAppBar(
                    title = { Text("Rent-a-Charger") },
                    actions = {
                        IconButton(onClick = { showProfile = true }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        }
                    }
                )
            },
            // ...
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    MapView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        locations = mockStations.map { Pair(it.latitude.toDouble(), it.longitude.toDouble()) },
                        selectedLocation = selectedLocation,
                        onMarkerClick = { /* ... */ },
                        onMapClick = { lat, lng ->
                            selectedLocation = Pair(lat, lng)
                        }
                    )
                }
            }
        )
    }
}