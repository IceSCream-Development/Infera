package com.icescream.infera.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.icescream.infera.Level
import com.icescream.infera.R
import com.icescream.infera.ui.theme.InferaTheme
import java.io.InputStreamReader

/**
 * Pantalla que muestra el mapa de niveles.
 *
 * @param onLevelClick es una función que se llama cuando un nivel es presionado.
 * Pasa el objeto Level completo para que la pantalla de preguntas sepa qué mostrar.
 */
@Composable
fun LevelsMapScreen(onLevelClick: (Level) -> Unit) {
    val context = LocalContext.current

    val levels = remember {
        val gson = Gson()
        val inputStream = context.resources.openRawResource(R.raw.levels)
        val reader = InputStreamReader(inputStream)
        gson.fromJson(reader, Array<Level>::class.java).toList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(levels) { level ->
            LevelNode(
                level = level,
                // Aquí usamos el evento de clic y llamamos a la función onLevelClick
                onLevelClick = onLevelClick
            )
        }
    }
}

@Composable
fun LevelNode(level: Level, onLevelClick: (Level) -> Unit) {
    Card(
        shape = CircleShape,
        modifier = Modifier
            .size(80.dp)
            .clickable { onLevelClick(level) }, // Hacemos que el Card sea clickable
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${level.number}",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}