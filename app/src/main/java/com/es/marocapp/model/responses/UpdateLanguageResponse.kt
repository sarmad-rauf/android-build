package com.es.marocapp.model.responses

data class UpdateLanguageResponse(
    val responseCode: String?,
    val description: String?
)

data class UpgradeProfileResponse(
    val responseCode: String?,
    val description: String?
)
data class GetProfileResponse(
    val responseCode: String?,
    val description: String?,
    val profileName: String?
)