package com.icescream.infera.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.icescream.infera.data.LogrosManager
import com.icescream.infera.ui.AprendeScreen
import com.icescream.infera.ui.ChatBotScreen
import com.icescream.infera.ui.LeccionesScreen
import com.icescream.infera.ui.LogrosScreen
import com.icescream.infera.ui.PerfilScreen

@Composable
fun InferaMainApp() {
    // Obtenemos el contexto actual de la aplicación
    val context = LocalContext.current
    // Creamos una instancia de nuestro LogrosManager.
    // Usamos 'remember' para que no se vuelva a crear cada vez.
    val logrosManager = remember { LogrosManager(context) }

    // Estado que controla la pestaña actual de la barra de navegación inferior
    var homeTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = homeTabIndex == 0,
                    onClick = { homeTabIndex = 0 },
                    icon = { Spacer(modifier = Modifier.width(1.dp)) },
                    label = { Text("Aprende") })
                NavigationBarItem(
                    selected = homeTabIndex == 1,
                    onClick = { homeTabIndex = 1 },
                    icon = { Spacer(modifier = Modifier.width(1.dp)) },
                    label = { Text("ChatBot") })
                NavigationBarItem(
                    selected = homeTabIndex == 2,
                    onClick = { homeTabIndex = 2 },
                    icon = { Spacer(modifier = Modifier.width(1.dp)) },
                    label = { Text("Logros") })
                NavigationBarItem(
                    selected = homeTabIndex == 3,
                    onClick = { homeTabIndex = 3 },
                    icon = { Spacer(modifier = Modifier.width(1.dp)) },
                    label = { Text("Lecciones") })
                NavigationBarItem(
                    selected = homeTabIndex == 4,
                    onClick = { homeTabIndex = 4 },
                    icon = { Spacer(modifier = Modifier.width(1.dp)) },
                    label = { Text("Perfil") })
            }
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (homeTabIndex) {
                0 -> AprendeScreen() // Ya no recibe logrosManager
                1 -> ChatBotScreen()
                2 -> LogrosScreen() // Ya no recibe logros
                3 -> LeccionesScreen()
                4 -> PerfilScreen()
            }
        }
    }
}