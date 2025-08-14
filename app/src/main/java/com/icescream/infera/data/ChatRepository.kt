package com.icescream.infera.data

import com.icescream.infera.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Repositorio responsable de comunicar la app con el backend del chatbot.
 * Mantiene la UI desacoplada de detalles HTTP y URLs.
 */
object ChatRepository {
    private val httpClient: OkHttpClient = OkHttpClient()

    suspend fun sendMessage(prompt: String): String {
        val requestBody = JSONObject(mapOf("prompt" to prompt))
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(AppConfig.CHAT_API_URL)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@use "El servidor respondió con un error (${response.code})."
                }
                try {
                    JSONObject(bodyStr).getString("response")
                } catch (_: Exception) {
                    "¡Hubo un problema interpretando la respuesta del servidor!"
                }
            }
        }
    }
}
