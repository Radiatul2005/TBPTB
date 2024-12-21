package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class Collaborator(
    val user_id: String,
    val project_id: String,
    val is_owner: Boolean,
    val status: String,
    val User: User
)