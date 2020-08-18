package com.es.marocapp.model.responses

data class GetAccountHolderInformationResponse(
    val accountHolderStatus: String,
    val bankDomainName: String,
    val credentialList: CredentialList,
    val defaultfri: Any,
    val description: String,
    val deviceId: String,
    val firstName: String,
    val internalidentity: String,
    val msisdn: String,
    val profileName: String,
    val responseCode: String,
    val sureName: String,
    val language: String,
    val email:String?
)

data class CredentialList(
    val credentials: List<Credential>
)

data class Credential(
    val credentialstatus: String,
    val credentialtype: String
)