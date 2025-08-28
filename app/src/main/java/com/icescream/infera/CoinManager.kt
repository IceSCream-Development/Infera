package com.icescream.infera

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import java.util.*

class CoinManager private constructor(context: Context) {
    companion object {
        private var INSTANCE: CoinManager? = null
        fun getInstance(context: Context): CoinManager {
            if (INSTANCE == null) {
                INSTANCE = CoinManager(context.applicationContext)
            }
            return INSTANCE!!
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("coin_prefs", Context.MODE_PRIVATE)

    private val KEY_COINS = "coins"
    private val KEY_LAST_DAILY = "last_daily"

    // Sincroniza el saldo de Firestore al iniciar
    init {
        syncCoinsFromFirestore {}
    }

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 0)
        private set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    /** Obtiene y actualiza el saldo de Firestore una vez (para mostrar en la UI). Llama onLoaded(saldo). */
    fun getCoinsFromFirestore(onLoaded: (Int) -> Unit) {
        val user = auth.currentUser ?: return onLoaded(0)
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val saldo = (doc.getLong("oinkies") ?: 0L).toInt()
                coins = saldo // Sincroniza cache local
                onLoaded(saldo)
            }
            .addOnFailureListener { onLoaded(coins) }
    }

    /** Refresca local el saldo trayéndolo de Firestore */
    fun syncCoinsFromFirestore(onSynced: () -> Unit = {}) {
        val user = auth.currentUser ?: return onSynced()
        val uid = user.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                coins = (doc.getLong("oinkies") ?: 0L).toInt()
                onSynced()
            }
            .addOnFailureListener { onSynced() }
    }

    /** Actualiza el saldo en Firestore tras sumar localmente */
    private fun updateCoinsInFirestore() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        db.collection("users").document(uid).set(hashMapOf("oinkies" to coins), SetOptions.merge())
    }

    // --- MONEDAS POR PREGUNTA ---
    fun otorgarMonedasPorPregunta(numPreguntas: Int, cantidadPorPregunta: Int = 10) {
        coins += numPreguntas * cantidadPorPregunta
        updateCoinsInFirestore()
    }

    // --- MONEDAS POR INICIO DIARIO ---
    fun otorgarMonedasPorInicioSesion(cantidad: Int = 100): Boolean {
        val hoy = todayString()
        val ultimoDia = prefs.getString(KEY_LAST_DAILY, null)
        if (hoy != ultimoDia) {
            coins += cantidad
            prefs.edit().putString(KEY_LAST_DAILY, hoy).apply()
            updateCoinsInFirestore()
            return true
        }
        return false // No se otorgaron por ya haberse dado hoy
    }

    private fun todayString(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    // --- VERIFICAR SI PUEDE HABLAR CON EL BOT ---
    fun puedeHablarConBot(precio: Int = 10): Boolean {
        return coins >= precio
    }

    // --- COBRAR MONEDAS POR USO DEL BOT ---
    fun cobrarPorBot(precio: Int = 10): Boolean {
        return if (coins >= precio) {
            coins -= precio
            updateCoinsInFirestore()
            true
        } else {
            false
        }
    }

}