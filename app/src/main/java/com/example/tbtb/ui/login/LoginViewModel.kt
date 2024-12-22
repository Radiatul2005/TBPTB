package com.example.tbtb.ui.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.request.LoginRequest
import com.example.tbtb.data.repository.AuthRepository
import com.example.tbtb.ui.common.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

    // Login status menggunakan sealed class
    private val _loginStatus = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginStatus: StateFlow<LoginState> = _loginStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        val loginRequest = LoginRequest(email, password)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.login(loginRequest)
                }
                if (response.data?.token != null) {
                    response.data.user?.let { saveToken(response.data.token, it.id) }
                    _loginStatus.value = LoginState.Success("Login Successful")
                } else {
                    _loginStatus.value = LoginState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                val userFriendlyMessage = when {
                    e.message?.contains("404") == true -> "Invalid username or password"
                    e.message?.contains("401") == true -> "Invalid username or password"
                    e.message?.contains("500") == true -> "Server is currently unavailable. Please try again later."
                    else -> "An unexpected error occurred. Please try again."
                }
                _errorMessage.value = userFriendlyMessage
                _loginStatus.value = LoginState.Error(userFriendlyMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun saveToken(token: String, userId: String) {
        sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply()
        sharedPreferences.edit().putString(USER_ID, userId).apply()
    }

    companion object {
        const val USER_PREFS = "user_prefs"
        const val USER_ID = "user_id"
        const val KEY_USER_TOKEN = "user_token"
    }
}

