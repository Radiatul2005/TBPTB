package com.example.tbtb.data.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("photo_url")
    val photo_url: String
)
