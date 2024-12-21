package com.example.tbtb.data.response

import com.example.tbtb.data.model.AddProjectData
import com.example.tbtb.data.model.AuthData

data class AddProjectResponse (
    val message: String?, // Pesan dari server
    val data: AddProjectData?
)