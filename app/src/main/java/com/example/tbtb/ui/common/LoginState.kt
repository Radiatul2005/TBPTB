package com.example.tbtb.ui.common

// Sealed class untuk status login
sealed class LoginState {
    data object Idle : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}
