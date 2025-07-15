package com.icescream.infera.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.vectorResource

/**
 * HomeScreen con barra de navegación inferior.
 * Incluye 5 secciones/tab-espacios. Las puedes personalizar fácilmente.
 */

@Composable
fun HomeScreen(
    seccionSeleccionada: Int = 0,                  // Índice de la pestaña activa
    onSeleccionar: (Int) -> Unit = {}              // Al seleccionar otra pestaña
) {
    // Nombres de ejemplo para las 5 secciones
    val items = listOf(
        "Principal", "Retos", "Progreso", "Perfil", "Ajustes"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = seccionSeleccionada == index,
                        onClick = { onSeleccionar(index) },
                        label = { Text(label) },
                        icon = {
                            // Puedes cambiar los iconos por reales usando ImageVector o Painter
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_view),
                                contentDescription = label
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        // Muestra el contenido de la sección activa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla: " + items[seccionSeleccionada])
        }
    }
}
