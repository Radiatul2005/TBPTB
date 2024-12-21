package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class ProjectData(
    val id: String,
    val nama_project: String,
    val deskripsi: String,
    @SerializedName("object")
    val object_: String,
    val is_finish: Boolean,
    val invite_code: String,
    val created_at: String,
    val updated_at: String
)
