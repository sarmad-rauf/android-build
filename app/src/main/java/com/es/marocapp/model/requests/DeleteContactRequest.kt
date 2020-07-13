package com.es.marocapp.model.requests

data class DeleteContactRequest(
    val contactidentity: String,
    val context: String
)