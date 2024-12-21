package com.example.tbtb.data.response

import com.example.tbtb.data.model.ProjectDetails
import com.google.gson.annotations.SerializedName

data class ProjectDetailsResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProjectDetails?
)