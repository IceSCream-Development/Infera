package com.icescream.infera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.icescream.infera.ui.theme.InferaTheme
import com.icescream.infera.ui.RegisterScreen
import com.icescream.infera.ui.LoginScreen
import com.icescream.infera.WelcomeScreen

// MainActivity es el punto de entrada de la app (actividad principal)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado que decide cuál pantalla mostrar
            var pantallaActual by remember { mutableStateOf("welcome") } // Valores posibles: "welcome", "login", "register"

            // Usa el tema global
            InferaTheme {
                when (pantallaActual) {
                    "welcome" -> {
                        WelcomeScreen(
                            onLoginClick = {
                                pantallaActual = "login"
                            }, // Cambia a la pantalla de login
                            onRegisterClick = {
                                pantallaActual = "register"
                            } // Cambia a la pantalla de registro
                        )
                    }

                    "login" -> {
                        LoginScreen(
                            onLogin = { email, password ->
                                // Aquí pones la lógica de login real
                            },
                            onBack = {
                                pantallaActual = "welcome"
                            } // Botón regresar manda a la pantalla de bienvenida
                        )
                    }

                    "register" -> {
                        RegisterScreen(
                            onRegister = { username, email, password ->
                                // Aquí pondrás la lógica de registro real
                            },
                            onBack = {
                                pantallaActual = "welcome"
                            } // Botón regresar manda a la pantalla de bienvenida
                        )
                    }
                }
            }
        }
    }
}