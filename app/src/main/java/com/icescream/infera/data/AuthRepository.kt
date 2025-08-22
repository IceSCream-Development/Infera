package com.icescream.infera.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
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

    // Obtener el perfil del usuario actual
    fun getCurrentUserProfile(
        onSuccess: (String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado.")
            return
        }
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: ""
                    val email = user.email ?: ""
                    val fechaUnion = doc.getString("fechaUnion")
                        ?: user.metadata?.creationTimestamp?.let {
                            java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(it))
                        } ?: "Desconocido"
                    onSuccess(username, email, fechaUnion)
                } else {
                    onError("No existe perfil para este usuario")
                }
            }
            .addOnFailureListener { e -> onError("Error buscando perfil: ${e.message}") }
    }

    // Actualizar el nombre de usuario
    fun updateUsername(
        newUsername: String,
        oldUsername: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser ?: run {
            onError("No hay usuario autenticado.")
            return
        }
        val uid = user.uid
        db.collection("usernames").document(newUsername).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("El nombre de usuario ya está en uso.")
                } else {
                    db.collection("users").document(uid)
                        .update("username", newUsername)
                        .addOnSuccessListener {
                            db.collection("usernames").document(newUsername)
                                .set(mapOf("uid" to uid))
                                .addOnSuccessListener {
                                    // Borrar el nombre anterior para liberarlo
                                    db.collection("usernames").document(oldUsername).delete()
                                        .addOnSuccessListener {
                                            onSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            onError("Error al liberar el nombre antiguo: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    onError("Error guardando nombre único: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            onError("No se pudo actualizar el nombre: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                onError("No se pudo consultar el nombre de usuario: ${e.message}")
            }
    }

    // Cambiar contraseña
    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado.")
            return
        }
        user.updatePassword(newPassword)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("No se pudo cambiar la contraseña: ${e.message}") }
    }

    // Login con Google y creación automática de perfil si es nuevo
    fun loginWithGoogle(
        idToken: String,
        onSuccess: (profile: Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onError("No se pudo obtener usuario desde Google")
                    return@addOnSuccessListener
                }
                val uid = user.uid
                val email = user.email ?: ""
                val username = user.displayName ?: email.substringBefore("@")

                // Buscar si ya tiene perfil en 'users'
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            // Ya tiene perfil
                            onSuccess(doc.data!!)
                        } else {
                            // Crear nuevo perfil
                            val perfil = hashMapOf(
                                "username" to username,
                                "email" to email
                            )
                            db.collection("users").document(uid).set(perfil)
                                .addOnSuccessListener {
                                    // Guardar nombre de usuario en usernames sólo si no existe
                                    db.collection("usernames").document(username).get()
                                        .addOnSuccessListener { doc2 ->
                                            if (!doc2.exists()) {
                                                db.collection("usernames").document(username)
                                                    .set(mapOf("uid" to uid))
                                                    .addOnSuccessListener { onSuccess(perfil) }
                                                    .addOnFailureListener { onSuccess(perfil) } // No bloquear login si falla
                                            } else {
                                                onSuccess(perfil)
                                            }
                                        }
                                        .addOnFailureListener { onSuccess(perfil) }
                                }
                                .addOnFailureListener { e ->
                                    onError("No se pudo crear perfil: ${e.message}")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        onError("Error buscando perfil de Google: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al autenticar con Google")
            }
    }

    // Guarda el nivel completado en Firestore para el usuario actual
    fun guardarNivelCompletado(
        nivel: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado.")
            return
        }
        val uid = user.uid
        db.collection("users").document(uid)
            .update(
                "niveles_completados",
                com.google.firebase.firestore.FieldValue.arrayUnion(nivel)
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("No se pudo guardar el nivel: " + e.message) }
    }

    // Recupera los niveles completados del usuario autenticado
    fun obtenerProgresoNiveles(
        onSuccess: (List<Int>) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado.")
            return
        }
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val lista = doc.get("niveles_completados") as? List<Int> ?: listOf()
                    onSuccess(lista)
                } else {
                    onSuccess(listOf()) // Si no hay documento, ningún nivel está completado
                }
            }
            .addOnFailureListener { e ->
                onError("No se pudo obtener el progreso: ${e.message}")
            }
    }
}
