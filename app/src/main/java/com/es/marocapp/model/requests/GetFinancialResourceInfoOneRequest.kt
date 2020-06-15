package com.es.marocapp.model.requests

data class GetFinancialResourceInfoOneRequest(
    val context: String,
    val language: String,
    val operation: String,
    val profile: String,
    val `receiver`: String
)