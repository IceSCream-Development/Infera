package com.icescream.infera.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.icescream.infera.R
import com.icescream.infera.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.icescream.infera.CoinManager
import androidx.compose.ui.platform.LocalContext

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

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Todo el contenido y botones ahora está en la misma columna scrolleable.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                // Imagen de perfil
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
                // Nombre de usuario (encabezado)
                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                // Mostrar monedas
                val context = LocalContext.current
                val coins = remember { mutableStateOf(CoinManager.getInstance(context).coins) }
                Text(
                    text = "Monedas: ${coins.value}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFECB400),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Fecha de unión
                Text(
                    text = "Miembro desde: ${uiState.fechaUnion}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                    label = { Text("Nombre de usuario", color = labelColor) },
                    textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 12.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = containerColor,
                        focusedContainerColor = containerColor,
                        disabledTextColor = textColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        disabledLabelColor = labelColor,
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor,
                        disabledBorderColor = borderColor
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = {},
                    label = { Text("Correo electrónico", color = labelColor) },
                    textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 12.sp),
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = containerColor,
                        disabledTextColor = textColor,
                        disabledLabelColor = labelColor,
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor,
                        disabledBorderColor = borderColor
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editablePassword,
                    onValueChange = { editablePassword = it },
                    label = { Text("Nueva contraseña", color = labelColor) },
                    textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 12.sp),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (showPassword) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Mostrar/Ocultar",
                            modifier = Modifier.clickable { showPassword = !showPassword }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = containerColor,
                        focusedContainerColor = containerColor,
                        disabledTextColor = textColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        disabledLabelColor = labelColor,
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor,
                        disabledBorderColor = borderColor
                    )
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (editableUsername.isBlank()) {
                            // No es necesario error local, ya que ViewModel lo manejará
                        } else {
                            if (editableUsername != uiState.username)
                                profileViewModel.updateUsername(editableUsername)
                            if (editablePassword.isNotBlank())
                                profileViewModel.updatePassword(editablePassword)
                            // Limpiar campo de contraseña
                            editablePassword = ""
                        }
                    },
                    enabled = editableUsername != uiState.username || editablePassword.isNotBlank(),
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
                Spacer(Modifier.height(32.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Cerrar sesión")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDeleteAccount,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
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
}
