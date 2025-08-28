package com.icescream.infera.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.icescream.infera.CoinManager
import com.icescream.infera.R
import com.icescream.infera.data.LogrosManager
import com.icescream.infera.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de perfil del usuario. Aquí puede ver/editar su información.
 */
@Composable
fun PerfilScreen(
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    // Estados locales para edición
    var editableUsername by remember { mutableStateOf("") }
    LaunchedEffect(uiState.username) {
        editableUsername = uiState.username
    }
    var editablePassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Limpia mensajes de éxito después de un tiempo
    if (uiState.successMsg != null) {
        LaunchedEffect(uiState.successMsg) {
            delay(2000)
            profileViewModel.loadProfile() // recarga datos y limpia mensaje
        }
    }

    val context = LocalContext.current
    val logrosManager = remember { LogrosManager(context) }
    LaunchedEffect(Unit) { logrosManager.onPerfilVisited() }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordChangeDialog by remember { mutableStateOf(false) }

    val streakState = remember { mutableStateOf(0) }
    // Al abrir, obtiene la racha real
    LaunchedEffect(Unit) {
        logrosManager.getCurrentStreak {
            streakState.value = it
        }
    }

    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }
    val isGoogleOnly = remember {
        firebaseUser?.providerData?.all { it.providerId == "google.com" || it.providerId == "firebase" } == true
    }
    var googlePasswordWarn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF2F8)) // Fondo pastel para toda la pantalla
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.perfil_blue_shape),
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .fillMaxWidth()
                .size(140.dp)
                .width(100.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Todo el contenido y botones ahora está en la misma columna scrolleable.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                // Nombre de usuario (encabezado)
                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                // Fecha de unión
                Text(
                    text = "Miembro desde: ${uiState.fechaUnion}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Imagen de perfil
                Image(
                    painter = painterResource(id = R.drawable.perfil_icon),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp, top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.oinkies),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50)),
                            contentScale = ContentScale.Crop
                        )
                        var coinBalance by remember { mutableStateOf(0) }
                        val coinManager = remember { CoinManager.getInstance(context) }
                        LaunchedEffect(Unit) {
                            coinManager.getCoinsFromFirestore { saldo ->
                                coinBalance = saldo
                            }
                        }
                        Text(
                            text = coinBalance.toString(),
                            fontSize = 16.sp,
                            color = Color(0xFFB7760C),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Oinkies Recolectados",
                            fontSize = 12.sp,
                            color = Color(0xFF9D9045),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.streak),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = streakState.value.toString(),
                            fontSize = 16.sp,
                            color = Color(0xFF7D1DD0),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Días de Racha",
                            fontSize = 12.sp,
                            color = Color(0xFF9D9045),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.Gray
                    )
                }
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                val isDark = isSystemInDarkTheme()
                val textColor = if (isDark) Color.White else Color.Black
                val labelColor = if (isDark) Color.LightGray else Color.DarkGray
                val containerColor = if (isDark) Color(0xFF232323) else Color.White
                val borderColor = if (isDark) Color.Gray else Color.LightGray

                OutlinedTextField(
                    value = editableUsername,
                    onValueChange = { editableUsername = it },
                    label = { Text("Nombre de usuario", color = Color(0xFF1C1B1F)) },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF23232D),
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFAEF),
                        focusedContainerColor = Color(0xFFFFFAEF),
                        disabledTextColor = Color(0xFF23232D),
                        focusedTextColor = Color(0xFF23232D),
                        unfocusedTextColor = Color(0xFF23232D),
                        disabledLabelColor = Color(0xFF1C1B1F),
                        focusedBorderColor = Color(0xFFB9D5F6),
                        unfocusedBorderColor = Color(0xFFEFE6D4),
                        disabledBorderColor = Color(0xFFEFE6D4)
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = {},
                    label = { Text("Correo electrónico", color = Color(0xFF1C1B1F)) },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF23232D),
                        fontSize = 14.sp
                    ),
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFAEF),
                        disabledTextColor = Color(0xFF23232D),
                        disabledLabelColor = Color(0xFF1C1B1F),
                        focusedBorderColor = Color(0xFFB9D5F6),
                        unfocusedBorderColor = Color(0xFFEFE6D4),
                        disabledBorderColor = Color(0xFFEFE6D4)
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editablePassword,
                    onValueChange = { editablePassword = it },
                    label = { Text("Nueva contraseña", color = Color(0xFF1C1B1F)) },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF23232D),
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    enabled = !isGoogleOnly,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (showPassword) R.drawable.unlock else R.drawable.lock
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier.clickable { showPassword = !showPassword }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFAEF),
                        focusedContainerColor = Color(0xFFFFFAEF),
                        disabledTextColor = Color(0xFF23232D),
                        focusedTextColor = Color(0xFF23232D),
                        unfocusedTextColor = Color(0xFF23232D),
                        disabledLabelColor = Color(0xFF1C1B1F),
                        focusedBorderColor = Color(0xFFB9D5F6),
                        unfocusedBorderColor = Color(0xFFEFE6D4),
                        disabledBorderColor = Color(0xFFEFE6D4)
                    )
                )
                if (isGoogleOnly) {
                    Text(
                        text = "Tu cuenta está protegida por Google. No puedes cambiar la contraseña desde aquí.",
                        color = Color(0xFF444A58),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (isGoogleOnly && editablePassword.isNotBlank()) {
                            googlePasswordWarn = true
                        } else if (editablePassword.isNotBlank()) {
                            showPasswordChangeDialog = true
                        } else {
                            if (editableUsername != uiState.username)
                                profileViewModel.updateUsername(editableUsername)
                        }
                    },
                    enabled = editableUsername != uiState.username || (editablePassword.isNotBlank() && !isGoogleOnly),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Guardar Cambios")
                }
                uiState.errorMsg?.let {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
                uiState.successMsg?.let {
                    Text(
                        text = it,
                        color = Color(0xFF13B313),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                // Botones de acción ahora al final del scroll:
                Spacer(Modifier.height(40.dp))
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2D2D2D),
                        containerColor = Color(0xFFFAF2F8),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Cerrar sesión")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFFFFFF),
                        containerColor = Color(0xFFE53935),
                        disabledContentColor = Color(0xFFDB8989)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Eliminar cuenta")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            modifier = Modifier.defaultMinSize(minWidth = 380.dp),
            title = {
                Text(
                    text = "Infera",
                    color = Color(0xFF55D36E),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de querer Cerrar tu sesión?",
                        color = Color(0xFF141C31),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Al continuar, se cerrará tu sesión y volverás a la pantalla de inicio.",
                        color = Color(0xFF444A58),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF2D2D2D)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2D2D2D)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF2D2D2D), fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7263D)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Salir", color = Color(0xFFFFF1F3), fontSize = 14.sp)
                    }
                }
            },
            dismissButton = null,
        )
    }
    // Diálogo de confirmación para eliminar cuenta
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier.defaultMinSize(minWidth = 400.dp),
            title = {
                Text(
                    text = "Infera",
                    color = Color(0xFF55D36E),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de querer Eliminar tu cuenta?",
                        color = Color(0xFF141C31),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Al continuar, se eliminará tu progreso y no podrás recuperar tu cuenta de nuevo.",
                        color = Color(0xFF444A58),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF2D2D2D)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2D2D2D)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF2D2D2D), fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            onDeleteAccount()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7263D)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Eliminar", color = Color(0xFFFFF1F3), fontSize = 14.sp)
                    }
                }
            },
            dismissButton = null,
        )
    }
    // Diálogo de confirmación para cambiar contraseña
    if (showPasswordChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordChangeDialog = false },
            modifier = Modifier.defaultMinSize(minWidth = 400.dp),
            title = {
                Text(
                    text = "Confirmar cambio de contraseña",
                    color = Color(0xFF55D36E),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de querer cambiar tu contraseña?",
                        color = Color(0xFF141C31),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Esta acción actualizará tu contraseña de acceso.",
                        color = Color(0xFF444A58),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showPasswordChangeDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF2D2D2D)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2D2D2D)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF2D2D2D), fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            showPasswordChangeDialog = false
                            if (editableUsername != uiState.username)
                                profileViewModel.updateUsername(editableUsername)
                            if (editablePassword.isNotBlank())
                                profileViewModel.updatePassword(editablePassword)
                            // Limpiar campo de contraseña
                            editablePassword = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F3)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Confirmar cambio", color = Color(0xFFD7263D), fontSize = 14.sp)
                    }
                }
            },
            dismissButton = null,
        )
    }
    if (googlePasswordWarn) {
        AlertDialog(
            onDismissRequest = { googlePasswordWarn = false },
            modifier = Modifier.defaultMinSize(minWidth = 400.dp),
            title = {
                Text(
                    text = "No puedes cambiar tu contraseña",
                    color = Color(0xFFD7263D),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Tu cuenta está protegida por Google. No puedes cambiar la contraseña desde aquí.",
                    color = Color(0xFF444A58),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { googlePasswordWarn = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Aceptar", color = Color(0xFFD7263D))
                }
            },
            dismissButton = null,
        )
    }
}
