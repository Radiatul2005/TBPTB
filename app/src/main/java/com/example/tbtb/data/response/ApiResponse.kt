package com.example.tbtb.data.response

data class ApiResponse<T>(
    val message: String,
    val data: T?
)