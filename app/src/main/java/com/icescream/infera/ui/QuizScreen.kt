package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.icescream.infera.Level

/**
 * Pantalla que muestra las preguntas de un nivel.
 *
 * @param level El objeto Level que contiene las preguntas a mostrar.
 * @param onBackClick Función para regresar a la pantalla anterior.
 */
@Composable
fun QuizScreen(level: Level, onBackClick: () -> Unit) {
    val currentQuestion = level.questions.firstOrNull()

    if (currentQuestion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay preguntas para este nivel.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nivel ${level.number}: ${level.name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = currentQuestion.text,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentQuestion.answers) { answer ->
                    Button(
                        onClick = { /* Aquí irá la lógica para verificar la respuesta */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = answer.text)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de atrás hacia abajo

            // Botón para regresar a la pantalla anterior
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al mapa de niveles")
            }
        }
    }
}