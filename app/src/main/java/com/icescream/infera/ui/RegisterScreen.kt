package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit, // Llamada cuando el usuario quiere registrarse
    onBack: () -> Unit // Llamada al presionar 'Regresar'
) {
    // Estados para los datos del formulario
    var username by remember { mutableStateOf("") } // Nombre de usuario
    var email by remember { mutableStateOf("") }    // Email del usuario
    var password by remember { mutableStateOf("") } // Contraseña digitada
    var confirmPassword by remember { mutableStateOf("") } // Confirmación de contraseña
    var errorMessage by remember { mutableStateOf<String?>(null) } // Para mostrar mensajes de error
    var passwordVisible by remember { mutableStateOf(false) } // Para gestionar si vemos la contraseña (puedes expandir esto)

    // Valida que el email tenga un formato correcto
    fun isEmailValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Lógica al presionar "Registrarse". Realiza validaciones antes de llamar onRegister
    fun register() {
        errorMessage = null // Reinicia el mensaje de error
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Todos los campos son obligatorios."
        } else if (!isEmailValid(email)) {
            errorMessage = "El email no tiene un formato válido."
        } else if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden."
        } else if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres."
        } else {
            // Si pasa todas las validaciones, llama a la función de registro externa
            onRegister(username, email, password)
        }
    }

    // Estructura visual usando Box y Column (Jetpack Compose)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido central del formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp), // Margen horizontal para apariencia limpia
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Regístrate", style = MaterialTheme.typography.headlineMedium) // Título
            Spacer(modifier = Modifier.height(24.dp)) // Espaciado
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Campo de contraseña (oculto)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation() // Mostrar/ocultar
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Confirmación de contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Muestra el mensaje de error si hay uno
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error) // Texto en color de error
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Botón principal para registrarse
            Button(onClick = { register() }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrarse")
            }
        }
        // Botón "Regresar" en la parte superior izquierda
        Button(onClick = onBack, modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp)) {
            Text("Regresar")
        }
    }
}
