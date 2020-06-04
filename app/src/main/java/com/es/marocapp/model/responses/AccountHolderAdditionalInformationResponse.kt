package com.es.marocapp.model.responses

data class AccountHolderAdditionalInformationResponse(
    val additionalinformation: List<Additionalinformation>,
    val description: String,
    val responseCode: String
)

data class Additionalinformation(
    val name: String,
    val value: String
)