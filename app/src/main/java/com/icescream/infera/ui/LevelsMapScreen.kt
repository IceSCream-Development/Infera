package com.icescream.infera.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.icescream.infera.Level
import com.icescream.infera.R
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun LevelsMapScreen(
    levels: List<Level>,
    onLevelClick: (Level) -> Unit,
    isUnlocked: (Level) -> Boolean
) {
    // Posiciones relativas dentro de cada fondo
    val levelPositions = listOf(
        705 to 250,
        500 to 520,
        200 to 700,
        550 to 885,
        915 to 1000,
        915 to 1300,
        600 to 1420,
        365 to 1650
    )

    val density = LocalDensity.current
    val iconSizeDp = 72.dp
    val iconSizePx = with(density) { iconSizeDp.toPx() }
    // Tamaño de fondo original
    val originalWidth = 1080f
    val originalHeight = 1920f
    var bgSizePx by remember { mutableStateOf(IntSize(1, 1)) }
    val totalNiveles = levels.size
    val fondoCount = ceil(totalNiveles / levelPositions.size.toFloat()).toInt()
    val scrollState = rememberScrollState()

    // Scroll automático hacia el nivel 1 (parte baja) al entrar a la pantalla
    LaunchedEffect(bgSizePx.height) {
        if (bgSizePx.height > 1) {
            scrollState.scrollTo(scrollState.maxValue)
            // Si prefieres con animación:
            // scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Si, usar FillMaxWidth por consistencia con el Box padre
            Box(
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        bgSizePx = coordinates.size
                    }
            ) {
                if (bgSizePx.width > 1) {
                    val widthPx = bgSizePx.width.toFloat()
                    val scaleX = widthPx / originalWidth
                    val fondoTotalHeightPx = originalHeight * scaleX * fondoCount
                    val fondoTotalHeightDp = with(density) { fondoTotalHeightPx.toDp() }
                    // Stacked backgrounds (ahora de abajo hacia arriba)
                    Box(Modifier
                        .height(fondoTotalHeightDp)
                        .fillMaxWidth()) {
                        // Dibuja cada fondo tile desde abajo hacia arriba
                        for (i in (fondoCount - 1) downTo 0) {
                            Image(
                                painter = painterResource(R.drawable.levels_background),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(with(density) { (originalHeight * scaleX).toDp() })
                                    .offset(y = with(density) { (originalHeight * scaleX * i).toDp() }),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        // Dibuja todos los niveles, comenzando desde abajo
                        levels.forEachIndexed { idx, level ->
                            val fondoIndex = idx / levelPositions.size
                            val posIndex = idx % levelPositions.size
                            val (baseX, baseY) = levelPositions[posIndex]
                            // Calcula el offset Y desde ABAJO (base)
                            val px = baseX * scaleX - iconSizePx / 2f
                            val pyFromBottom =
                                baseY * scaleX - iconSizePx / 2f + (originalHeight * scaleX * fondoIndex)
                            val dpX = with(density) { px.toDp() }
                            // Invertir para que niveles crezcan hacia ARRIBA (parte baja es fondoTotalHeightPx, restamos el offset desde abajo)
                            val dpY =
                                with(density) { (fondoTotalHeightPx - pyFromBottom - iconSizePx).toDp() }
                            val iconRes =
                                if (isUnlocked(level)) R.drawable.completed_level else R.drawable.incompleted_level
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(x = dpX, y = dpY)
                                    .clickable(enabled = isUnlocked(level)) { onLevelClick(level) },
                                contentAlignment = Alignment.TopStart
                            ) {
                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = "Nivel ${level.number}",
                                    modifier = Modifier.size(iconSizeDp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
