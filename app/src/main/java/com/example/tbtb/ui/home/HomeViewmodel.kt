package com.example.tbtb.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.repository.AuthRepository
import com.example.tbtb.data.repository.ProjectRepository
import com.example.tbtb.data.response.ProjectDetailsResponse
import com.example.tbtb.data.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewmodel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val projectRepository = ProjectRepository()

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _projectDetails = MutableStateFlow<ProjectDetailsResponse?>(null)
    val projectDetails: StateFlow<ProjectDetailsResponse?> = _projectDetails

    private val _shouldLogout = MutableStateFlow(false)
    val shouldLogout: StateFlow<Boolean> = _shouldLogout

    fun getCurrentUser(token: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    authRepository.getCurrentUser(token){ it, errorMessage ->

                        _currentUser.value = it
                        _message.value = errorMessage

                    }
                }

            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAllProjects(token: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = projectRepository.getAllProjects(token)
                _projects.value = response.data
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProjectDetails(token: String, projectId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = projectRepository.getProjectDetails(token, projectId)
                _projectDetails.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}