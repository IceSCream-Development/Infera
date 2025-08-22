package com.icescream.infera.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Clase que gestiona la lógica de los logros.
 * Aquí se definen los logros, se verifica si están completados
 * y se guarda el progreso del usuario.
 */
class LogrosManager(context: Context) {

    // Lista de todos los logros disponibles en la aplicación
    private val allLogros = listOf(
        Logro(
            id = "first_level_completed",
            title = "Primer Paso",
            description = "Completa el primer nivel."
        ),
        Logro(
            id = "five_questions_correct",
            title = "Conocimiento en Crecimiento",
            description = "Responde 5 preguntas correctamente."
        ),
        Logro(
            id = "three_levels_completed",
            title = "Maestro Principiante",
            description = "Completa 3 niveles."
        ),
        Logro(
            id = "twenty_questions_correct",
            title = "Maestro de Finanzas",
            description = "Responde 20 preguntas correctamente."
        ),
        Logro(
            id = "five_levels_completed",
            title = "Explorador Avanzado",
            description = "Completa 5 niveles."
        ),
        Logro(
            id = "ten_questions_correct",
            title = "Decidido",
            description = "Responde 10 preguntas correctamente."
        ),
        Logro(
            id = "fifty_questions_correct",
            title = "Leyenda",
            description = "Responde 50 preguntas correctamente."
        ),
        Logro(
            id = "ten_levels_completed",
            title = "Avanzando",
            description = "Completa 10 niveles."
        ),
        Logro(
            id = "perfect_level_completed",
            title = "Perfección",
            description = "Completa un nivel sin equivocarte."
        ),
        Logro(
            id = "first_attempt_level_completed",
            title = "Primer intento",
            description = "Completa un nivel en el primer intento."
        ),
        Logro(
            id = "chatbot_used",
            title = "Conversador",
            description = "Usa el ChatBot por primera vez."
        ),
        Logro(
            id = "logros_visited",
            title = "Curioso",
            description = "Entra en la sección de Logros por primera vez."
        ),
        Logro(
            id = "seven_days_played",
            title = "Paciente",
            description = "Juega durante 7 días diferentes."
        ),
        Logro(
            id = "three_levels_wrong_answer",
            title = "Persistente",
            description = "Completa 3 niveles diferentes luego de cometer al menos un error en cada uno."
        ),
        Logro(
            id = "all_sections_opened",
            title = "Explorador",
            description = "Abre todas las secciones de la app."
        )
    )

    // Usamos SharedPreferences para guardar el progreso del usuario
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("user_progress", Context.MODE_PRIVATE)

    // Keys para guardar el progreso
    private val KEY_CORRECT_ANSWERS = "correct_answers_count"
    private val KEY_LEVELS_COMPLETED = "levels_completed"
    private val KEY_UNLOCKED_LOGROS = "unlocked_logros"
    private val KEY_DAYS_PLAYED = "days_played"
    private val KEY_CHATBOT_USED = "chatbot_used"
    private val KEY_LOGROS_VISITED = "logros_visited"
    private val KEY_PERFIL_VISITED = "perfil_visited"
    private val KEY_APRENDE_VISITED = "aprende_visited"
    private val KEY_SECCIONES_VISITADAS = "secciones_visitadas"
    private val KEY_LEVELS_WITH_MISTAKES = "levels_with_mistakes"

    // Inicializamos el progreso del usuario
    private var correctAnswersCount = sharedPrefs.getInt(KEY_CORRECT_ANSWERS, 0)
    private var unlockedLogrosIds: Set<String> =
        sharedPrefs.getStringSet(KEY_UNLOCKED_LOGROS, emptySet()) ?: emptySet()

    /**
     * Devuelve la lista de logros con su estado actual (completado o no).
     */
    fun getLogros(): List<Logro> {
        return allLogros.map { logro ->
            // Si el ID del logro está en la lista de desbloqueados, lo marcamos como completado
            if (unlockedLogrosIds.contains(logro.id)) {
                logro.copy(isCompleted = true)
            } else {
                logro
            }
        }
    }

    /**
     * Llama a esta función cada vez que el usuario responde una pregunta correctamente.
     */
    fun onQuestionAnsweredCorrectly() {
        correctAnswersCount++
        sharedPrefs.edit().putInt(KEY_CORRECT_ANSWERS, correctAnswersCount).apply()
        checkAndUnlockLogros()
    }

    /**
     * Llama a esta función cada vez que un usuario completa un nivel.
     * @param levelNumber El número del nivel completado.
     */
    fun onLevelCompleted(levelNumber: Int) {
        val completedLevels = sharedPrefs.getStringSet(KEY_LEVELS_COMPLETED, mutableSetOf())
        val updatedLevels = (completedLevels ?: mutableSetOf()).toMutableSet()
        updatedLevels.add(levelNumber.toString())
        sharedPrefs.edit().putStringSet(KEY_LEVELS_COMPLETED, updatedLevels).apply()
        checkAndUnlockLogros()
    }

