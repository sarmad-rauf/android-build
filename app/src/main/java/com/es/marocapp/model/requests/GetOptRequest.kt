package com.es.marocapp.model.requests

data class GetOptRequest(
    val authorization: String,
    val context: String,
    val identity: String
)