package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onProfileClicked: () -> Unit
) {
    // Mock listings as a list of lat/lng positions
    val mockListings = listOf(
        Pair(48.137154, 11.575382),    // Marienplatz, Munich
        Pair(48.1643, 11.6053),        // Englischer Garten
        Pair(48.1306, 11.5855)         // Deutsches Museum
    )

    var selectedLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showProfile by remember { mutableStateOf(false) }

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
                    items(mockListings) { listing ->
                        // (Optionally show as "Lat: X, Lng: Y" or fake address here.)
                        Text(
                            text = "Lat: ${listing.first}, Lng: ${listing.second}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
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
        content = { paddingValues ->
            // MAP IS DISPLAYED HERE!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                // Supply mock markers and a selected marker on tap
                MapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    locations = mockListings,
                    selectedLocation = selectedLocation,
                    onMarkerClick = { /* option: show details */ },
                    onMapClick = { lat, lng ->
                        selectedLocation = Pair(lat, lng)
                    }
                )
            }
        }
    )
}