package com.icescream.infera.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icescream.infera.R
import com.icescream.infera.ui.theme.InferaTheme

@Composable
fun AppBackground() {
    Image(
        painter = painterResource(id = R.drawable.logo_screen),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun HomeScreen(
    seccionSeleccionada: Int,
    onSeleccionar: (Int) -> Unit,
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                NavigationBarItem(
                    selected = seccionSeleccionada == 0,
                    onClick = { onSeleccionar(0) },
                    label = { Text("Aprende") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_aprende),
                            contentDescription = "Aprende",
                            modifier = Modifier
                                .size(36.dp)
                                .background(color = Color.Transparent)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Replace with your desired color
                        selectedIconColor = Color(0xFF3EA711),
                        selectedTextColor = Color(0xFF3EA711),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 1,
                    onClick = { onSeleccionar(1) },
                    label = { Text("ChatBot") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_chatbot),
                            contentDescription = "ChatBot",
                            modifier = Modifier
                                .size(36.dp)
                                .background(color = Color.Transparent)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color(0xFF3EA711),
                        selectedTextColor = Color(0xFF3EA711),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 2,
                    onClick = { onSeleccionar(2) },
                    label = { Text("Logros") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_logros),
                            contentDescription = "Logros",
                            modifier = Modifier
                                .size(36.dp)
                                .background(color = Color.Transparent)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color(0xFF3EA711),
                        selectedTextColor = Color(0xFF3EA711),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == 3,
                    onClick = { onSeleccionar(3) },
                    label = { Text("Perfil") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_perfil),
                            contentDescription = "Perfil",
                            modifier = Modifier
                                .size(36.dp)
                                .background(color = Color.Transparent)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color(0xFF3EA711),
                        selectedTextColor = Color(0xFF3EA711),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppBackground()
            contenido(paddingValues)
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun HomeScreenPreview() {
    InferaTheme {
        HomeScreen(
            seccionSeleccionada = 0, // Provide an actual Int value
            onSeleccionar = { selectedIndex ->
                // This lambda now matches the (Int) -> Unit signature
                println("Preview: Section selected: $selectedIndex")
            },
            contenido = { paddingValues ->
                // This lambda now matches the @Composable (PaddingValues) -> Unit signature
                // You can put placeholder content here if needed for the preview
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), // Use the paddingValues
                    contentAlignment = Alignment.Center
                ) {
                    Text("Preview Content Area")
                }
            }
        )
    }
}