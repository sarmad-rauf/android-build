package com.es.marocapp.model.requests

data class CreateCredentialRequest(
    val context: String,
    val identity: String,
    val secret: String,
    val type: String
)