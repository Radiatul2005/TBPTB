package com.example.tbtb.ui.project

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.model.AddProjectData
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.repository.ProjectRepository
import com.example.tbtb.data.repository.TaskRepository
import com.example.tbtb.data.request.AddCollaboratorsRequest
import com.example.tbtb.data.request.EditProjectRequest
import com.example.tbtb.data.request.JoinProjectRequest
import com.example.tbtb.data.request.ProjectRequest
import com.example.tbtb.data.response.JoinProjectResponse
import com.example.tbtb.data.response.ProjectDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository()
    private val taskRepository = TaskRepository()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> get() = _projects

    private val _addProject = MutableStateFlow<AddProjectData?>(null)
    val addProject: StateFlow<AddProjectData?> get() = _addProject

    private val _projectDetails = MutableStateFlow<ProjectDetailsResponse?>(null)
    val projectDetails: StateFlow<ProjectDetailsResponse?> = _projectDetails

    private val _addCollaboratorsResult = MutableStateFlow<String?>(null)
    val addCollaboratorsResult: StateFlow<String?> = _addCollaboratorsResult

    private val _joinProjectResult = MutableStateFlow<JoinProjectResponse?>(null)
    val joinProjectResult: StateFlow<JoinProjectResponse?> get() = _joinProjectResult

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _operationResult = MutableStateFlow<Boolean?>(null)
    val operationResult: StateFlow<Boolean?> get() = _operationResult


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _projectDelete = MutableStateFlow<String?>(null)
    val projectDelete: StateFlow<String?> get() = _projectDelete

    fun fetchProjects(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllProjects(token)
                _projects.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchProjectDetails(token: String, projectId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.getProjectDetails(token, projectId)
                _projectDetails.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addProject(token: String, request: ProjectRequest, onComplete: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createProject("Bearer $token", request)
                _addProject.value = response.data
                val inviteCode = response.data?.project?.invite_code

                onComplete()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProject(token: String, projectId: String, request: EditProjectRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.updateProject("Bearer $token", projectId, request)
                fetchProjects(token) // Refresh project list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProject(token: String, projectId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.deleteProject(token, projectId)
                _projectDelete.value = response.message
                Log.d("ProjectViewModel", response.message)
                fetchProjects(token)
                onComplete(true)
            } catch (e: Exception) {
                _error.value = e.message
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun joinProject(token: String, inviteCode: String, onComplete: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val request = JoinProjectRequest(inviteCode)
                val response = repository.joinProject(token, request)
                _joinProjectResult.value = response.data
                onComplete()
            } catch (e: Exception) {
                _error.value = e.message
                onComplete()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addCollaborators(token: String, request: AddCollaboratorsRequest, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.addCollaborators(token, request)
                _addCollaboratorsResult.value = response.message
                onComplete(true)
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("ProjectViewModel", "Error adding collaborators: ${e.message}")
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Delete a task
    fun deleteTask(token: String, taskId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = taskRepository.deleteTask(token, taskId)
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
