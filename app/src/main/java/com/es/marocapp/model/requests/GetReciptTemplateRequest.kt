package com.es.marocapp.model.requests

data class GetReciptTemplateRequest(
    val context: String,
    val identity: String,
    val financialtransactionid: String
)