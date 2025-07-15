package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit, // Se llama cuando el usuario presiona 'Iniciar sesión'
    onRegisterClick: () -> Unit // Se llama cuando el usuario presiona 'Crear cuenta'
) {
    // Box: Permite superponer elementos. Aquí, gestionamos el diseño principal.
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido central (título de bienvenida)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Bienvenido a tu app de Finanzas!") // Personaliza el título principal de bienvenida
        }

        // Botones de acciones en la parte inferior de la pantalla
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Alinea la fila al fondo del contenedor
                .padding(bottom = 32.dp), // Margen desde la parte inferior
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre botones
        ) {
            Button(onClick = onLoginClick) {
                Text("Iniciar sesión") // Botón para ir a la pantalla de login
            }
            Button(onClick = onRegisterClick) {
                Text("Crear cuenta") // Botón para ir a la pantalla de registro
            }
        }
    }
}
