package com.icescream.infera.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import kotlinx.coroutines.*
import com.icescream.infera.data.ChatRepository
import androidx.compose.ui.platform.LocalContext
import com.icescream.infera.data.LogrosManager
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.icescream.infera.R
import com.icescream.infera.CoinManager


// Modelo de mensaje
data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatBotScreen() {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("chatbot_history", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    // Función para leer historial guardado
    fun loadHistory(): MutableList<ChatMessage> {
        val json = prefs.getString("history", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ChatMessage>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf(ChatMessage("¡Hola! ¿En qué puedo ayudarte?", false))
        }
    }

    // Función para guardar historial
    fun saveHistory(history: List<ChatMessage>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history", json).apply()
    }

    val initialMessages = loadHistory()
    val messages =
        remember { mutableStateListOf<ChatMessage>().also { it.addAll(initialMessages) } }

    val scope = rememberCoroutineScope()
    val logrosManager = remember { LogrosManager(context) }
    LaunchedEffect(Unit) { logrosManager.onChatBotUsed() }

    fun addMessageAndPersist(msg: ChatMessage) {
        messages.add(msg)
        saveHistory(messages)
    }

    val coinManager = remember { CoinManager.getInstance(context) }
    var canSend by remember { mutableStateOf(true) }
    var showCoinWarning by remember { mutableStateOf(false) }
    val coinPrice = 1
    var coinBalance by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        coinManager.getCoinsFromFirestore { saldo ->
            coinBalance = saldo
            canSend = saldo >= coinPrice
        }
    }
    LaunchedEffect(coinBalance) {
        if (coinBalance < coinPrice) {
            canSend = false
            showCoinWarning = true
        } else {
            canSend = true
            showCoinWarning = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF2F8))
    ) {
        // Barra superior Swiny
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 38.dp, bottom = 8.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Swiny",
                color = Color(0xFF4A90E2),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Box(
                Modifier
                    .background(Color(0xFFFFE683), shape = RoundedCornerShape(11.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    "BOT",
                    color = Color(0xFFCAA500),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Contador de monedas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.oinkies),
                    contentDescription = "Oinkies",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 2.dp)
                )
                Text(
                    text = coinBalance.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB7760C),
                )
            }
        }
        Divider(color = Color(0xFFEFE6D4), thickness = 1.dp)
        // Mensajes chat
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(messages.asReversed()) { message ->
                    if (!message.isUser) {
                        // Mensaje Swiny
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Avatar/ícono Swiny
                            Box(
                                Modifier
                                    .padding(bottom = 3.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFE6F3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.swiny_chatbot),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            // Burbuja lila
                            Box(
                                Modifier
                                    .widthIn(max = 280.dp)
                                    .padding(start = 8.dp, end = 18.dp)
                                    .background(
                                        Color(0xFFF2ECFF),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    message.text,
                                    color = Color(0xFF29235C),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        // Mensaje usuario
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                Modifier
                                    .widthIn(max = 240.dp)
                                    .background(
                                        Color(0xFFDDFCD2),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 15.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    message.text,
                                    color = Color(0xFF173A04),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(11.dp))
                    }
                }
            }
        }
        // Input campo
        if (showCoinWarning) {
            Text(
                text = "Uy! Parece que no cuentas con los Oinkies suficientes",
                color = Color(0xFFE53935),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color(0xFFF2ECFF), shape = RoundedCornerShape(22.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text("Escribe un mensaje") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                enabled = canSend
            )
            IconButton(
                onClick = {
                    if (!canSend) return@IconButton
                    if (prompt.isNotBlank()) {
                        val userPrompt = prompt.trim()
                        if (userPrompt.isEmpty()) return@IconButton
                        // Descontar oinkie antes de enviar
                        if (coinManager.cobrarPorBot(coinPrice)) {
                            coinBalance -= coinPrice
                            isLoading = true
                            addMessageAndPersist(ChatMessage(userPrompt, true))
                            prompt = ""
                            scope.launch {
                                try {
                                    val reply = ChatRepository.sendMessage(userPrompt)
                                    addMessageAndPersist(ChatMessage(reply, false))
                                } catch (e: Exception) {
                                    addMessageAndPersist(
                                        ChatMessage(
                                            "Error de conexión o respuesta del servidor",
                                            false
                                        )
                                    )
                                }
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = canSend,
                modifier = Modifier
                    .padding(start = 3.dp)
                    .size(38.dp)
                    .background(Color(0xFFF2ECFF), shape = RoundedCornerShape(19.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Enviar mensaje",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(27.dp)
                )
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
fun UserAvatar() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center
    ) {
        Text("Tú", color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BotAvatar() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center
    ) {
        Text("IA", color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}
