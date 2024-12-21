package com.example.tbtb.data.repository

import android.util.Log
import com.example.tbtb.data.request.LoginRequest
import com.example.tbtb.data.response.LoginResponse
import com.example.tbtb.data.request.RegisterRequest
import com.example.tbtb.data.response.RegisterResponse
import com.example.tbtb.api.ApiClient
import com.example.tbtb.api.ApiService
import com.example.tbtb.data.model.Proposal
import com.example.tbtb.data.model.UpdateUserData
import com.example.tbtb.data.model.UserData
import com.example.tbtb.data.request.UpdateUserRequest
import com.example.tbtb.data.response.ApiResponse
import com.example.tbtb.data.response.UpdateUserResponse
import com.example.tbtb.data.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AuthRepository {

    private val apiService: ApiService = ApiClient.apiService

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(loginRequest)
                Log.d("Login Success", response.toString())
                response
            } catch (e: Exception) {
                Log.e("Login Error", e.message ?: "Unknown error")
                throw e
            }
        }
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(registerRequest)
                Log.d("Register Success", response.toString())
                response
            } catch (e: Exception) {
                Log.e("Register Error", e.message ?: "Unknown error")
                throw e
            }
        }
    }

    fun getCurrentUser(token: String, callback: (UserResponse?, String?) -> Unit) {
        val call = apiService.getCurrentUser("Bearer $token")

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid or expired token"
                        403 -> "Access denied"
                        else -> "Error: ${response.code()} ${response.message()}"
                    }
                    callback(null, errorMessage)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("Get Current User", t.localizedMessage ?: "Unknown error")
            }
        })
    }

    suspend fun updateUser(
        token: String,
        nama: String? = null,
        email: String? = null,
        password: String? = null,
        photo_url: MultipartBody.Part? = null
    ): Response<ApiResponse<UpdateUserData>> {
        val nameRequestBody = nama?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }
        val emailRequestBody = email?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }
        val passwordRequestBody = password?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }

        return try {
                apiService.updateUser("Bearer $token", emailRequestBody, nameRequestBody, passwordRequestBody, photo_url)
            } catch (e: Exception) {
                Log.e("Update User Error", e.message ?: "Unknown error")
                throw e
            }
        }


}
