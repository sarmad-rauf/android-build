package com.es.marocapp.model.requests

data class AddContactRequest(
    val contactIdentity: String,
    val contactName: String,
    val context: String
)