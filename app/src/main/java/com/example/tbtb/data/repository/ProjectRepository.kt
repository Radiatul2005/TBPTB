package com.example.tbtb.data.repository

import com.example.tbtb.api.ApiClient
import com.example.tbtb.data.model.AddProjectData
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.request.AddCollaboratorsRequest
import com.example.tbtb.data.request.EditProjectRequest
import com.example.tbtb.data.request.JoinProjectRequest
import com.example.tbtb.data.request.ProjectRequest
import com.example.tbtb.data.response.AddProjectResponse
import com.example.tbtb.data.response.ApiResponse
import com.example.tbtb.data.response.JoinProjectResponse
import com.example.tbtb.data.response.ProjectDetailsResponse
import com.example.tbtb.data.response.ProjectsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class ProjectRepository {

    private val api = ApiClient.apiService

    suspend fun getAllProjects(token: String): ProjectsResponse = withContext(Dispatchers.IO) {
        try {
            api.getAllProjects("Bearer $token")
        } catch (e: HttpException) {
            throw Exception(e.message())
        }
    }

    suspend fun getProjectDetails(token: String, projectId: String): ProjectDetailsResponse =
        withContext(Dispatchers.IO) {
            try {
                api.getProjectDetails("Bearer $token", projectId)
            } catch (e: HttpException) {
                throw Exception(e.message())
            }
        }

    suspend fun createProject(token: String, projectRequest: ProjectRequest): AddProjectResponse =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<AddProjectResponse> = api.createProject(token, projectRequest)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception(e.message())
            }
        }

    suspend fun updateProject(token: String, id: String, projectRequest: EditProjectRequest): ApiResponse<Project> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Project>> = api.updateProject(token, id, projectRequest)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception(e.message())
            }
        }

    suspend fun deleteProject(token: String, id: String): ApiResponse<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Unit>> = api.deleteProject("Bearer $token", id)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception("Failed to delete project: ${response.body()!!}")
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }

    suspend fun joinProject(token: String, request: JoinProjectRequest): ApiResponse<JoinProjectResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<JoinProjectResponse>> = api.joinProject("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception(response.message())
                }
            } catch (e: HttpException) {
                throw Exception(e.message())
            }
        }
    suspend fun addCollaborators(token: String, request: AddCollaboratorsRequest): ApiResponse<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<Unit>> = api.addCollaborators("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception("Failed to add collaborators: ${response.body()!!}")
                }
            } catch (e: HttpException) {
                throw Exception("HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }


}
