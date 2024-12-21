package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: String,
    val deskripsi: String,
    val deadline: String,
    val is_finish: Boolean,
    val penanggung_jawab: String,
    val project_id: String
)