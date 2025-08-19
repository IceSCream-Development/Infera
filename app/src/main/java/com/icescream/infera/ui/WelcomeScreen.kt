package com.icescream.infera.ui
import com.icescream.infera.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icescream.infera.ui.theme.InferaTheme


val poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_light, FontWeight.Light)
)


@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 200.dp  )
    ) {
        Title(
            title1 = stringResource(R.string.welcome_title),
            title2 = "Infera",
            modifier = Modifier
        )
        Body(
            presentation = stringResource(R.string.presentation),
            question = stringResource(R.string.question),
            modifier = Modifier
        )
        WelcomeButtons(
            modifier = Modifier,
            onLoginClick = onLoginClick, // Se llama cuando el usuario presiona 'Iniciar sesión'
            onRegisterClick = onRegisterClick
        )
    }
}

//TÍTULO DE BIENVENIDA

@Composable
fun Title(title1: String, title2: String, modifier: Modifier = Modifier) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title1,
                style = TextStyle(
                    fontFamily = poppins,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = modifier
                    .padding(4.dp)
            )
        }
        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title2,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier
                    .padding(4.dp)
            )
        }
    }
}

//Esto es el texto
@Composable
fun Body(presentation: String, question: String, modifier: Modifier = Modifier) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = presentation,
                style = TextStyle(
                    fontFamily = poppins,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp
                ),
                modifier = modifier
                    .padding(4.dp)
            )
        }

        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = question,
                style = TextStyle(
                    fontFamily = poppins,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = modifier
                    .padding(top = 70.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
fun WelcomeButtons(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit ) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //Botón Iniciar Sesión
            Button(
                onClick = {
                    onLoginClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 17.sp
                )
            }
        }

        //Botón Crear Cuenta
        Row (
            horizontalArrangement = Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    onRegisterClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFF4A90E2)),
                modifier = Modifier
                    .padding(8.dp)
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
    fun WelcomeScreenPreview() {
    InferaTheme {
        WelcomeScreen(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
