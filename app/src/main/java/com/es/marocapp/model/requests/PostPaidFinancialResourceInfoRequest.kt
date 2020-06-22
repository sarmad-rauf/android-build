package com.es.marocapp.model.requests

data class PostPaidFinancialResourceInfoRequest(
    val code: String,
    val context: String,
    val `receiver`: String,
    val sender: String
)