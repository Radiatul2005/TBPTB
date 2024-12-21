package com.example.tbtb.data.request

import com.google.gson.annotations.SerializedName

data class ProjectRequest(
    @SerializedName("nama_project")
    val nama_project: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("object")
    val object_type: String,
    @SerializedName("collaborators")
    val collaborators: List<String> = emptyList()
)
