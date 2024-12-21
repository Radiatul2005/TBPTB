package com.example.tbtb.data.request


import com.google.gson.annotations.SerializedName

data class EditProjectRequest(
    @SerializedName("nama_project")
    val nama_project: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("object")
    val object_type: String,
    @SerializedName("is_finish")
    val is_finish: Boolean
)
