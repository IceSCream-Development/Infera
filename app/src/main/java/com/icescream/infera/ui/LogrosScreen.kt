package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icescream.infera.data.Logro
import com.icescream.infera.data.LogrosManager
import kotlinx.coroutines.delay

/**
 * Pantalla para mostrar la lista de logros del usuario.
 */
@Composable
fun LogrosScreen() {
    val context = LocalContext.current
    val logrosManager = remember { LogrosManager(context) }
    // Estado que actualiza la lista de logros tras visitar la sección
    var logros by remember { mutableStateOf(logrosManager.getLogros()) }
    val reloadState = rememberUpdatedState(logrosManager)
    LaunchedEffect(Unit) {
        logrosManager.onLogrosVisited()
        // Espera breve para asegurar que el logros_desbloqueados se actualiza en Firestore
        delay(500)
        logrosManager.syncLogrosWithFirestore {
            logros = logrosManager.getLogros()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Logros",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logros) { logro ->
                LogroItem(logro)
            }
        }
    }
}

/**
 * Componente para mostrar un solo logro en la lista.
 * Cambia de color dependiendo si está completado o no.
 */
@Composable
fun LogroItem(logro: Logro) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.isCompleted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = logro.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (logro.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = logro.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (logro.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}