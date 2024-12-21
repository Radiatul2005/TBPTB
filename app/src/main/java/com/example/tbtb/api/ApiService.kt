package com.example.tbtb.api

import com.example.tbtb.data.model.AddProjectData
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.model.Proposal
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.model.UpdateUserData
import com.example.tbtb.data.model.User
import com.example.tbtb.data.model.UserData
import com.example.tbtb.data.request.AddCollaboratorsRequest
import com.example.tbtb.data.request.CreateTaskRequest
import com.example.tbtb.data.request.EditProjectRequest
import com.example.tbtb.data.request.JoinProjectRequest
import com.example.tbtb.data.request.LoginRequest
import com.example.tbtb.data.request.ProjectRequest
import com.example.tbtb.data.request.RegisterRequest
import com.example.tbtb.data.request.UpdateTaskRequest
import com.example.tbtb.data.request.UpdateUserRequest
import com.example.tbtb.data.response.AddProjectResponse
import com.example.tbtb.data.response.ApiResponse
import com.example.tbtb.data.response.JoinProjectResponse
import com.example.tbtb.data.response.LoginResponse
import com.example.tbtb.data.response.ProjectDetailsResponse
import com.example.tbtb.data.response.ProjectsResponse
import com.example.tbtb.data.response.RegisterResponse
import com.example.tbtb.data.response.UpdateUserResponse
import com.example.tbtb.data.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("api/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserResponse>

    @Multipart
    @PATCH("api/me")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Part("email") email: RequestBody? = null,
        @Part("nama") nama: RequestBody? = null,
        @Part("password") password: RequestBody? = null,
        @Part photo_url: MultipartBody.Part? = null
    ): Response<ApiResponse<UpdateUserData>>


    @GET("api/projects")
    suspend fun getAllProjects(
        @Header("Authorization") token: String
    ): ProjectsResponse

    @GET("api/projects/{id}")
    suspend fun getProjectDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ProjectDetailsResponse

    @POST("api/projects")
    suspend fun createProject( @Header("Authorization") token: String, @Body project: ProjectRequest): Response<AddProjectResponse>

    @POST("api/project/join")
    suspend fun joinProject(
        @Header("Authorization") token: String,
        @Body request: JoinProjectRequest
    ): Response<ApiResponse<JoinProjectResponse>>

    @PATCH("api/project/{id}")
    suspend fun updateProject(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body project: EditProjectRequest
    ): Response<ApiResponse<Project>>

    @DELETE("api/project/{id}")
    suspend fun deleteProject(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("api/project/collaborators")
    suspend fun addCollaborators(
        @Header("Authorization") token: String,
        @Body request: AddCollaboratorsRequest
    ): Response<ApiResponse<Unit>>

    // Create Task
    @POST("api/task")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body request: CreateTaskRequest
    ): Response<ApiResponse<Task>>

    // Update Task
    @PATCH("api/task/{id}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("id") taskId: String,
        @Body request: UpdateTaskRequest
    ): Response<ApiResponse<Task>>

    // Delete Task
    @DELETE("api/task/{id}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("id") taskId: String
    ): Response<ApiResponse<Unit>>

    // Get All Tasks for a project
    @GET("api/tasks/{project_id}")
    suspend fun getAllTasks(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: String
    ): Response<ApiResponse<List<Task>>>

    // Get Task Details
    @GET("api/task/{id}")
    suspend fun getTaskDetails(
        @Header("Authorization") token: String,
        @Path("id") taskId: String
    ): Response<ApiResponse<Task>>

    @Multipart
    @POST("api/{project_id}/proposal")
    suspend fun createProposal(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: String,
        @Part("judul") title: RequestBody, // Proposal title
        @Part("deskripsi") description: RequestBody, // Proposal description
        @Part file: MultipartBody.Part? // PDF file
    ): Response<ApiResponse<Proposal>>

}
