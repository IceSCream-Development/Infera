package com.icescream.infera.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

/** Repositorio que maneja el registro y login con Firebase */
object AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Registro de usuario y creación de perfil en Firestore
    fun registerUser(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Verificar si el username ya existe usando la colección usernames
        db.collection("usernames").document(username).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Ya existe ese username
                    onError("Ese nombre de usuario ya está en uso. Prueba uno diferente.")
                } else {
                    // Nombre de usuario disponible, continúa con el registro en Auth
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.currentUser!!.uid
                                val perfil = hashMapOf(
                                    "username" to username,
                                    "email" to email
                                )
                                db.collection("users").document(uid).set(perfil)
                                    .addOnSuccessListener {
                                        // Crear documento en usernames (con referencia al uid)
                                        db.collection("usernames").document(username)
                                            .set(mapOf("uid" to uid))
                                            .addOnSuccessListener {
                                                // Enviar correo de verificación antes de dar registro por exitoso
                                                auth.currentUser?.sendEmailVerification()
                                                    ?.addOnSuccessListener {
                                                        onSuccess() // Registro correcto y correo enviado
                                                    }
                                                    ?.addOnFailureListener { e ->
                                                        onError("No se pudo enviar el correo de verificación: " + e.message)
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                onError("No se pudo guardar el nombre de usuario: ${e.message}")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        onError("Hubo un problema guardando tu perfil. Intenta nuevamente.")
                                    }
                            } else {
                                val errorMsg = when (val ex = task.exception) {
                                    is FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
                                    is FirebaseAuthWeakPasswordException -> "Usa una contraseña de al menos 6 caracteres."
                                    is FirebaseAuthInvalidCredentialsException -> "El correo no es válido."
                                    else -> "No se pudo crear tu cuenta. Intenta nuevamente."
                                }
                                onError(errorMsg)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onError("No se pudo consultar el nombre de usuario: ${e.message}")
            }
    }

    // Login de usuario y obtención de su perfil
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && !user.isEmailVerified) {
                        auth.signOut()
                        onError("Debes verificar tu correo antes de iniciar sesión.")
                        return@addOnCompleteListener
                    }
                    val uid = user!!.uid
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) onSuccess(doc.data!!)
                            else onError("No existe perfil guardado para este usuario")
                        }
                        .addOnFailureListener { e -> onError("Error buscando perfil: ${e.message}") }
                } else {
                    onError("Correo o contraseña incorrectos")
                }
            }
    }

    // Permite reenviar el correo de verificación a un email
    fun resendVerificationEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && !user.isEmailVerified) {
                        user.sendEmailVerification()
                            .addOnSuccessListener {
                                auth.signOut()
                                onSuccess() // Correo de verificación reenviado
                            }
                            .addOnFailureListener { e ->
                                auth.signOut()
                                onError("No se pudo reenviar el correo de verificación: ${e.message}")
                            }
                    } else {
                        auth.signOut()
                        onError("El usuario ya está verificado o no existe.")
                    }
                } else {
                    onError("Correo o contraseña incorrectos")
                }
            }
    }
}
