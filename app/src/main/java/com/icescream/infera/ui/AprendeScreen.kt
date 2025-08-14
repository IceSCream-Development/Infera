package com.icescream.infera.ui

import androidx.compose.runtime.Composable
import com.icescream.infera.ui.LevelsMapScreen

/**
 * Esta es la pantalla principal de la sección "Aprende".
 * Ahora muestra el mapa de niveles completo.
 */
@Composable
fun AprendeScreen() {
    // Al llamar a LevelsMapScreen(), se muestra todo el contenido de esa pantalla.
    LevelsMapScreen(
        onLevelClick = TODO()
    )
}
