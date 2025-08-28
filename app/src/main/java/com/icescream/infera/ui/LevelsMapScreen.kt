package com.icescream.infera.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.icescream.infera.CoinManager
import com.icescream.infera.viewmodel.VidasViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.Icon

@Composable
fun LevelsMapScreen(
    levels: List<Level>,
    onLevelClick: (Level) -> Unit,
    isUnlocked: (Level) -> Boolean,
    nivelActualNumber: Int = 1,
    nivelActualTitulo: String = ""
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
        AppBackground()
        val context = LocalContext.current
        val coinManager = remember { CoinManager.getInstance(context) }
        val vidasVm: VidasViewModel = viewModel()
        val vidas by vidasVm.vidas.collectAsState()
        var coins by remember { mutableStateOf(0) }
        var streak by remember { mutableStateOf(0) }
        var nivel by remember { mutableStateOf(1) }
        var nivelTitulo by remember { mutableStateOf("") }
        val currentLevel = levels.firstOrNull()
        // Fetch monedas y streak de Firestore/asíncrono
        LaunchedEffect(Unit) {
            coinManager.getCoinsFromFirestore { saldo -> coins = saldo }
        }
        LaunchedEffect(Unit) {
            com.icescream.infera.data.LogrosManager(context)
                .getCurrentStreak { streakValue -> streak = streakValue }
        }
        // Puedes obtener el nivel y nombre real según tu lógica actual
        currentLevel?.let {
            nivel = it.number
            nivelTitulo = it.name
        }
        InfoBarLevels(
            vidas = vidas,
            monedas = coins,
            streak = streak,
            nivel = nivelActualNumber,
            nivelTitulo = nivelActualTitulo,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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

@Composable
fun InfoBarLevels(
    vidas: Int,
    monedas: Int,
    streak: Int,
    nivel: Int,
    nivelTitulo: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Color(0xFF5C8E46))
            .padding(top = 10.dp, bottom = 0.dp)
            .zIndex(2f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoItem(
                icon = R.drawable.lives,
                contentDesc = "Vidas",
                value = vidas.toString(),
                bgColor = Color.White,
                valueColor = Color(0xFFE84B50)
            )
            InfoItem(
                icon = R.drawable.oinkies,
                contentDesc = "Oinkies",
                value = monedas.toString(),
                bgColor = Color.White,
                valueColor = Color(0xFFB7760C)
            )
            InfoItem(
                icon = R.drawable.streak,
                contentDesc = "Racha",
                value = streak.toString(),
                bgColor = Color.White,
                valueColor = Color(0xFF7D1DD0)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nivel $nivel",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                modifier = Modifier
                    .background(Color(0xFF4D773A), RoundedCornerShape(9.dp))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = nivelTitulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 5.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
fun InfoItem(icon: Int, contentDesc: String, value: String, bgColor: Color, valueColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .heightIn(min = 30.dp)
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)), // fondo avatar
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDesc,
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}
