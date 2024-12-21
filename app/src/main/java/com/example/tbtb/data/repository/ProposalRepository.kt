package com.example.tbtb.data.repository

import com.example.tbtb.api.ApiClient
import com.example.tbtb.data.model.Proposal
import com.example.tbtb.data.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response

class ProposalRepository {

    private val apiService = ApiClient.apiService

    suspend fun createProposal(
        token: String,
        project_id: String,
        title: String,
        deskripsi: String,
        file_url: MultipartBody.Part
    ): Response<ApiResponse<Proposal>> {
        val titleRequestBody = RequestBody.create(MultipartBody.FORM, title)
        val descriptionRequestBody = RequestBody.create(MultipartBody.FORM, deskripsi)

        return try {
            // Call the API to create the proposal
            val response = apiService.createProposal(
                token,
                project_id,
                titleRequestBody,
                descriptionRequestBody,
                file_url
            )
            if (response.isSuccessful) {
                response
            } else {
                handleError(response)
            }
        } catch (e: Exception) {
            // Handle exception, if needed
            throw Exception(e.message)
        }
    }

    private fun <T> handleError(response: Response<T>): Response<T> {
        val errorBody = response.errorBody()?.string()
        when (response.code()) {
            400 -> throw Exception(parseErrorMessage(errorBody))
            403 -> throw Exception(parseErrorMessage(errorBody))
            else -> throw Exception(response.message())
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val json = JSONObject(errorBody ?: "")
            json.getString("message")
        } catch (e: Exception) {
            "Unknown error"
        }
    }
}
