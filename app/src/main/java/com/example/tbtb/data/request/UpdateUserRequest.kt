package com.example.tbtb.data.request

import okhttp3.MultipartBody

data class UpdateUserRequest(
    val email: String?,
    val nama: String?,
    val password: String?,
    val photo: MultipartBody.Part? = null
)