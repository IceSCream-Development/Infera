package com.icescream.infera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import android.util.Patterns // <--- Import necesario para validación

/**
 * Pantalla de inicio de sesión con validación, opción a recibir mensaje de error externo,
 * botón para regresar, y comentarios en español para facilitar el aprendizaje.
 */
@Composable
fun LoginScreen(
    onLogin: (String, String, (String?) -> Unit) -> Unit, 
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    errorMsg: String? = null,
    onGoToRegister: () -> Unit = {} 
) {
    // Estados para campos y manejo de errores
    var email by remember { mutableStateOf("") }               // Email que escribe el usuario
    var password by remember { mutableStateOf("") }            // Contraseña
    var errorMessage by remember { mutableStateOf<String?>(null) } // Mensajes de error
    var isLoading by remember { mutableStateOf(false) }         // Muestra un ProgressBar si se está logeando
    var passwordVisible by remember { mutableStateOf(false) }   // Si se muestra la contraseña

    // Función para validar el formato de email
    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Función llamada al presionar el botón "Iniciar sesión"
    fun handleLogin() {
        errorMessage = null
        // Validaciones básicas
        when {
            email.isBlank() || password.isBlank() ->
                errorMessage = "Completa ambos campos."
            !isEmailValid(email) ->
                errorMessage = "El formato de email no es válido."
            else -> {
                // Si pasa validaciones, intenta login
                isLoading = true
                onLogin(email, password) {
                    isLoading = false
                    if (it == null) {
                        // Login exitoso
                        onLoginSuccess()
                    } else {
                        // Muestra mensaje de error que le devuelve AuthRepository
                        errorMessage = it
                    }
                }
            }
        }
    }

    // Estructura visual principal usando Box y Column (Jetpack Compose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Formulario central
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
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
            // Botón principal de login
            Button(
                onClick = { handleLogin() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) { Text("Iniciar sesión") }
            // ProgressBar mientras está logeando
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            // Texto clickeable para ir al registro
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¿Aún no tienes cuenta?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onGoToRegister)
            )
        }
        // Botón para regresar atrás
        Button(
            onClick = onBack, modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Regresar")
        }
    }
}
