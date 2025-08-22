package com.icescream.infera.data

import android.content.Context
import com.google.gson.Gson
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

/**
 * Clase que gestiona la lógica de los logros.
 * Aquí se definen los logros, se verifica si están completados
 * y se guarda el progreso del usuario.
 */
class LogrosManager(private val context: Context) {

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

    // Usamos Firestore para guardar el progreso del usuario
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Inicializamos el progreso del usuario
    private var correctAnswersCount: Int = 0
    private var unlockedLogrosIds: Set<String> = emptySet()

    init {
        // Al inicializar, sincroniza los contadores y flags relevantes desde Firestore
        syncLogrosWithFirestore {}
        syncCorrectAnswersFromFirestore {}
        syncDaysPlayedFromFirestore {}
        syncLevelsWithMistakesFromFirestore {}
        syncSectionFlagsFromFirestore {}
    }

    // Nueva función para sincronizar logros con Firestore
    fun syncLogrosWithFirestore(onFinished: () -> Unit = {}) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val lista = doc.get("logros_desbloqueados") as? List<String> ?: emptyList()
                unlockedLogrosIds = lista.toSet()
                onFinished()
            }
            .addOnFailureListener { onFinished() }
    }

    // Nueva función para guardar logros completados en Firestore
    private fun saveLogrosToFirestore() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid)
            .update("logros_desbloqueados", unlockedLogrosIds.toList())
    }

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
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val actual = (doc.getLong("correct_answers_count") ?: 0L).toInt() + 1
                correctAnswersCount = actual
                db.collection("users").document(uid).update("correct_answers_count", actual)
                checkAndUnlockLogros()
            }
    }

    /**
     * Llama a esta función cada vez que un usuario completa un nivel.
     * @param levelNumber El número del nivel completado.
     */
    fun onLevelCompleted(levelNumber: Int) {
        checkAndUnlockLogrosWithNiveles(levelNumber)
    }

    /**
     * Verifica si se cumplen las condiciones para desbloquear un logro con niveles.
     */
    fun checkAndUnlockLogrosWithNiveles(levelNumber: Int) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val nivelesCompletados = doc.get("niveles_completados") as? List<Int> ?: emptyList()
                val updatedNivelesCompletados = nivelesCompletados.toMutableList()
                if (!updatedNivelesCompletados.contains(levelNumber)) {
                    updatedNivelesCompletados.add(levelNumber)
                    db.collection("users").document(uid)
                        .update("niveles_completados", updatedNivelesCompletados)
                    checkAndUnlockLogrosWithNivelesList(updatedNivelesCompletados)
                }
            }
    }

    /**
     * Verifica si se cumplen las condiciones para desbloquear un logro con niveles.
     */
    fun checkAndUnlockLogrosWithNivelesList(nivelesCompletados: List<Int>) {
        val unlockedLogros = unlockedLogrosIds.toMutableSet()
        // Regla: Nivel 1 completado
        if (nivelesCompletados.contains(1) && !unlockedLogros.contains("first_level_completed")) {
            unlockedLogros.add("first_level_completed")
        }
        // Regla: 3 niveles completados
        if (nivelesCompletados.size >= 3 && !unlockedLogros.contains("three_levels_completed")) {
            unlockedLogros.add("three_levels_completed")
        }
        // Regla: 5 niveles completados
        if (nivelesCompletados.size >= 5 && !unlockedLogros.contains("five_levels_completed")) {
            unlockedLogros.add("five_levels_completed")
        }
        // Regla: 10 niveles completados
        if (nivelesCompletados.size >= 10 && !unlockedLogros.contains("ten_levels_completed")) {
            unlockedLogros.add("ten_levels_completed")
        }
        // Después, actualizar si hay cambios
        if (unlockedLogros.size > unlockedLogrosIds.size) {
            unlockedLogrosIds = unlockedLogros
            saveLogrosToFirestore()
        }
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

        // Regla: 20 preguntas correctas
        if (correctAnswersCount >= 20 && !unlockedLogros.contains("twenty_questions_correct")) {
            unlockedLogros.add("twenty_questions_correct")
        }
        // 10 y 50 preguntas correctas
        if (correctAnswersCount >= 10 && !unlockedLogros.contains("ten_questions_correct")) {
            unlockedLogros.add("ten_questions_correct")
        }
        if (correctAnswersCount >= 50 && !unlockedLogros.contains("fifty_questions_correct")) {
            unlockedLogros.add("fifty_questions_correct")
        }
        // Eventos especiales y preguntas siguen funcionando igual
        if (unlockedLogros.size > unlockedLogrosIds.size) {
            unlockedLogrosIds = unlockedLogros
            saveLogrosToFirestore()
        }
    }

    // Método para desbloquear un logro manualmente (para logros por evento)
    fun unlockLogro(logroId: String) {
        if (!unlockedLogrosIds.contains(logroId)) {
            val unlockedLogros = unlockedLogrosIds.toMutableSet()
            unlockedLogros.add(logroId)
            unlockedLogrosIds = unlockedLogros
            saveLogrosToFirestore()
        }
    }

    // ChatBot usado
    fun onChatBotUsed() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).update("chatbot_used", true)
            .addOnSuccessListener {
                unlockLogro("chatbot_used")
                checkAllSectionsVisited()
            }
            .addOnFailureListener {}
    }

    // Sección logros visitada
    fun onLogrosVisited() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).update("logros_visited", true)
            .addOnSuccessListener {
                unlockLogro("logros_visited")
                checkAllSectionsVisited()
            }
            .addOnFailureListener {}
    }

    // Visita Perfil
    fun onPerfilVisited() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).update("perfil_visited", true)
            .addOnSuccessListener { checkAllSectionsVisited() }
            .addOnFailureListener {}
    }

    // Visita Aprende
    fun onAprendeVisited() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).update("aprende_visited", true)
            .addOnSuccessListener { checkAllSectionsVisited() }
            .addOnFailureListener {}
    }

    // Revisa si secciones ha sido visitadas para el logro "Explorador"
    private fun checkAllSectionsVisited() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val chatbot = doc.getBoolean("chatbot_used") ?: false
            val logros = doc.getBoolean("logros_visited") ?: false
            val perfil = doc.getBoolean("perfil_visited") ?: false
            val aprende = doc.getBoolean("aprende_visited") ?: false
            if (chatbot && logros && perfil && aprende) {
                if (!unlockedLogrosIds.contains("all_sections_opened")) {
                    unlockLogro("all_sections_opened")
                }
            }
        }
    }

    // Logro Paciente: agrega el día actual (yyyy-MM-dd) y evalúa si se desbloquea
    fun onGamePlayedToday() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val daysPlayed =
                (doc.get("days_played") as? List<String>)?.toMutableSet() ?: mutableSetOf()
            daysPlayed.add(today)
            db.collection("users").document(uid).update("days_played", daysPlayed.toList())
            if (daysPlayed.size >= 7) {
                unlockLogro("seven_days_played")
            }
        }
    }

    // Para "Perfección". Llamar con verdadero si completó el nivel sin fallos
    fun onLevelFinished(noMistakes: Boolean, levelNumber: Int) {
        if (noMistakes) {
            unlockLogro("perfect_level_completed")
        }
        // Ya no revisa primer intento con storage local, debe hacerse sólo usando la lista de completados en Firebase.
    }

    // Para "Persistente". Llamar cuando el usuario falla alguna pregunta pero igual termina el nivel
    fun onLevelCompletedWithMistake(levelNumber: Int) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val levelsWithMistakes =
                (doc.get("levels_with_mistakes") as? List<Int>)?.toMutableSet() ?: mutableSetOf()
            levelsWithMistakes.add(levelNumber)
            db.collection("users").document(uid)
                .update("levels_with_mistakes", levelsWithMistakes.toList())
            if (levelsWithMistakes.size >= 3) {
                unlockLogro("three_levels_wrong_answer")
            }
        }
    }

    private fun syncCorrectAnswersFromFirestore(onFinished: () -> Unit = {}) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val actual = (doc.getLong("correct_answers_count") ?: 0L).toInt()
            correctAnswersCount = actual
            onFinished()
        }.addOnFailureListener { onFinished() }
    }

    private fun syncDaysPlayedFromFirestore(onFinished: () -> Unit = {}) {
        onFinished()
    }

    private fun syncLevelsWithMistakesFromFirestore(onFinished: () -> Unit = {}) {
        onFinished()
    }

    // Sincronizar flags de visita a secciones (dummy; sólo para el init)
    private fun syncSectionFlagsFromFirestore(onFinished: () -> Unit = {}) {
        onFinished()
    }

    // Para "Perfección" y "Primer intento"
    fun onLevelFinished(noMistakes: Boolean, levelNumber: Int, nivelesCompletados: List<Int>) {
        if (noMistakes) {
            unlockLogro("perfect_level_completed")
        }
        // Ya no se verifica "Primer intento" porque ese logro ha sido eliminado.
    }
}