    /**
     * Verifica si se cumplen las condiciones para desbloquear un logro.
     */
    private fun checkAndUnlockLogros() {
        val unlockedLogros = unlockedLogrosIds.toMutableSet()

        // Regla: 5 preguntas correctas
        if (correctAnswersCount >= 5 && !unlockedLogros.contains("five_questions_correct")) {
            unlockedLogros.add("five_questions_correct")
        }

        // Regla: Nivel 1 completado
        val completedLevels = sharedPrefs.getStringSet(KEY_LEVELS_COMPLETED, emptySet())
        if (completedLevels?.contains("1") == true && !unlockedLogros.contains("first_level_completed")) {
            unlockedLogros.add("first_level_completed")
        }

        // Regla: 3 niveles completados
        if ((completedLevels?.size ?: 0) >= 3 && !unlockedLogros.contains("three_levels_completed")) {
            unlockedLogros.add("three_levels_completed")
        }

        // Regla: 20 preguntas correctas
        if (correctAnswersCount >= 20 && !unlockedLogros.contains("twenty_questions_correct")) {
            unlockedLogros.add("twenty_questions_correct")
        }

        // Regla: 5 niveles completados
        if ((completedLevels?.size
                ?: 0) >= 5 && !unlockedLogros.contains("five_levels_completed")
        ) {
            unlockedLogros.add("five_levels_completed")
        }

        // 10 y 50 preguntas correctas
        if (correctAnswersCount >= 10 && !unlockedLogros.contains("ten_questions_correct")) {
            unlockedLogros.add("ten_questions_correct")
        }
        if (correctAnswersCount >= 50 && !unlockedLogros.contains("fifty_questions_correct")) {
            unlockedLogros.add("fifty_questions_correct")
        }
        // 10 niveles completados
        if ((completedLevels?.size
                ?: 0) >= 10 && !unlockedLogros.contains("ten_levels_completed")
        ) {
            unlockedLogros.add("ten_levels_completed")
        }
        // Eventos especiales, para usar desde las pantallas:
        // desbloquear "perfect_level_completed", "first_attempt_level_completed", "chatbot_used",
        // "logros_visited", "seven_days_played", "three_levels_wrong_answer", "all_sections_opened"

        // Si se desbloqueó algún logro, actualizamos las SharedPreferences
        if (unlockedLogros.size > unlockedLogrosIds.size) {
            unlockedLogrosIds = unlockedLogros
            sharedPrefs.edit().putStringSet(KEY_UNLOCKED_LOGROS, unlockedLogrosIds).apply()
        }
    }

    // Método para desbloquear un logro manualmente (para logros por evento)
    fun unlockLogro(logroId: String) {
        if (!unlockedLogrosIds.contains(logroId)) {
            val unlockedLogros = unlockedLogrosIds.toMutableSet()
            unlockedLogros.add(logroId)
            unlockedLogrosIds = unlockedLogros
            sharedPrefs.edit().putStringSet(KEY_UNLOCKED_LOGROS, unlockedLogrosIds).apply()
        }
    }

    // ChatBot usado
    fun onChatBotUsed() {
        sharedPrefs.edit().putBoolean(KEY_CHATBOT_USED, true).apply()
        unlockLogro("chatbot_used")
        checkAllSectionsVisited()
    }

    // Sección logros visitada
    fun onLogrosVisited() {
        sharedPrefs.edit().putBoolean(KEY_LOGROS_VISITED, true).apply()
        unlockLogro("logros_visited")
        checkAllSectionsVisited()
    }

    // Visita Perfil
    fun onPerfilVisited() {
        sharedPrefs.edit().putBoolean(KEY_PERFIL_VISITED, true).apply()
        checkAllSectionsVisited()
    }

    // Visita Aprende
    fun onAprendeVisited() {
        sharedPrefs.edit().putBoolean(KEY_APRENDE_VISITED, true).apply()
        checkAllSectionsVisited()
    }

    // Revisa si secciones ha sido visitadas para el logro "Explorador"
    private fun checkAllSectionsVisited() {
        val chatbot = sharedPrefs.getBoolean(KEY_CHATBOT_USED, false)
        val logros = sharedPrefs.getBoolean(KEY_LOGROS_VISITED, false)
        val perfil = sharedPrefs.getBoolean(KEY_PERFIL_VISITED, false)
        val aprende = sharedPrefs.getBoolean(KEY_APRENDE_VISITED, false)
        if (chatbot && logros && perfil && aprende) {
            unlockLogro("all_sections_opened")
        }
    }

    // Logro Paciente: agrega el día actual (yyyy-MM-dd) y evalúa si se desbloquea
    fun onGamePlayedToday() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
        val daysPlayed: MutableSet<String> =
            sharedPrefs.getStringSet(KEY_DAYS_PLAYED, mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()
        daysPlayed.add(today)
        sharedPrefs.edit().putStringSet(KEY_DAYS_PLAYED, daysPlayed).apply()
        if (daysPlayed.size >= 7) {
            unlockLogro("seven_days_played")
        }
    }

    // Para "Perfección". Llamar con verdadero si completó el nivel sin fallos
    fun onLevelFinished(noMistakes: Boolean, levelNumber: Int) {
        if (noMistakes) {
            unlockLogro("perfect_level_completed")
        }
        // Revisa primer intento (solo está aquí para que esté junto, la lógica real ya existe después del primer intento)
        val completedLevels =
            sharedPrefs.getStringSet(KEY_LEVELS_COMPLETED, mutableSetOf()) ?: mutableSetOf()
        if (!completedLevels.contains(levelNumber.toString())) {
            unlockLogro("first_attempt_level_completed")
        }
    }

    // Para "Persistente". Llamar cuando el usuario falla alguna pregunta pero igual termina el nivel
    fun onLevelCompletedWithMistake(levelNumber: Int) {
        val levelsWithMistakes =
            sharedPrefs.getStringSet(KEY_LEVELS_WITH_MISTAKES, mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()
        levelsWithMistakes.add(levelNumber.toString())
        sharedPrefs.edit().putStringSet(KEY_LEVELS_WITH_MISTAKES, levelsWithMistakes).apply()
        if (levelsWithMistakes.size >= 3) {
            unlockLogro("three_levels_wrong_answer")
        }
    }
}