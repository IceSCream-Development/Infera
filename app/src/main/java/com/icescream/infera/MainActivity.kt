package com.icescream.infera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.icescream.infera.ui.theme.InferaTheme
import com.icescream.infera.ui.AprendeScreen
import com.icescream.infera.ui.ChatBotScreen
import com.icescream.infera.ui.LogrosScreen
import com.icescream.infera.ui.LeccionesScreen
import com.icescream.infera.ui.PerfilScreen
import com.icescream.infera.ui.RegisterScreen
import com.icescream.infera.ui.LoginScreen
import com.icescream.infera.ui.WelcomeScreen
import com.icescream.infera.ui.WelcomeUserScreen
import com.icescream.infera.ui.HomeScreen  // Import correcto de HomeScreen
import com.icescream.infera.data.AuthRepository // Importa el AuthRepository singleton

// MainActivity es el punto de entrada de la app (actividad principal)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado que decide cuál pantalla mostrar
            var pantallaActual by remember { mutableStateOf("welcome") } // Posibles: "welcome", "login", "register", "welcomeUser", "home"
            var registerErrorMsg by remember { mutableStateOf<String?>(null) }
            var loginErrorMsg by remember { mutableStateOf<String?>(null) }
            var homeTabIndex by remember { mutableStateOf(0) } // Indice de la sección activa de Home
            var currentUsername by remember { mutableStateOf<String?>(null) } // Para personalizar WelcomeUserScreen

            // Usa el tema global
            InferaTheme {
                when (pantallaActual) {
                    "welcome" -> {
                        WelcomeScreen(
                            onLoginClick = {
                                pantallaActual = "login"
                            },
                            onRegisterClick = {
                                pantallaActual = "register"
                            }
                        )
                    }

                    "login" -> {
                        LoginScreen(
                            onLogin = { email, password, callback ->
                                AuthRepository.loginUser(
                                    email = email,
                                    password = password,
                                    onSuccess = { userProfile ->
                                        loginErrorMsg = null
                                        currentUsername = userProfile["username"] as? String
                                        callback(null) // Login exitoso
                                    },
                                    onError = { errorMsg ->
                                        loginErrorMsg = errorMsg
                                        callback(errorMsg)
                                    }
                                )
                            },
                            onLoginSuccess = {
                                pantallaActual = "welcomeUser"
                                homeTabIndex = 0
                            },
                            onBack = {
                                pantallaActual = "welcome"
                                loginErrorMsg = null
                            },
                            errorMsg = loginErrorMsg,
                            onGoToRegister = {
                                pantallaActual = "register"
                                loginErrorMsg = null
                            }
                        )
                    }

                    "register" -> {
                        RegisterScreen(
                            onRegister = { username, email, password, onSuccess ->
                                AuthRepository.registerUser(
                                    username = username,
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        registerErrorMsg = null
                                        currentUsername = username
                                        onSuccess() // Muestra el snackbar
                                        pantallaActual = "login"
                                        homeTabIndex = 0
                                    },
                                    onError = { errorMsg ->
                                        registerErrorMsg = errorMsg
                                    }
                                )
                            },
                            onBack = {
                                pantallaActual = "welcome"
                                registerErrorMsg = null
                            },
                            errorMsg = registerErrorMsg,
                            onGoToLogin = {
                                pantallaActual = "login"
                                registerErrorMsg = null
                            }
                        )
                    }

                    "welcomeUser" -> {
                        WelcomeUserScreen(
                            username = currentUsername,
                            onContinue = {
                                pantallaActual = "home"
                                // Otros resets de estado si hace falta
                            }
                        )
                    }

                    "home" -> {
                        HomeScreen(
                            seccionSeleccionada = homeTabIndex,
                            onSeleccionar = { nuevoIndex -> homeTabIndex = nuevoIndex },
                            contenido = {
                                when (homeTabIndex) {
                                    0 -> AprendeScreen()
                                    1 -> ChatBotScreen()
                                    2 -> LogrosScreen()
                                    3 -> LeccionesScreen()
                                    4 -> PerfilScreen()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}