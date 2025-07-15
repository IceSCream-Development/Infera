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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // UID del usuario creado
                    val uid = auth.currentUser!!.uid
                    // El perfil que guardaremos en Firestore
                    val perfil = hashMapOf(
                        "username" to username,
                        "email" to email
                    )
                    db.collection("users").document(uid).set(perfil)
                        .addOnSuccessListener { onSuccess() }
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
                    val uid = auth.currentUser!!.uid
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
}
