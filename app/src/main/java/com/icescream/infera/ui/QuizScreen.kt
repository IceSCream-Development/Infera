package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.icescream.infera.Level
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra las preguntas de un nivel con feedback visual y avance automático.
 *
 * @param level El objeto Level que contiene las preguntas a mostrar.
 * @param onBackClick Función para regresar a la pantalla anterior.
 * @param onLevelCompleted Función para notificar que el nivel ha sido completado.
 */
@Composable
fun QuizScreen(level: Level, onBackClick: () -> Unit, onLevelCompleted: (Level) -> Unit = {}) {
    // Colores de feedback
    val correctGreen = Color(0xFF4CAF50)        // Verde
    val correctDarkGreen =
        Color(0xFF2E7D32)    // Verde oscuro para mostrar la correcta cuando el usuario falla
    val wrongRed = Color(0xFFE53935)            // Rojo para respuesta seleccionada incorrecta

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isShowingFeedback by remember { mutableStateOf(false) }
    var isLevelFinished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var hasReportedCompletion by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val questions = level.questions
    val hasQuestions = questions.isNotEmpty()

    if (!hasQuestions) {
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
        return
    }

    if (isLevelFinished) {
        // Notificar sólo una vez que el nivel fue completado
        LaunchedEffect(Unit) {
            if (!hasReportedCompletion) {
                hasReportedCompletion = true
                onLevelCompleted(level)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¡Nivel completado!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Tu puntuación: $correctCount/${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver al mapa de niveles")
                }
            }
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]
    // Respuestas barajadas y estables por cada índice de pregunta
    val shuffledAnswers = remember(currentQuestionIndex) { currentQuestion.answers.shuffled() }
    val correctIndex = remember(currentQuestionIndex) {
        shuffledAnswers.indexOfFirst { it.isCorrect }
    }

    fun goToNextOrFinish() {
        if (currentQuestionIndex < questions.lastIndex) {
            currentQuestionIndex += 1
            selectedAnswerIndex = null
            isShowingFeedback = false
        } else {
            isLevelFinished = true
        }
    }

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
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
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
            itemsIndexed(shuffledAnswers) { index, answer ->
                val containerColor: Color = when {
                    !isShowingFeedback -> MaterialTheme.colorScheme.primaryContainer
                    selectedAnswerIndex == index && index == correctIndex -> correctGreen
                    selectedAnswerIndex == index && index != correctIndex -> wrongRed
                    selectedAnswerIndex != null && selectedAnswerIndex != correctIndex && index == correctIndex -> correctDarkGreen
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
                val textColor: Color =
                    if (containerColor == correctGreen || containerColor == correctDarkGreen || containerColor == wrongRed) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }

                Button(
                    onClick = {
                        if (isShowingFeedback) return@Button
                        selectedAnswerIndex = index
                        isShowingFeedback = true
                        val isCorrect = index == correctIndex
                        if (isCorrect) {
                            correctCount += 1
                        }
                        scope.launch {
                            if (isCorrect) delay(1500) else delay(3000)
                            goToNextOrFinish()
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = answer.text)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackClick,
            enabled = !isShowingFeedback,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver al mapa de niveles")
        }
    }
}