package com.icescream.infera.data

/**
 * Clase de datos para representar un logro en la aplicación.
 * @param id Un identificador único para el logro (ej. "nivel_1_completado").
 * @param title El título del logro que se mostrará al usuario.
 * @param description Una descripción de cómo obtener el logro.
 * @param isCompleted Indica si el logro ha sido obtenido por el usuario.
 */
data class Logro(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
