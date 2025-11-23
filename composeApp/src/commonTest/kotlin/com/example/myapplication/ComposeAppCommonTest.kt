package com.example.myapplication

import ActionSelectionScreen
import LoginScreen
import PropertyForm
import RentScreen
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun TestApp() {
    var currentScreen by remember { mutableStateOf("login") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Button(onClick = { currentScreen = "login" }) { Text("Login") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { currentScreen = "action" }) { Text("Action") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { currentScreen = "rent" }) { Text("Rent") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { currentScreen = "sell" }) { Text("Sell") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { currentScreen = "map" }) { Text("Map") }
        }
        Spacer(Modifier.height(24.dp))

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var carValue by remember { mutableStateOf("") }
        var availableStations by remember { mutableStateOf<List<Station>>(emptyList()) }
        var propertyDetails: Station? = null

        when (currentScreen) {
			"login" -> LoginScreen(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                message = message,
                onLogin = { message = "Login pressed for $username" },
                onRegister = { message = "Register pressed for $username" }
            )
            "action" -> ActionSelectionScreen { action ->
                message = "Selected action: $action"
            }
            "rent" -> RentScreen(
                carValue = carValue,
                onCarValueChange = { carValue = it },
                onCheckStations = {
                    availableStations = listOf(
                        Station(1, true, 48.137154, 11.576124, 101),
                        Station(2, false, 48.139126, 11.580186, 102)
                    )
                    message = "Checked stations for car: $carValue"
                }
            )
            "sell" -> PropertyForm(
                onSubmit = { details ->
                    propertyDetails = details
                    message = "Property submitted: $details"
                }
            )
            "map" -> MapScreen(
                onProfileClicked = { message = "Profile clicked" }
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(message)
        if (availableStations.isNotEmpty()) {
            Text("Available Stations:")
            availableStations.forEach {
                Text("Station ${it.id}: (${it.latitude}, ${it.longitude}) - Status: ${if (it.status) "Available" else "Unavailable"}")
            }
        }

        propertyDetails?.let {
            Text("Last submitted property: $it")
        }
    }
}

@Composable
@Preview
fun PreviewTestApp() {
    TestApp()
}
