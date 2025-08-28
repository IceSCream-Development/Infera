package com.icescream.infera.ui

import android.util.Patterns
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onGoToRegister: () -> Unit
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
                                onLoginSuccess()
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

    //Estructura visual principal usando Box y Column (Jetpack Compose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Title(Modifier)

            EntryBoxes(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                modifier = Modifier
            )

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
            Button(
                onClick = {
                    handleLogin()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Iniciar sesión", fontSize = 18.sp )
            }

            TextButton(
                onClick = {
                    // Navigate to create account screen

                },
                modifier = Modifier.padding(top = 0.dp, bottom = 8.dp)
            ) {
                Text(
                    "¿Olvidaste tu contraseña?",
                    fontSize = 15.sp
                )
            }

            Footer(
                Modifier,
                onGoToRegister,
                { loginWithGoogle() }
            )


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

@Composable
fun Title(modifier: Modifier){
    Text(
        text = "Infera",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(4.dp, bottom = 16.dp)
    )

    Text(
        text = "¡Bienvenido de vuelta!",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Text(
        text = "Continua aprendiendo sobre finanzas a tu ritmo",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

//Entry boxes for the email and password
@Composable
fun EntryBoxes(
    email: String,
    onEmailChange: (String) -> Unit, //Lambda function when the email changes
    password: String,
    onPasswordChange: (String) -> Unit, //Lambda function when the password changes
    modifier: Modifier
){

    //Email label with poppins font and text color
    Text(
        text = "Email",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 0.dp)
    )

    //Box where we will store the email entry
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 0.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.DarkGray,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary
        ),

        )

    //Password label with poppins font and text color
    Text(
        text = "Contraseña",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 0.dp)
    )

    //Box where we will store the password entry
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp, top = 0.dp),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.DarkGray,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            errorIndicatorColor = Color.Red
        ),
    )
}


//Footer to create an account and login with Facebook, Google and Apple
@Composable
fun Footer(
    modifier: Modifier,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit
) {

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
            text = "O Inicia Sesión Con",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
    }

    //Row to separate the logos
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //Logos(logoResId = R.drawable.logo_fb, { println("Facebook") },modifier = Modifier) //This function are the logos
        Logos(logoResId = R.drawable.logo_google, onClick = onGoogleClick, modifier = Modifier)
        //Logos(logoResId = R.drawable.logo_apple, { println("Apple") } ,modifier = Modifier)
    }

    //Row to separate the text and the Logos
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, top = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "¿No tienes una cuenta?",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier,
        )

        TextButton(
            onClick = {
                onRegisterClick()
            },
            modifier = Modifier
        ) {
            Text(
                "Registrate",
                fontSize = 15.sp,
                color = Color(0xFF4A90E2),

                )
        }
    }
}

@Composable
fun Logos(@DrawableRes logoResId: Int,
          onClick: () -> Unit,
          modifier: Modifier){
    //Facebook Logo
    Box(
        modifier = Modifier
            .size(50.dp, 50.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp, 25.dp)
                .clip(RoundedCornerShape(percent = 50)),
            contentScale = ContentScale.Crop
        )
    }

}

@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun LoginScreenPreview() {
    InferaTheme {
        LoginScreen (
            onLogin = { email, password, setErrorMsgCallback ->
                println("Preview: Login attempt with Email: $email, Password: $password")
                setErrorMsgCallback(null) // Simulate no error from login logic
            },
            onLoginSuccess = {
                println("Preview: Login Success action triggered!")
            },
            onBack = {
                println("Preview: Back button clicked")
            },
            errorMsg = null, // No external error message provided to LoginScreen
            onGoToRegister = {
                println("Preview: Go to Register clicked")
            }
        )
    }
}