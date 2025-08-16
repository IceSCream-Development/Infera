package com.icescream.infera.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icescream.infera.ui.theme.InferaTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegister: (String, String, String, () -> Unit) -> Unit, // Ahora pasa un callback de éxito
    onBack: () -> Unit, // Llamada al presionar 'Regresar'
    errorMsg: String? = null // Mensaje de error externo a mostrar (por ejemplo, desde el backend)
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
                .padding(innerPadding)
                .background(Color(0xFFFFFAEF))
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
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 0.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo de contraseña (oculto)
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodySmall,
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
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Confirmación de contraseña

                Text(
                    text = "Confirmar Contraseña",
                    style = MaterialTheme.typography.bodySmall,
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
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Prioridad de mensajes: primero errores de validación local, sino el error externo
                val messageToShow = errorMessage ?: errorMsg
                messageToShow?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

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
                        .height(64.dp)
                )

                {
                    Text("Comenzar")
                }

                //Row to separate the text and the Logos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp, top = 30.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "¿Ya tienes una cuenta?",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier,
                    )

                    TextButton(
                        onClick = {

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
            .padding(4.dp, bottom = 16.dp, top = 50.dp)
    )

    Text(
        text = "¡Nos alegra que estés aquí!",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Text(
        text = "Aprende sobre finanzas a tu ritmo con la ayuda de Swiny",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)

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
        modifier = Modifier.padding(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF6200EE) // morado similar al de la imagen
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Acepto los",
            fontSize = 15.sp,
            color = Color.Gray,
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
                color = Color(0xFF4A90E2)
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
            errorMsg = null // Preview sin mensaje de error
        )
    }
}