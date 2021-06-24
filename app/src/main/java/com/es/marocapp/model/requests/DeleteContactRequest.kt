package com.es.marocapp.model.requests

data class DeleteContactRequest(
    val identity : String,
    val context: String,
    val billprovidercontactid: String
)