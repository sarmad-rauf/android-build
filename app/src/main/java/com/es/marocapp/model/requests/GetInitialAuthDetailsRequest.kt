package com.es.marocapp.model.requests

data class GetInitialAuthDetailsRequest(
    val msisdn: String,
    val noncekey1: String
)