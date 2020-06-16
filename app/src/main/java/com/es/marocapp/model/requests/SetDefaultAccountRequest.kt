package com.es.marocapp.model.requests

data class SetDefaultAccountRequest(

    val context: String,
    val receiver: String,
    val operation: String,
    val language: String,
    val profile : String?
)