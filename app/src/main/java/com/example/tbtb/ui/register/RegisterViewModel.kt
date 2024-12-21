package com.example.tbtb.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.request.LoginRequest
import com.example.tbtb.data.repository.AuthRepository
import com.example.tbtb.data.request.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _registerStatus = MutableStateFlow<String?>(null)
    val registerStatus: StateFlow<String?> = _registerStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun register(name: String, email: String, password: String) {

        _isLoading.value = true
        _errorMessage.value = null

        val registerRequest = RegisterRequest(name, email, password)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.register(registerRequest)
                }
                if (response != null) {
                    if (response.data != null) {
                        _registerStatus.value = "Registration Successful"
                    } else {
                        _registerStatus.value = "Login Failed: Invalid credentials"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }


}
