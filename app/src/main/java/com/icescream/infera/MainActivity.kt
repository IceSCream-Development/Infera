package com.icescream.infera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.icescream.infera.ui.theme.InferaTheme
import com.icescream.infera.ui.RegisterScreen
import com.icescream.infera.ui.LoginScreen
import com.icescream.infera.ui.WelcomeScreen
import com.icescream.infera.ui.HomeScreen
import com.icescream.infera.data.AuthRepository // Importa el AuthRepository singleton

// MainActivity es el punto de entrada de la app (actividad principal)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado que decide cuál pantalla mostrar
            var pantallaActual by remember { mutableStateOf("welcome") } // Posibles: "welcome", "login", "register", "home"
            var registerErrorMsg by remember { mutableStateOf<String?>(null) }
            var loginErrorMsg by remember { mutableStateOf<String?>(null) }
            var homeTabIndex by remember { mutableStateOf(0) } // Indice de la sección activa de Home

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
                            onLogin = { email, password ->
                                AuthRepository.loginUser(
                                    email = email,
                                    password = password,
                                    onSuccess = { userProfile ->
                                        loginErrorMsg = null
                                        pantallaActual = "home"
                                        homeTabIndex = 0 // Siempre empieza en Principal
                                    },
                                    onError = { errorMsg ->
                                        loginErrorMsg = errorMsg // Si falla, mostrar error
                                    }
                                )
                            },
                            onBack = {
                                pantallaActual = "welcome"
                                loginErrorMsg = null
                            },
                            errorMsg = loginErrorMsg // <-- asegúrate que LoginScreen acepte este parámetro si quieres mostrar errores
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
                                        onSuccess() // Muestra el snackbar
                                        pantallaActual = "home"
                                        homeTabIndex = 0 // Arrancamos siempre en Principal
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
                            errorMsg = registerErrorMsg
                        )
                    }

                    "home" -> {
                        HomeScreen(
                            seccionSeleccionada = homeTabIndex,
                            onSeleccionar = { homeTabIndex = it }
                        )
                    }
                }
            }
        }
    }
}