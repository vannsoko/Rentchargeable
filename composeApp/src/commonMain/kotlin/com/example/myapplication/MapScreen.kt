package com.example.myapplication

import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import com.example.myapplication.ApiService


// 1. Mock Data Model
data class ChargerListing(
    val id: Int,
    var status: Boolean,
    val latitude: Double,
    val longitude: Double,
    val userId: Int,
    val ownerName: String,
    val address: String,
    val pricePerHour: Double
)

// 2. Mock Data List
private val mockListings = listOf(
    ChargerListing(1, true, 48.137154, 11.576124, 101, "Anna Müller", "Leopoldstraße 12, München", 7.50),
    ChargerListing(2, false, 48.139126, 11.580186, 102, "Maximilian Bauer", "Sendlinger-Tor-Platz 5, München", 8.00),
    ChargerListing(3, true, 48.144599, 11.565679, 103, "Sophie Schneider", "Theresienstraße 45, München", 6.75),
    ChargerListing(4, true, 48.132010, 11.584080, 104, "Lukas Fischer", "Isartorplatz 3, München", 9.25),
    ChargerListing(5, false, 48.139215, 11.597222, 105, "Laura Weber", "Prinzregentenstraße 22, München", 7.00),
    ChargerListing(6, true, 48.155000, 11.582000, 106, "Jonas Wagner", "Schwabinger Straße 8, München", 8.50)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onProfileClicked: () -> Unit
) {
    // Correctly create the scaffold state
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
                        ChargerListingItem(listing)
                        HorizontalDivider()
                    }
                }
            }
        },
        sheetPeekHeight = 128.dp, // How much of the sheet is visible when collapsed
        topBar = {
            // Top bar with title and profile button
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Map will be displayed here", style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
fun ChargerListingItem(listing: ChargerListing) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Availability circle
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = if (listing.status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    shape = CircleShape
                )
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(listing.ownerName, style = MaterialTheme.typography.titleMedium)
            Text(listing.address, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            String.format("€%.2f/hr", listing.pricePerHour),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfilePanel(
    username: String,
    profilePicture: Painter,
    onClose: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = profilePicture,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(username, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onClose) { Text("Close") }
        }
    }
}
