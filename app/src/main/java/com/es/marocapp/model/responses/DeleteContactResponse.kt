package com.es.marocapp.model.responses

data class DeleteContactResponse(
    val contactList: List<Contact>,
    val description: String,
    val responseCode: String
)