package com.example.tbtb.data.repository

import com.example.tbtb.api.ApiClient
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.request.CreateTaskRequest
import com.example.tbtb.data.request.UpdateTaskRequest
import com.example.tbtb.data.response.ApiResponse
import com.example.tbtb.data.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class TaskRepository {

    private val api = ApiClient.apiService

    suspend fun createTask(token: String, request: CreateTaskRequest): ApiResponse<Task> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Task>> = api.createTask("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }

    suspend fun updateTask(token: String, taskId: String, request: UpdateTaskRequest): ApiResponse<Task> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Task>> = api.updateTask("Bearer $token", taskId, request)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }

    suspend fun deleteTask(token: String, taskId: String): ApiResponse<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Unit>> = api.deleteTask("Bearer $token", taskId)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception("Failed to delete task: ${response.message()}")
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }

    suspend fun getAllTasks(token: String, projectId: String): ApiResponse<List<Task>> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<List<Task>>> = api.getAllTasks("Bearer $token", projectId)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }

    suspend fun getTaskDetails(
        token: String,
        taskId: String,
        callback: (Task?, String?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Task>> =
                    api.getTaskDetails("Bearer $token", taskId)

                if (response.isSuccessful) {
                    val task = response.body()?.data
                    if (task != null) {
                        // Successful response, return task data
                        callback(task, null)
                    } else {
                        // Response body is null
                        callback(null, "Error: Response body is null")
                    }
                } else {
                    // Handle error cases
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid or expired token"
                        403 -> "Access denied"
                        else -> "Error: ${response.code()} ${response.message()}"
                    }
                    callback(null, errorMessage)
                }
            } catch (e: HttpException) {
                // Handle HTTP exceptions
                callback(null, "HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                // Handle generic exceptions
                callback(null, "Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

}
