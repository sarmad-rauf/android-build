package com.es.marocapp.model.responses

data class RegisterUserResponse(
    val accountholderid: String?,
    val description: String?,
    val invitationToken: Any?,
    val responseCode: String?
)