package com.icescream.infera.ui

import androidx.compose.runtime.*
import com.icescream.infera.Level
import com.icescream.infera.ui.LevelsMapScreen
import com.icescream.infera.ui.QuizScreen

/**
 * Pantalla principal de la sección "Aprende".
 * Muestra el mapa de niveles y navega a QuizScreen cuando se selecciona un nivel.
 * Desbloquea niveles en orden secuencial.
 */
@Composable
fun AprendeScreen() {
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var highestUnlockedLevel by remember { mutableStateOf(1) } // Nivel 1 desbloqueado inicialmente

    if (selectedLevel == null) {
        LevelsMapScreen(
            onLevelClick = { level -> selectedLevel = level },
            isUnlocked = { level -> level.number <= highestUnlockedLevel }
        )
    } else {
        QuizScreen(
            level = selectedLevel!!,
            onBackClick = { selectedLevel = null },
            onLevelCompleted = { completedLevel ->
                // Desbloquear el siguiente nivel si corresponde
                if (completedLevel.number >= highestUnlockedLevel) {
                    highestUnlockedLevel = completedLevel.number + 1
                }
            }
        )
    }
}
