package com.icescream.infera.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.infera.utils.LivesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VidasViewModel(app: Application) : AndroidViewModel(app) {
    private val _vidas = MutableStateFlow(LivesManager.getLives(app))
    val vidas: StateFlow<Int> = _vidas.asStateFlow()

    private val _tiempoRestante = MutableStateFlow(LivesManager.getMillisUntilNextLife(app))
    val tiempoRestante: StateFlow<Long> = _tiempoRestante.asStateFlow()

    init {
        // Coroutine única para actualización
        viewModelScope.launch {
            while (true) {
                LivesManager.recoverLivesSiCorresponde(app)
                _vidas.value = LivesManager.getLives(app)
                _tiempoRestante.value = LivesManager.getMillisUntilNextLife(app)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    // Método para perder una vida
    fun perderVida() {
        LivesManager.loseLife(getApplication())
        _vidas.value = LivesManager.getLives(getApplication())
        _tiempoRestante.value = LivesManager.getMillisUntilNextLife(getApplication())
    }

    // Método para forzar refresco manual (si llegada del QuizScreen lo requiere)
    fun refrescar() {
        LivesManager.recoverLivesSiCorresponde(getApplication())
        _vidas.value = LivesManager.getLives(getApplication())
        _tiempoRestante.value = LivesManager.getMillisUntilNextLife(getApplication())
    }
}
