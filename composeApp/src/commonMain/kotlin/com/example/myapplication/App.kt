package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.ktor.http.*
import kotlinx.coroutines.launch

@Composable
fun App() {
    AppTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var isLoggedIn by remember { mutableStateOf(false) }

        val apiService = remember { ApiService() }
        val scope = rememberCoroutineScope()

        isLoggedIn = true
        if (isLoggedIn) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Welcome, du da!", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                MapViewTest(modifier = Modifier.fillMaxSize())
            }

        } else {
            LoginScreen(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                message = message,
                onLogin = {
                    scope.launch {
                        val response = apiService.login(UserCredentials(username = username, password = password))
                        if (response.status == HttpStatusCode.OK) {
                            isLoggedIn = true
                            message = "Login successful!"
                        } else {
                            message = "Login failed: Invalid credentials"
                        }
                    }
                },
                onRegister = {
                    scope.launch {
                        val response = apiService.register(UserCredentials(username = username, password = password))
                        message = if (response.status == HttpStatusCode.Created) {
                            "Registration successful! Please log in."
                        } else {
                            "Registration failed: Username may already exist."
                        }
                    }
                }
            )
        }
    }
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
