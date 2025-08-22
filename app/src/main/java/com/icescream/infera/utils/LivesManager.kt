package com.icescream.infera.utils

import android.content.Context
import android.content.SharedPreferences

object LivesManager {
    private const val PREFS_NAME = "lives_prefs"
    private const val KEY_LIVES = "lives"
    private const val KEY_LAST_LIFE_LOST_TIME = "last_life_lost_time"
    private const val MAX_LIVES = 5
    private const val RESTORE_INTERVAL_MS = 5 * 60 * 1000L // 5 minutos en milisegundos

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Obtiene la cantidad actual de vidas guardadas (NO recalcula automáticamente) */
    fun getLives(context: Context): Int {
        return prefs(context).getInt(KEY_LIVES, MAX_LIVES)
    }

    /** Pierde una vida, y guarda timestamp de pérdida */
    fun loseLife(context: Context): Int {
        val actual = prefs(context).getInt(KEY_LIVES, MAX_LIVES)
        if (actual > 0) {
            prefs(context).edit()
                .putInt(KEY_LIVES, actual - 1)
                .putLong(KEY_LAST_LIFE_LOST_TIME, System.currentTimeMillis())
                .apply()
        }
        return actual - 1
    }

    /** Se puede responder si vidas > 0 */
    fun canAnswer(context: Context): Boolean = getLives(context) > 0

    /** Guarda la cantidad de vidas directamente (por debugging o restauración) */
    fun setVidas(context: Context, amount: Int) {
        prefs(context).edit().putInt(KEY_LIVES, amount.coerceIn(0, MAX_LIVES)).apply()
    }

    /** Obtiene el tiempo (en ms) cuando se perdió la última vida */
    fun getLastLifeLostTime(context: Context): Long =
        prefs(context).getLong(KEY_LAST_LIFE_LOST_TIME, 0L)

    /** Llama esto antes de mostrar vidas: recupera vidas solo si pasó suficiente tiempo y mantiene lastLost correcto */
    fun recoverLivesSiCorresponde(context: Context) {
        var lives = prefs(context).getInt(KEY_LIVES, MAX_LIVES)
        if (lives >= MAX_LIVES) return
        val lastLost = getLastLifeLostTime(context)
        if (lastLost == 0L) return
        val now = System.currentTimeMillis()
        val intervalos = ((now - lastLost) / RESTORE_INTERVAL_MS).toInt()
        if (intervalos > 0) {
            val newLives = (lives + intervalos).coerceAtMost(MAX_LIVES)
            val nuevoTimestamp =
                if (newLives < MAX_LIVES) lastLost + intervalos * RESTORE_INTERVAL_MS else 0L
            prefs(context).edit()
                .putInt(KEY_LIVES, newLives)
                .putLong(KEY_LAST_LIFE_LOST_TIME, nuevoTimestamp)
                .apply()
        }
    }

    /** Devuelve el tiempo en ms restante para la siguiente vida (o -1 si ya lleno de vidas) */
    fun getMillisUntilNextLife(context: Context): Long {
        val lives = getLives(context)
        if (lives >= MAX_LIVES) return -1
        val lastLost = getLastLifeLostTime(context)
        if (lastLost == 0L) return RESTORE_INTERVAL_MS
        val elapsed = System.currentTimeMillis() - lastLost
        return RESTORE_INTERVAL_MS - (elapsed % RESTORE_INTERVAL_MS)
    }

    /** Devuelve el máximo de vidas */
    fun getMaxLives() = MAX_LIVES
}
