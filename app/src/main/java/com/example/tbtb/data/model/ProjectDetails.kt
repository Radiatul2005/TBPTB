package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class ProjectDetails(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama_project")
    val namaProject: String,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("is_finish")
    val isFinish: Boolean,

    @SerializedName("project_collaborator")
    val collaborators: List<Collaborator>,

    @SerializedName("task")
    val tasks: List<Task>,

    @SerializedName("proposals")
    val proposals: Any? // Assuming proposals could be nullable
)