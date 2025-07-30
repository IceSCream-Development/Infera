package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    seccionSeleccionada: Int,
    onSeleccionar: (Int) -> Unit,
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = seccionSeleccionada == 0,
                    onClick = { onSeleccionar(0) },
                    label = { Text("Aprende") },
                    icon = { /* Aquí podrías poner un ícono si lo deseas */ }
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 1,
                    onClick = { onSeleccionar(1) },
                    label = { Text("ChatBot") },
                    icon = { }
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 2,
                    onClick = { onSeleccionar(2) },
                    label = { Text("Logros") },
                    icon = { }
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 3,
                    onClick = { onSeleccionar(3) },
                    label = { Text("Lecciones") },
                    icon = { }
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 4,
                    onClick = { onSeleccionar(4) },
                    label = { Text("Perfil") },
                    icon = { }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            contenido(paddingValues)
        }
    }
}
