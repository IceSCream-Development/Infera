package com.icescream.infera.viewmodel

import androidx.lifecycle.ViewModel
import com.icescream.infera.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Estado UI completo para el perfil
data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val fechaUnion: String = "",
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null, successMsg = null)
        AuthRepository.getCurrentUserProfile(
            onSuccess = { username, email, fechaUnion ->
                _uiState.value = ProfileUiState(
                    username = username,
                    email = email,
                    fechaUnion = fechaUnion,
                    isLoading = false,
                    errorMsg = null
                )
            },
            onError = { msg ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMsg = msg
                )
            }
        )
    }

    fun updateUsername(newUsername: String) {
        val oldUsername = uiState.value.username
        _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null, successMsg = null)
        AuthRepository.updateUsername(
            newUsername = newUsername,
            oldUsername = oldUsername,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    username = newUsername,
                    isLoading = false,
                    successMsg = "Nombre de usuario actualizado"
                )
            },
            onError = { msg ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMsg = msg)
            }
        )
    }

    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null, successMsg = null)
        AuthRepository.updatePassword(
            newPassword = newPassword,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMsg = "Contraseña actualizada"
                )
            },
            onError = { msg ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMsg = msg)
            }
        )
    }
}
