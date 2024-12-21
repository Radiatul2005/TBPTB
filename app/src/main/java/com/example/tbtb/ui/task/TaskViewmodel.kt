package com.example.tbtb.ui.task

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.repository.TaskRepository
import com.example.tbtb.data.request.CreateTaskRequest
import com.example.tbtb.data.request.UpdateTaskRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _taskDetails = MutableStateFlow<Task?>(null)
    val taskDetails: StateFlow<Task?> get() = _taskDetails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _operationResult = MutableStateFlow<Boolean?>(null)
    val operationResult: StateFlow<Boolean?> get() = _operationResult

    // Fetch task details
    fun fetchTaskDetails(token: String, taskId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repository.getTaskDetails(token, taskId) { task, errorMessage ->
                    if (task != null) {
                        _taskDetails.value = task
                    } else {
                        _error.value = errorMessage
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Create a new task
    fun createTask(token: String, request: CreateTaskRequest, onComplete:  (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repository.createTask(token, request)
                _operationResult.value = true
                onComplete(true)
            } catch (e: Exception) {
                _error.value = e.message
                _operationResult.value = false
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update an existing task
    fun updateTask(token: String, taskId: String, request: UpdateTaskRequest, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repository.updateTask(token, taskId, request)
                _operationResult.value = true
                onComplete(true)
            } catch (e: Exception) {
                _error.value = e.message
                _operationResult.value = false
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


}
