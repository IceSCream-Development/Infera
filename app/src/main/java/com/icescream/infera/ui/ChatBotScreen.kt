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
import com.icescream.infera.data.LogrosManager
import androidx.compose.ui.platform.LocalContext

// Modelo de mensaje
data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatBotScreen() {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! ¿En qué puedo ayudarte?", false)
        )
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val logrosManager = remember { LogrosManager(context) }

    LaunchedEffect(Unit) {
        logrosManager.onChatBotUsed()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Gemini Playground", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(messages.asReversed()) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!message.isUser) BotAvatar()
                        Card(
                            modifier = Modifier.padding(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.isUser)
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                message.text,
                                color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (message.isUser) UserAvatar()
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Escribe tu pregunta...") },
                modifier = Modifier.fillMaxWidth(0.95f),
                enabled = !isLoading,
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val userPrompt = prompt.trim()
                    if (userPrompt.isEmpty()) return@Button
                    isLoading = true
                    messages.add(ChatMessage(userPrompt, true))
                    prompt = ""
                    scope.launch {
                        try {
                            val reply = ChatRepository.sendMessage(userPrompt)
                            messages.add(ChatMessage(reply, false))
                        } catch (e: Exception) {
                            messages.add(
                                ChatMessage(
                                    "Error de conexión o respuesta del servidor",
                                    false
                                )
                            )
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading && prompt.isNotBlank(),
            ) {
                Text("Enviar")
            }
            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
        }
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
