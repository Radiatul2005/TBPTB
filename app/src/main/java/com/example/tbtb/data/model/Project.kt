package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class Project(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama_project")
    val namaProject: String,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("created_at")
    val createdAt: String
)