package com.icescream.infera

import android.content.Context
import android.content.SharedPreferences
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

    private val prefs: SharedPreferences =
        context.getSharedPreferences("coin_prefs", Context.MODE_PRIVATE)

    private val KEY_COINS = "coins"
    private val KEY_LAST_DAILY = "last_daily"

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 0)
        private set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    // --- MONEDAS POR PREGUNTA ---
    fun otorgarMonedasPorPregunta(numPreguntas: Int, cantidadPorPregunta: Int = 10) {
        coins += numPreguntas * cantidadPorPregunta
    }

    // --- MONEDAS POR INICIO DIARIO ---
    fun otorgarMonedasPorInicioSesion(cantidad: Int = 100): Boolean {
        val hoy = todayString()
        val ultimoDia = prefs.getString(KEY_LAST_DAILY, null)
        if (hoy != ultimoDia) {
            coins += cantidad
            prefs.edit().putString(KEY_LAST_DAILY, hoy).apply()
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
            true
        } else {
            false
        }
    }

}