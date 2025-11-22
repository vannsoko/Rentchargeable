package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding


// 1. Mock Data Model
data class ChargerListing(
    val id: Int,
    val ownerName: String,
    val pricePerHour: Double,
    val address: String
)

// 2. Mock Data List
private val mockListings = listOf(
    ChargerListing(1, "John Doe", 7.50, "123 Maple St, Springfield"),
    ChargerListing(2, "Jane Smith", 8.00, "456 Oak Ave, Shelbyville"),
    ChargerListing(3, "Peter Jones", 6.75, "789 Pine Ln, Capital City"),
    ChargerListing(4, "Mary Williams", 9.25, "101 Elm Ct, Ogdenville"),
    ChargerListing(5, "David Brown", 7.00, "212 Birch Rd, North Haverbrook"),
    ChargerListing(6, "Sarah Miller", 8.50, "321 Cedar Blvd, Clintonville")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onProfileClicked: () -> Unit
) {
    // Correctly create the scaffold state
    val scaffoldState = rememberBottomSheetScaffoldState()

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
                    IconButton(onClick = onProfileClicked) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(listing.ownerName, style = MaterialTheme.typography.titleMedium)
            Text(listing.address, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            String.format("$%.2f/hr", listing.pricePerHour),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
