package com.icescream.infera.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.icescream.infera.Level
import com.icescream.infera.ui.LevelsMapScreen
import com.icescream.infera.ui.QuizScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import android.content.Context

/**
 * Pantalla principal de la sección "Aprende".
 * Muestra el mapa de niveles y navega a QuizScreen cuando se selecciona un nivel.
 * Desbloquea niveles en orden secuencial.
 */
@Composable
fun AprendeScreen() {
    val context = LocalContext.current
    // Cargar la lista real de niveles desde raw solo 1 vez
    val levels: List<Level> by remember {
        mutableStateOf(loadLevelsFromRaw(context))
    }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var highestUnlockedLevel by remember { mutableStateOf(1) } // Nivel 1 desbloqueado inicialmente
    var nivelesCompletados by remember { mutableStateOf<List<Int>>(emptyList()) }
    var cargandoProgreso by remember { mutableStateOf(true) }

    // Recupera el progreso al entrar a AprendeScreen
    LaunchedEffect(Unit) {
        cargandoProgreso = true
        com.icescream.infera.data.AuthRepository.obtenerProgresoNiveles(
            onSuccess = { completados ->
                nivelesCompletados = completados
                // El nivel más alto desbloqueado será el último completado + 1, mínimo 1
                highestUnlockedLevel =
                    if (completados.isNotEmpty()) completados.maxOrNull()!! + 1 else 1
                cargandoProgreso = false
            },
            onError = {
                // Puedes mostrar un mensaje de error si lo deseas
                cargandoProgreso = false
            }
        )
    }

    if (cargandoProgreso) {
        androidx.compose.material3.CircularProgressIndicator()
        return
    }

    if (selectedLevel == null) {
        LevelsMapScreen(
            levels = levels,
            onLevelClick = { level -> selectedLevel = level },
            isUnlocked = { level -> level.number <= highestUnlockedLevel }
        )
    } else {
        QuizScreen(
            level = selectedLevel!!,
            onBackClick = { selectedLevel = null },
            onLevelCompleted = { completedLevel ->
                com.icescream.infera.data.AuthRepository.guardarNivelCompletado(
                    nivel = completedLevel.number,
                    onSuccess = {
                        // Actualiza localmente la lista de completados y el nivel desbloqueado
                        nivelesCompletados = nivelesCompletados + completedLevel.number
                        highestUnlockedLevel =
                            (nivelesCompletados.maxOrNull() ?: completedLevel.number) + 1
                        selectedLevel = null
                    },
                    onError = { selectedLevel = null } // Manejo simple: regresa en caso de error
                )
            }
        )
    }
}

// Función utilitaria para leer el archivo levels.json desde res/raw
fun loadLevelsFromRaw(context: Context): List<Level> {
    return try {
        val inputStream = context.resources.openRawResource(
            context.resources.getIdentifier("levels", "raw", context.packageName)
        )
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val levelType = object : TypeToken<List<Level>>() {}.type
        Gson().fromJson(jsonString, levelType)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
