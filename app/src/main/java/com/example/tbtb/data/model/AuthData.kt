package com.example.tbtb.data.model

data class AuthData(
    val token: String?,   // Token JWT
    val user: UserData?       // Data pengguna
)