package com.es.marocapp.model.requests

data class UpdateLanguageRequest(

    val context: String?,
    val identity : String?,
    val reason: String?,
    val language: String?
)