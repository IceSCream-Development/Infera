package com.icescream.infera.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Pantalla de inicio de sesión con validación, opción a recibir mensaje de error externo,
 * botón para regresar, y comentarios en español para facilitar el aprendizaje.
 */
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onBack: () -> Unit,
    errorMsg: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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


     //Estructura visual principal usando Box y Column (Jetpack Compose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .background(Color(0xFFFFFAEF))
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
                    login()
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

            Footer(Modifier)


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
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Text(
        text = "Continua aprendiendo sobre finanzas a tu ritmo",
        style = MaterialTheme.typography.bodySmall,
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
            focusedContainerColor = Color(0xFFF0E9D7),
            unfocusedContainerColor = Color(0xFFF0E9D7)
        ),

    )

    //Password label with poppins font and text color
    Text(
        text = "Contraseña",
        style = MaterialTheme.typography.bodySmall,
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
            focusedContainerColor = Color(0xFFF0E9D7),
            unfocusedContainerColor = Color(0xFFF0E9D7),
            errorIndicatorColor = Color.Red
        ),
    )
}


//Footer to create an account and login with Facebook, Google and Apple
@Composable
fun Footer(modifier: Modifier) {

    //Row to separate the footer, and write the text
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
        Text(
            text = "O Inicia Sesión Con",
            color = Color.Gray,
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
        modifier = Modifier.fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Logos(logoResId = R.drawable.logo_fb, { println("Facebook") },modifier = Modifier) //This function are the logos
        Logos(logoResId = R.drawable.logo_google, { println("Google") } ,modifier = Modifier)
        Logos(logoResId = R.drawable.logo_apple, { println("Apple") } ,modifier = Modifier)
    }

    //Row to separate the text and the Logos
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, top = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "¿No tienes una cuenta?",
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
                "Registrate",
                fontSize = 15.sp,
                color = Color(0xFF4A90E2),

                )
        }
    }
}

@Composable
fun Logos(@DrawableRes logoResId: Int,
          OnClick: () -> Unit,
          modifier: Modifier){
    //Facebook Logo
    Box(
        modifier = Modifier
            .size(50.dp, 50.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(percent = 50))
            .clickable(onClick = OnClick),
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
            onLogin = { email, password ->
                println("Preview: Login attempt with Email: $email, Password: $password")
            },
            onBack = {
                println("Preview: Back button clicked")
            },
            errorMsg = "Invalid username or password."
        )
    }
}