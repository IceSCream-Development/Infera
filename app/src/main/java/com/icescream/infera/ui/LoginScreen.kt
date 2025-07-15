package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Pantalla de inicio de sesión con validación, opción a recibir mensaje de error externo,
 * botón para regresar, y comentarios en español para facilitar el aprendizaje.
 */
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit, // Llamada cuando el usuario intenta iniciar sesión
    onBack: () -> Unit, // Llamada al presionar 'Regresar'
    errorMsg: String? = null // Mensaje de error del backend, opcional
) {
    var email by remember { mutableStateOf("") } // Email del usuario
    var password by remember { mutableStateOf("") } // Contraseña digitada
    var errorMessage by remember { mutableStateOf<String?>(null) } // Para validación local
    var passwordVisible by remember { mutableStateOf(false) }

    // Valida el formato del email
    fun isEmailValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Lógica al intentar iniciar sesión
    fun login() {
        errorMessage = null
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "¡UPS! Parece que tu usuario o contraseña son incorrectos"
        } else if (!isEmailValid(email)) {
            errorMessage = "¡UPS! Parece que tu usuario o contraseña son incorrectos"
        } else {
            onLogin(email, password)
        }
    }

    // Estructura visual principal usando Box y Column (Jetpack Compose)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Muestra primero error local, si no, muestra el externo
            val messageToShow = errorMessage ?: errorMsg
            messageToShow?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            // Botón para iniciar sesión
            Button(onClick = { login() }, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar sesión")
            }
        }
        // Botón para regresar a la pantalla anterior
        Button(
            onClick = onBack, modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Regresar")
        }
    }
}
