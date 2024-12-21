package com.example.tbtb.data.request

data class CreateTaskRequest(
    val deskripsi: String,
    val deadline: String,
    val penanggung_jawab: String, // User ID for the responsible person
    val project_id: String
)