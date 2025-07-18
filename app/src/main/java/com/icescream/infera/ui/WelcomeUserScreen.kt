package com.icescream.infera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = username?.let { "¡Bienvenido/a, $it!" } ?: "¡Bienvenido/a!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ya formas parte de nuestra comunidad. Empieza cuando quieras.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Vamos")
            }
        }
    }
}
