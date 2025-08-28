package com.icescream.infera.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import com.icescream.infera.Level
import com.icescream.infera.R
import com.icescream.infera.utils.LivesManager
import com.icescream.infera.viewmodel.VidasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.unit.sp
import com.icescream.infera.data.LogrosManager // Importación del gestor de logros
import com.icescream.infera.CoinManager

/**
 * Pantalla que muestra las preguntas de un nivel con feedback visual y avance automático.
 *
 * @param level El objeto Level que contiene las preguntas a mostrar.
 * @param logrosManager El gestor de logros para actualizar el progreso.
 * @param onBackClick Función para regresar a la pantalla anterior.
 * @param onLevelCompleted Función para notificar que el nivel ha sido completado.
 */
@Composable
fun QuizScreen(level: Level, logrosManager: LogrosManager, onBackClick: () -> Unit, onLevelCompleted: (Level) -> Unit = {}) {
    // Colores de feedback
    val correctGreen = Color(0xFF4CAF50)
    val correctDarkGreen = Color(0xFF2E7D32)
    val wrongRed = Color(0xFFE53935)
    val vidasVm: VidasViewModel = viewModel()
    val vidas by vidasVm.vidas.collectAsState()
    val tiempoRestanteMs by vidasVm.tiempoRestante.collectAsState()
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isShowingFeedback by remember { mutableStateOf(false) }
    var isLevelFinished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var feedbackMsg by remember { mutableStateOf<String?>(null) }
    var lastAnswerIsCorrect by remember { mutableStateOf<Boolean?>(null) }
    var mostrarDialogoSinVidas by remember { mutableStateOf(false) }
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
        CompleteLevelScreen(
            onContinue = {
                onLevelCompleted(level)
                val perfect = correctCount == questions.size
                if (perfect) {
                    CoinManager.getInstance(context).otorgarMonedasPorPregunta(1, 5)
                } else {
                    CoinManager.getInstance(context).otorgarMonedasPorPregunta(1, 3)
                }
                onBackClick()
            }
        )
        return
    }

    // ... AQUÍ el contenido principal del quiz (Column con preguntas, feedback, etc.) ...
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentQuestion = questions[currentQuestionIndex]
        // Respuestas barajadas y estables por cada índice de pregunta
        val shuffledAnswers =
            remember(currentQuestionIndex) { currentQuestion.answers.shuffled() }
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

        val yellowBtn = Color(0xFFFFD600)
        val feedbackGreen = Color(0xFF43BE57)
        val feedbackRed = Color(0xFFE34053)
        val correctBg = Color(0xFFD1FFCC)
        val incorrectBg = Color(0xFFFFD4DA)
        val borderDefault = Color(0xFFBEBEBE)
        val greyBtn = Color(0xFFDFDFDF)

        // En cada render, si vidas == 0, muestra el diálogo
        if (vidas <= 0 && !mostrarDialogoSinVidas) {
            mostrarDialogoSinVidas = true
        }

        // Imagen de fondo (quiz_background)
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.quiz_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            // Número de pregunta fijo centrado (aprox. igual altura actual)
            Text(
                text = "${(currentQuestionIndex + 1)} de ${questions.size}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 30.dp) // Ajusta este padding según la altura que quieras
            )
            // CONTENIDO DEL QUIZ ENCIMA
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 68.dp), // Ajustamos para dejar espacio al botón
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra superior personalizada
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable(onClick = onBackClick)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back_buttom),
                                contentDescription = "Regresar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Box(
                            modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
                        ) {
                            // Eliminado el número de pregunta de aquí (ahora está fijo superpuesto)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White, shape = MaterialTheme.shapes.medium)
                                .height(32.dp)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.lives),
                                    contentDescription = "Vidas",
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "$vidas",
                                    color = Color(0xFF23235D),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                                if (vidas < LivesManager.getMaxLives()) {
                                    Text(
                                        text = "  " + formatTime(tiempoRestanteMs),
                                        color = Color(0xFF23235D),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                    // Barra de progreso horizontal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp) // Ajuste aquí: ahora es igual a las barras internas
                            .padding(horizontal = 22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    Color(0xFFE1CFFE),
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((currentQuestionIndex + 1) / questions.size.toFloat())
                                .height(8.dp)
                                .background(
                                    Color(0xFF6B50DE),
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // SIN CARD: solo mostraremos el contenido directamente sobre el fondo
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(horizontal = 16.dp)
                            .defaultMinSize(minHeight = 320.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = level.name, // Título dinámico del nivel
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF6E6B7B),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF23235D),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // Opciones tipo botón con borde redondeado
                        shuffledAnswers.forEachIndexed { index, answer ->
                            val isSelected = selectedAnswerIndex == index
                            val showFeedback = selectedAnswerIndex != null && isShowingFeedback
                            val isCorrectAns = answer.isCorrect
                            val isCorrectSelection = isSelected && isCorrectAns && showFeedback
                            val isIncorrectSelection =
                                isSelected && !isCorrectAns && showFeedback
                            val shouldHighlightCorrect =
                                showFeedback && isCorrectAns && selectedAnswerIndex != null && !isSelected && !shuffledAnswers[selectedAnswerIndex!!].isCorrect
                            val borderColor = when {
                                isCorrectSelection || shouldHighlightCorrect -> feedbackGreen
                                isIncorrectSelection -> feedbackRed
                                else -> borderDefault
                            }
                            val bgColor = when {
                                isCorrectSelection || shouldHighlightCorrect -> correctBg
                                isIncorrectSelection -> incorrectBg
                                else -> Color.White
                            }
                            OutlinedButton(
                                onClick = {
                                    if (!isShowingFeedback && vidas > 0) {
                                        selectedAnswerIndex = index
                                        isShowingFeedback = false
                                        if (isCorrectAns) {
                                            feedbackMsg = "¡Buen Trabajo! "
                                            lastAnswerIsCorrect = true
                                            logrosManager.onQuestionAnsweredCorrectly()
                                        } else {
                                            feedbackMsg = "¡Ups! Respuesta Incorrecta  -1"
                                            lastAnswerIsCorrect = false
                                            vidasVm.perderVida()
                                        }
                                        isShowingFeedback = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(2.dp, borderColor),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = bgColor,
                                    contentColor = Color(0xFF23235D),
                                    disabledContentColor = Color(0xFFBEBEBE)
                                ),
                                enabled = true
                            ) {
                                Text(
                                    text = answer.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                    color = Color(0xFF23235D),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        // Feedback textual debajo de las opciones
                        if (isShowingFeedback && feedbackMsg != null) {
                            Text(
                                text = feedbackMsg ?: "",
                                color = if (lastAnswerIsCorrect == true) feedbackGreen else feedbackRed,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(38.dp))
                }
                // Botón continuar fijo abajo
                val continuarEnabled =
                    isShowingFeedback && selectedAnswerIndex != null && vidas > 0
                Button(
                    onClick = {
                        if (continuarEnabled) {
                            goToNextOrFinish()
                            selectedAnswerIndex = null
                            isShowingFeedback = false
                            feedbackMsg = null
                            lastAnswerIsCorrect = null
                        }
                    },
                    enabled = continuarEnabled,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            if (continuarEnabled) yellowBtn else greyBtn,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (continuarEnabled) yellowBtn else greyBtn,
                        contentColor = if (continuarEnabled) Color(0xFF23235D) else Color(0xFF949494)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "Continuar",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = if (continuarEnabled) Color(0xFF23235D) else Color(0xFF949494)
                    )
                }
            }
        }
    }
    if (mostrarDialogoSinVidas && vidas <= 0) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = onBackClick) {
                    Text("Salir al mapa de niveles")
                }
            },
            title = { Text("¡Sin vidas!") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Debes esperar a recuperar al menos 1 vida para seguir jugando.")
                    if (tiempoRestanteMs > 0) {
                        Text("Se añade una vida en " + formatTime(tiempoRestanteMs))
                    }
                }
            },
            dismissButton = {}
        )
    }
}

@Composable
fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = (totalSeconds / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()
    return "%02d:%02d".format(minutes, seconds)
}
