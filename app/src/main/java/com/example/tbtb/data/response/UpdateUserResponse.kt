package com.example.tbtb.data.response

import com.example.tbtb.data.model.UserData

data class UpdateUserResponse(
    val message: String,
    val data: UserData? // Assuming the response contains updated user data
)