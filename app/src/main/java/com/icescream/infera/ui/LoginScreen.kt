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
import android.util.Patterns
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.icescream.infera.R
import com.icescream.infera.data.AuthRepository
import androidx.compose.ui.platform.LocalContext

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
    var isGoogleLoading by remember { mutableStateOf(false) }   // Loading extra para Google Sign-In

    val context = LocalContext.current

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

    // --- GOOGLE SIGN-IN ---
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.result
                    val idToken = account.idToken
                    if (idToken != null) {
                        isGoogleLoading = true
                        AuthRepository.loginWithGoogle(
                            idToken = idToken,
                            onSuccess = {
                                isGoogleLoading = false
                                onLoginSuccess()
                            },
                            onError = {
                                isGoogleLoading = false
                                errorMessage = it
                            }
                        )
                    } else {
                        errorMessage = "No se pudo obtener el token de Google."
                    }
                } catch (e: Exception) {
                    errorMessage = "Fallo Google Sign-In: ${e.localizedMessage}"
                }
            } else {
                errorMessage = "Google Sign-In cancelado."
            }
        }

    fun launchGoogleSignIn() {
        errorMessage = null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        launcher.launch(client.signInIntent)
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
                enabled = !isLoading && !isGoogleLoading
            ) { Text("Iniciar sesión") }
            // ProgressBar mientras está logeando
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            // --- Botón Google Sign-In ---
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { launchGoogleSignIn() },
                enabled = !isLoading && !isGoogleLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                // Aquí puedes poner el logo de Google real tras agregarlo en drawable
                Text("Iniciar sesión con Google", color = Color.Black)
            }
            if (isGoogleLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }

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
