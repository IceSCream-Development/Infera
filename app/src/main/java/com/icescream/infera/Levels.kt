// Levels.kt
package com.icescream.infera

import com.google.gson.annotations.SerializedName

// Representa una respuesta dentro de una pregunta
data class Answer(
    @SerializedName("texto")
    val text: String,
    @SerializedName("correcta")
    val isCorrect: Boolean
)

// Representa una pregunta dentro de un nivel
data class Question(
    @SerializedName("texto")
    val text: String,
    @SerializedName("respuestas")
    val answers: List<Answer>
)

// Representa un nivel completo en tu app
data class Level(
    @SerializedName("numero")
    val number: Int,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("descripcion")
    val description: String,
    @SerializedName("preguntas")
    val questions: List<Question>
)