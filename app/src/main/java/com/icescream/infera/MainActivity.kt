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
import com.icescream.infera.data.LogrosManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import android.util.Log

// MainActivity es el punto de entrada de la app (actividad principal)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Forzar persistencia Firestore (por robustez, aunque es el default)
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        // SUGERIDO: INFORMAR a los usuarios que desactiven optimización de batería para la app en settings del sistema (no se puede automatizar en código Android)
        setContent {
            val authUser = FirebaseAuth.getInstance().currentUser
            Log.d(
                "AUTHTEST",
                "Usuario: " + (authUser?.email ?: "NULO") + ", UID: " + (authUser?.uid ?: "NULO")
            )
            val initialScreen = if (authUser != null) "welcomeUser" else "welcome"
            var pantallaActual by remember { mutableStateOf(initialScreen) }
            var registerErrorMsg by remember { mutableStateOf<String?>(null) }
            var loginErrorMsg by remember { mutableStateOf<String?>(null) }
            var homeTabIndex by remember { mutableStateOf(0) } // Indice de la sección activa de Home
            var currentUsername by remember { mutableStateOf<String?>(null) } // Para personalizar WelcomeUserScreen
            val context = this // Activity Context
            val logrosManager = remember { LogrosManager(context) }
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
                            },
                            onGoogleRegisterSuccess = {
                                pantallaActual = "welcomeUser"
                                // currentUsername se podría actualizar usando datos de Firebase si lo deseas
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
                                    3 -> PerfilScreen(
                                        onLogout = {
                                            // Cerrar sesión en Firebase
                                            FirebaseAuth.getInstance().signOut()
                                            pantallaActual = "welcome"
                                            homeTabIndex = 0
                                        },
                                        onDeleteAccount = {
                                            // Eliminar cuenta de usuario y perfil en Firestore
                                            val user = FirebaseAuth.getInstance().currentUser
                                            val uid = user?.uid
                                            if (user != null && uid != null) {
                                                val db =
                                                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                                // 1. Borrar perfil Usuario
                                                db.collection("users").document(uid).delete()
                                                    .addOnSuccessListener {
                                                        // 2. Buscar username y borrar
                                                        db.collection("usernames")
                                                            .whereEqualTo("uid", uid).get()
                                                            .addOnSuccessListener { query ->
                                                                for (doc in query) {
                                                                    doc.reference.delete()
                                                                }
                                                                // 3. Borrar usuario Auth
                                                                user.delete()
                                                                    .addOnCompleteListener {
                                                                        pantallaActual = "welcome"
                                                                        homeTabIndex = 0
                                                                    }
                                                            }
                                                    }
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
