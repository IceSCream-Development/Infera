package com.icescream.infera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.icescream.infera.data.Logro
import com.icescream.infera.data.LogrosManager
import kotlinx.coroutines.delay
import com.icescream.infera.R


/**
 * Pantalla para mostrar la lista de logros del usuario.
 */
@Composable
fun LogrosScreen() {
    val context = LocalContext.current
    val logrosManager = remember { LogrosManager(context) }
    // Leer coins y logros desde Firestore
    var coinBalance by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        com.icescream.infera.CoinManager.getInstance(context).getCoinsFromFirestore { saldo ->
            coinBalance = saldo
        }
    }
    var logros by remember { mutableStateOf(logrosManager.getLogros()) }
    LaunchedEffect(Unit) {
        logrosManager.onLogrosVisited()
        logrosManager.syncLogrosWithFirestore {
            logros = logrosManager.getLogros()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo verde detrás de todo (aseguro Z-index bajo y altura grande)
        Box(
            Modifier
                .matchParentSize()
                .background(Color(0xFF7ED957))
                .zIndex(0f)
        )
        // Column con index Z superior
        Column(
            Modifier
                .fillMaxSize()
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 10.dp, start = 18.dp, end = 18.dp, // Top igual
                        bottom = 18.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Caja oinkies RECOLECTADOS, más pequeña
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .width(125.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.oinkies),
                            contentDescription = "Icono monedas",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = coinBalance.toString(),
                            color = Color(0xFFB7760C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "Oinkies Recolectados",
                        fontSize = 12.sp,
                        color = Color(0xFF929292),
                        fontWeight = FontWeight.Bold
                    )
                }
                // Imagen swiny logros_screen (más grande)
                Spacer(modifier = Modifier.weight(1f))
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.logros_screen),
                    contentDescription = "Logros Swiny",
                    modifier = Modifier.size(170.dp)
                )
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Logros Obtenidos",
                        color = Color(0xFF23232D),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Divider(
                        color = Color(0xFFEFE6D4),
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(top = 5.dp, start = 32.dp, end = 32.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 20.dp, top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        logros.forEach { logro ->
                            Box(
                                Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                            ) {
                                AchievementCard(
                                    logro,
                                    modifier = Modifier.fillMaxWidth(0.90f)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(logro: com.icescream.infera.data.Logro, modifier: Modifier = Modifier) {
    val mainColor = if (logro.isCompleted) Color(0xFFDDFCD2) else Color(0xFFDADADA)
    val titleColor = if (logro.isCompleted) Color(0xFF23232D) else Color(0xFFBCBCBC)
    val descColor = if (logro.isCompleted) Color(0xFF565555) else Color(0xFFBABABA)
    val rewardColor = Color(0xFF9CA3AF) // gris para ambos estados
    Box(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(mainColor)
            .border(BorderStroke(1.dp, Color(0xFFB4B4B4)), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 17.dp)
    ) {
        Column {
            Text(
                text = logro.title,
                color = titleColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp,
            )
            Text(
                text = logro.description,
                color = descColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 17.sp
            )
            Text(
                text = if (logro.isCompleted) "5 Oinkies Obtenidos!" else "Por desbloquear...",
                color = rewardColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 7.dp)
            )
        }
    }
}