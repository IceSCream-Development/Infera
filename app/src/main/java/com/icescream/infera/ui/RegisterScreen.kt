package com.icescream.infera.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.icescream.infera.R
import com.icescream.infera.ui.theme.InferaTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.icescream.infera.data.AuthRepository

@Composable
fun RegisterScreen(
    onRegister: (String, String, String, () -> Unit) -> Unit, // Ahora pasa un callback de éxito
    onBack: () -> Unit, // Llamada al presionar 'Regresar'
    errorMsg: String? = null, // Mensaje de error externo a mostrar (por ejemplo, desde el backend)
    onGoToLogin: () -> Unit = {}, // Nuevo callback para ir a inicio de sesión
    onGoogleRegisterSuccess: () -> Unit = {} // Nuevo callback para ir directo a WelcomeUserScreen tras Google
) {
    // Estados para los datos del formulario
    var username by remember { mutableStateOf("") } // Nombre de usuario
    var email by remember { mutableStateOf("") }    // Email del usuario
    var password by remember { mutableStateOf("") } // Contraseña digitada
    var confirmPassword by remember { mutableStateOf("") } // Confirmación de contraseña
    var errorMessage by remember { mutableStateOf<String?>(null) } // Para mostrar mensajes de error
    var passwordVisible by remember { mutableStateOf(false) } // Para gestionar si vemos la contraseña (puedes expandir esto)
    var checked by remember { mutableStateOf(false) }

    // Valida que el email tenga un formato correcto
    fun isEmailValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Para mostrar el mensaje flotante (Snackbar) al completar registro
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.result!!
                    val idToken = account.idToken
                    if (idToken != null) {
                        AuthRepository.loginWithGoogle(
                            idToken = idToken,
                            onSuccess = { perfil ->
                                onGoogleRegisterSuccess()
                            },
                            onError = { errorMsg ->
                                errorMessage = errorMsg
                            }
                        )
                    } else {
                        errorMessage = "No se pudo obtener token de Google."
                    }
                } catch (e: Exception) {
                    errorMessage = "Google Sign-In falló: ${e.localizedMessage}"
                }
            }
        }

    fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Contenido central del formulario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp), // Margen horizontal para apariencia limpia
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                TitleRegister(Modifier)

                Spacer(modifier = Modifier.height(24.dp)) // Espaciado

                Text(
                    text = "Nombre de Usuario",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo de contraseña (oculto)
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Confirmación de contraseña

                Text(
                    text = "Confirmar Contraseña",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Prioridad de mensajes: primero errores de validación local, sino el error externo
                val messageToShow = errorMessage ?: errorMsg
                messageToShow?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)

                }

                //Row to separate the footer, and write the text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.Gray
                    )
                    Text(
                        text = "O Registrate Con",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.Gray
                    )
                }

                Logos(
                    logoResId = R.drawable.logo_google,
                    onClick = { loginWithGoogle() },
                    modifier = Modifier)

                TermsAndConditionsCheckbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    onTermsClick = { /* abrir pantalla de términos o navegador */ }
                )


                // Botón principal para registrarse
                Button(
                    onClick = { register() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )

                {
                    Text("Comenzar")
                }

                //Row to separate the text and the Logos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "¿Ya tienes una cuenta?",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier,
                    )

                    TextButton(
                        onClick = {
                            onGoToLogin()
                        },
                        modifier = Modifier
                    ) {
                        Text(
                            "Inicia Sesión",
                            fontSize = 15.sp,
                            color = Color(0xFF4A90E2),

                            )
                    }
                }

            }


            // Botón para regresar a la pantalla anterior
            OutlinedButton(
                onClick = onBack, modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFF4A90E2))
            ) {
                Text("Regresar")
            }


        }
    }
}


@Composable
fun TitleRegister(modifier: Modifier){
    Text(
        text = "Infera",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(4.dp, bottom = 8.dp, top = 40.dp)
    )

    Text(
        text = "¡Nos alegra que estés aquí!",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Text(
        text = "Aprende sobre finanzas a tu ritmo con la ayuda de Swiny",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 4.dp)

    )
}

@Composable
fun TermsAndConditionsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 16.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF6200EE),
                checkmarkColor = Color(0xFFFFFAEF)
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Acepto los",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier,
        )

        TextButton(
            onClick = {

            },
            modifier = Modifier
        ) {
            Text(
                "Términos y Condiciones",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

    }
}


@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun RegisterScreenPreview() {
    InferaTheme {
        RegisterScreen(
            onRegister = { username, email, password, onSuccessSnackbar ->
                // En el preview, esta función no necesita hacer el registro real.
                // Puedes imprimir algo para verificar o simplemente dejarla vacía.
                println("Preview: Intento de registro con Usuario: $username, Email: $email")
                onSuccessSnackbar() // Simula llamar al snackbar
            },
            onBack = {
                println("Preview: Botón de volver presionado")
            },
            errorMsg = null, // Preview sin mensaje de error
            onGoToLogin = {},
            onGoogleRegisterSuccess = {} // Nuevo callback
        )
    }
}