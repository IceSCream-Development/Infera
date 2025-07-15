package com.icescream.infera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit, // Se llama cuando el usuario presiona 'Iniciar sesión'
    onRegisterClick: () -> Unit // Se llama cuando el usuario presiona 'Crear cuenta'
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Contenido central (título de bienvenida)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¡Bienvenido a tu app de Finanzas!",
                color = MaterialTheme.colorScheme.onBackground // Color adaptable
            )
        }

        // Botones de acciones en la parte inferior de la pantalla
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onLoginClick) {
                Text("Iniciar sesión")
            }
            Button(onClick = onRegisterClick) {
                Text("Crear cuenta")
            }
        }
    }
}
