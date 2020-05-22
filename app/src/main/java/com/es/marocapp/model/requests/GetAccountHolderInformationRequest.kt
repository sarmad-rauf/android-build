package com.es.marocapp.model.requests

data class GetAccountHolderInformationRequest(
    val context: String,
    val identity: String
)