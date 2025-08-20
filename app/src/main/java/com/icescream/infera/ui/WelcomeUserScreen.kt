package com.icescream.infera.ui

import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icescream.infera.ui.theme.InferaTheme
import com.icescream.infera.R

/**
 * Pantalla de bienvenida que aparece tras registro/login exitoso.
 * Muestra un mensaje y un botón para continuar a la pantalla principal (Home).
 */
@Composable
fun WelcomeUserScreen(
    username: String? = null,        // Puedes mostrar el nombre si tienes el dato
    onContinue: () -> Unit           // Acción al pulsar el botón "Vamos"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7ed957))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = username?.let { "¡Bienvenido/a, $it!" } ?: "¡Bienvenido/a!",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¡Te estábamos esperando!",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.swiny_welcome),
                contentDescription = null,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Swiny trajo consejos, y Piko muchas preguntas (como siempre).",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "¿Empezamos?",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE046)
                ),
                modifier = Modifier.fillMaxWidth(0.9f),
            ) {
                Text(
                    "Vamos",
                    color = Color(0xFF9C6A18)
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun WelcomeUserScreenPreview() {
    InferaTheme {
        WelcomeUserScreen(
            username = "Mane",
            onContinue = {}
        )
    }
}