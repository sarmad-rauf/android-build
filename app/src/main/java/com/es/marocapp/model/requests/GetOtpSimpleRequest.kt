package com.es.marocapp.model.requests

data class GetOtpSimpleRequest(
    val context: String,
    val identity: String
)