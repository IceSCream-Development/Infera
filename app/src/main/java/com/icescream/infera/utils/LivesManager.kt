package com.icescream.infera.utils

import android.content.Context
import android.content.SharedPreferences

object LivesManager {
    private const val PREFS_NAME = "lives_prefs"
    private const val KEY_LIVES = "lives"
    private const val KEY_LIFE_RECOVERY_TIMESTAMPS = "life_recovery_timestamps"
    private const val MAX_LIVES = 5
    private const val RESTORE_INTERVAL_MS = 5 * 60 * 1000L // 5 minutos en ms

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun loadTimestamps(context: Context): MutableList<Long> {
        val str = prefs(context).getString(KEY_LIFE_RECOVERY_TIMESTAMPS, "") ?: ""
        return if (str.isBlank()) mutableListOf() else str.split(",")
            .mapNotNull { it.toLongOrNull() }.toMutableList()
    }

    private fun saveTimestamps(context: Context, times: List<Long>) {
        prefs(context).edit().putString(KEY_LIFE_RECOVERY_TIMESTAMPS, times.joinToString(","))
            .apply()
    }

    // Devuelve las vidas que EXPRESAMENTE tienes ahorita para jugar (nunca suma las que se están recuperando)
    fun getLives(context: Context): Int {
        return prefs(context).getInt(KEY_LIVES, MAX_LIVES).coerceIn(0, MAX_LIVES)
    }

    // Perder una vida SOLO si tienes al menos 1 y menos de MAX_LIVES timestamps pendientes
    fun loseLife(context: Context): Int {
        val vidasActuales = getLives(context)
        val recoveryList = loadTimestamps(context)
        if (vidasActuales > 0 && recoveryList.size < MAX_LIVES) {
            val nuevoBase = vidasActuales - 1
            prefs(context).edit().putInt(KEY_LIVES, nuevoBase).apply()
            recoveryList.add(System.currentTimeMillis() + RESTORE_INTERVAL_MS)
            saveTimestamps(context, recoveryList)
        }
        return getLives(context)
    }

    // Devuelve si puedes jugar (tienes vida actual)
    fun canAnswer(context: Context): Boolean = getLives(context) > 0

    // DEBUG/RESET: establece vidas actuales y borra pendientes
    fun setVidas(context: Context, amount: Int) {
        prefs(context).edit().putInt(KEY_LIVES, amount.coerceIn(0, MAX_LIVES)).apply()
        saveTimestamps(context, emptyList())
    }

    // Recupera todas las vidas posibles hasta el máximo y limpia SIEMPRE los vencidos
    fun recoverLivesSiCorresponde(context: Context) {
        val baseVidas = prefs(context).getInt(KEY_LIVES, MAX_LIVES)
        val recoveryList = loadTimestamps(context)
        val now = System.currentTimeMillis()
        val vencidas = recoveryList.filter { it <= now }
        val pendientes = recoveryList.filter { it > now }

        val maxQuePuedoSumar = (MAX_LIVES - baseVidas).coerceAtLeast(0)
        val vidasASumar = vencidas.size.coerceAtMost(maxQuePuedoSumar)
        val nuevoBase = (baseVidas + vidasASumar).coerceAtMost(MAX_LIVES)

        prefs(context).edit().putInt(KEY_LIVES, nuevoBase).apply()
        // ¡SIEMPRE elimina todos los timestamps vencidos, aunque no los puedas sumar!
        saveTimestamps(context, pendientes)
    }

    // Retorna tiempo para próxima vida (solo si faltan vidas)
    fun getMillisUntilNextLife(context: Context): Long {
        val vidasBase = getLives(context)
        if (vidasBase >= MAX_LIVES) return -1L
        val recoveryList = loadTimestamps(context)
        val now = System.currentTimeMillis()
        val pendientes = recoveryList.filter { it > now }
        val next = pendientes.minOrNull() ?: (now + RESTORE_INTERVAL_MS)
        return (next - now).coerceAtLeast(0)
    }

    fun getMaxLives() = MAX_LIVES
}
