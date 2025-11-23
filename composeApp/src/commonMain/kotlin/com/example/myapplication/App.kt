import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import com.example.myapplication.*
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*

@Composable
fun App() {
    AppTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var currentPage by remember { mutableStateOf("login") }
        val apiService = remember { ApiService() }
        val scope = rememberCoroutineScope()
        var responseBody by remember { mutableStateOf<User?>(null) }

        when (currentPage) {
            "login" -> LoginScreen(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                message = message,
                onLogin = {
                    scope.launch {
                        val response = apiService.login(UserCredentials(username = username, password = password))
                        print(response.status)
                        if (response.status == HttpStatusCode.OK) {
                            currentPage = "map"
                            message = "Login successful!"
                        } else {
                            message = "Login failed: Invalid credentials"
                        }
                    }
                },
                onRegister = {
                    scope.launch {
                        val response = apiService.register(UserCredentials(username = username, password = password))
                        if (response.status == HttpStatusCode.Created) {
                            message = "Registration successful!"
                            currentPage = "action"
                        } else {
                            message = "Registration failed: Username may already exist."
                        }
                    }
                }
            )
            "action" -> ActionSelectionScreen { action ->
                when (action) {
                    "rent" -> currentPage = "rent"
                    "sell" -> currentPage = "sell"
                    "skip" -> {
                        currentPage = "map"
                    }
                }
            }
            "rent" -> RentScreen(
                username = username,
                apiService = apiService,
                token = "" /* token, now just pass "" or remove if you want */,
                onCarAdded = { currentPage = "map" }
            )
            "sell" -> PropertyForm(
                onSubmit = { propertyDetails ->
                    scope.launch {
                        apiService.createStation(propertyDetails)
                        currentPage = "map"
                    }
                }
            )
            "map" -> MapScreen(
                onProfileClicked = {
                    username = ""
                    password = ""
                    message = ""
                    currentPage = "login"
                }
            )
        }
    }
}

// Add these composables below App()

@Composable
fun ActionSelectionScreen(onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("What would you like to do?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onSelect("rent") }) { Text("Rent") }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onSelect("sell") }) { Text("Sell") }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onSelect("skip") }) { Text("Skip") }
    }
}

@Composable
fun RentScreen(
    username: String,
    apiService: ApiService,
    token: String,
    onCarAdded: () -> Unit // Call this to navigate after a car is added (optional)
) {
    var carValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Enter your car:", // maybe add a smart feature soon
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = carValue,
            onValueChange = { carValue = it },
            label = { Text("Car") }
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                isLoading = true
                message = null
                scope.launch {
                    try {
                        val response = apiService.addCarToUser(carValue, username, token)
                        if (response.status.value in 200..299) {
                            message = "Car added!"
                            onCarAdded()
                        } else {
                            message = "Error: ${response.status.description}"
                        }
                    } catch (e: Exception) {
                        message = "Failed: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = carValue.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Add Car")
            }
        }
        if (message != null) {
            Spacer(Modifier.height(16.dp))
            Text(message ?: "")
        }
    }
}

// PropertyForm remains as in your previous code
@Composable
fun PropertyForm(onSubmit: (Station) -> Unit) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Add Station", fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 340.dp) // wider dialog
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Click on the map to pick a location:", fontSize = 13.sp)
                MapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp), // much smaller map
                    locations = listOf(),
                    selectedLocation = if(latitude.isNotBlank() && longitude.isNotBlank()) {
                        Pair(latitude.toDouble(), longitude.toDouble())
                    } else null,
                    onMarkerClick = { /* ... */ },
                    onMapClick = { lat, lng ->
                        latitude = lat.toString()
                        longitude = lng.toString()
                    }
                )
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude", fontSize = 12.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude", fontSize = 12.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price", fontSize = 12.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Checkbox(checked = status, onCheckedChange = { status = it })
                    Text("Available", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(0.6f),
                onClick = {
                    onSubmit(
                        Station(
                            id = 0,
                            status = status,
                            latitude = latitude,
                            longitude = longitude,
                            userId = 0
                        )
                    )
                }
            ) {
                Text("Submit", fontSize = 15.sp)
            }
        }
    )
}

@Composable
fun LoginScreen(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    message: String,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("What would you like to do today?", style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = onUsernameChange, label = { Text("Username") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = onPasswordChange, label = { Text("Password") })
        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = onLogin) { Text("Login") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onRegister) { Text("Register") }
        }
        Spacer(Modifier.height(16.dp))
        Text(message)
    }
}

 @Composable
 fun LoggedInScreen(username: String, onLogout: () -> Unit) {
     Column(
         modifier = Modifier.fillMaxSize().padding(16.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
     ) {
         Text("Welcome, $username!", style = MaterialTheme.typography.headlineMedium)
         Spacer(Modifier.height(16.dp))
         Button(onClick = onLogout) {
             Text("Logout")
         }
     }
 }
