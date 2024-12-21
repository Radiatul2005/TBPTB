package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class UpdateUserData (
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("photo_url")
    val photo_url: String,
    @SerializedName("password")
    val password: String
)