package com.example.tbtb.data.response

import com.example.tbtb.data.model.AuthData

// Respons untuk login
data class LoginResponse(
    val message: String?, // Pesan dari server
    val data: AuthData?   // Data yang diterima
)


