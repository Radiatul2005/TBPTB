package com.example.tbtb.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.model.UpdateUserData
import com.example.tbtb.data.model.UserData
import com.example.tbtb.data.repository.AuthRepository
import com.example.tbtb.data.repository.ProjectRepository
import com.example.tbtb.data.request.UpdateUserRequest
import com.example.tbtb.data.response.UpdateUserResponse
import com.example.tbtb.data.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    private val _updateSuccess = MutableStateFlow<UpdateUserData?>(null)
    val updateSuccess: StateFlow<UpdateUserData?> = _updateSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _operationResult = MutableStateFlow<Boolean?>(null)
    val operationResult: StateFlow<Boolean?> get() = _operationResult

    fun getCurrentUser(token: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.getCurrentUser(token) { userResponse, error->
                        _currentUser.value = userResponse
                        _errorMessage.value = error
                    }
                }
                Log.d("ProfileViewModel", "Response: $response")
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(
        token: String,
        nama: String? = null,
        email: String? = null,
        password: String? = null,
        photo_url: MultipartBody.Part? = null,
        onComplete: (Boolean) -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val updateResponse = authRepository.updateUser(token, nama, email, password, photo_url)
                if (updateResponse.isSuccessful) {
                    Log.d("ProfileViewModel", "User updated")
                    _updateSuccess.value = updateResponse.body()?.data
                    _errorMessage.value = updateResponse.message()
                    onComplete(true)
                    getCurrentUser(token)
                }else{
                    _errorMessage.value = updateResponse.message()
                    Log.d("ProfileViewModel", "User not updated")
                    onComplete(false)
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                Log.e("ProfileViewModel", "Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }




}