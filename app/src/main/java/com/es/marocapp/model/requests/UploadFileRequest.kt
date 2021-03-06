package com.es.marocapp.model.requests

data class UploadFileRequest(
    val identity: String,
    val profile: String,
    val reason: String,
    val context: String,
    val frontImage: FileData,
    val backImage: FileData
)

data class LevelTwoRegistrationRequest(
    val accountholder: Accountholder,
    val context: String,
    val deviceId: String,
    val email: String,
    val identity: String,
    val frontImage: FileData,
    val backImage: FileData
)

data class FileData(
    val fileData: String,
    val providerUploadDocumentRequest: ProviderUploadDocumentRequest
)

data class ProviderUploadDocumentRequest(
    val description: String,
    val documentnumber: String,
    val filename: String
)