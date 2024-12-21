package com.example.tbtb.data.request

data class UpdateTaskRequest(
    val deskripsi: String,
    val deadline: String,
    val is_finish: Boolean,
    val penanggung_jawab: String // User ID for the responsible person
)