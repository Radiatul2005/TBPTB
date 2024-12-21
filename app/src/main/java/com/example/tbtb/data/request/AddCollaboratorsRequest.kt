package com.example.tbtb.data.request

data class AddCollaboratorsRequest(
    val project_id: String,
    val collaborators: List<String>
)
