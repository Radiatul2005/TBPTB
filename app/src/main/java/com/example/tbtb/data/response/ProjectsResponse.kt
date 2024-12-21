package com.example.tbtb.data.response

import com.example.tbtb.data.model.Project
import com.google.gson.annotations.SerializedName

data class ProjectsResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<Project>
)