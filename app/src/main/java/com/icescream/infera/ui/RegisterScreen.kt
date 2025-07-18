package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegister: (String, String, String, () -> Unit) -> Unit, // Ahora pasa un callback de éxito
    onBack: () -> Unit, // Llamada al presionar 'Regresar'
    errorMsg: String? = null, // Mensaje de error externo a mostrar (por ejemplo, desde el backend)
    onGoToLogin: () -> Unit = {} // Nuevo callback para ir a inicio de sesión
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

    // Para mostrar el mensaje flotante (Snackbar) al completar registro
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
            // Llama a la función de registro externa y muestra el Snackbar cuando se completa
            onRegister(username, email, password) {
                scope.launch {
                    snackbarHostState.showSnackbar("Registro completado")
                }
            }
        }
    }

    // Usamos Scaffold para mostrar Snackbar flotante cuando haya mensaje
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo de contraseña (oculto)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Confirmación de contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Prioridad de mensajes: primero errores de validación local, sino el error externo
                val messageToShow = errorMessage ?: errorMsg
                messageToShow?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // Botón principal para registrarse
                Button(onClick = { register() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrarse")
                }
                // Texto clickeable para ir a inicio de sesión
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¿Ya tienes cuenta?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onGoToLogin)
                )
            }
            // Botón "Regresar" en la parte superior izquierda
            Button(
                onClick = onBack, modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text("Regresar")
            }
        }
    }
}
