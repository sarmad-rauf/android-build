package com.es.marocapp.model.requests

data class GetAccountHolderInformationRequest(
    val context: String,
    val identity: String
)

data class GetAccountDetailRequest(
    val context: String,
    val identity: String,
    val deviceId:String
)