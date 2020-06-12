package com.es.marocapp.model.requests

data class GenerateOtpRequest(
    val context: String,
    val identity: String,
    val type: String
)