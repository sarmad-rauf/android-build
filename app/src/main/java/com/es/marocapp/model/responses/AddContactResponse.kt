package com.es.marocapp.model.responses

data class AddContactResponse(
    val contactList: List<Contact>,
    val description: String,
    val responseCode: String
)
