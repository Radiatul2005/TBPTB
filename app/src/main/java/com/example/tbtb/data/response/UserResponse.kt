package com.example.tbtb.data.response

import com.example.tbtb.data.model.UserData

data class UserResponse(
    val message: String,
    val data: UserData?
)