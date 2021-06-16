package com.es.marocapp.model.requests

data class AddContactRequest(
    val identity: String,
    val contactname: String,
    val context: String,
    val billproviderfri: String,
    val customerreference: String
)

data class GetContactRequest(
    val context: String,
    val identity: String
)