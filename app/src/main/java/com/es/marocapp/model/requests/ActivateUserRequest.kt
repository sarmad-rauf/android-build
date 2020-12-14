package com.es.marocapp.model.requests

data class ActivateUserRequest(
    val context: String,
    val identity: String,
    val secret: String,
    val type: String,
    val userProfile:String
)