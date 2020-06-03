package com.es.marocapp.model.responses

data class AccountHolderAdditionalInformationResponse(
    val additionalinformation: List<Any>,
    val description: String,
    val responseCode: String
)