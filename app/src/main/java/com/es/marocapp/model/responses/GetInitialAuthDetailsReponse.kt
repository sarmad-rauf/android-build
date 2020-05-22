package com.es.marocapp.model.responses

data class GetInitialAuthDetailsReponse(
    val description: String?,
    val encryptedNonce: String?,
    val responseCode: String?
)