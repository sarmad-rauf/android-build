package com.es.marocapp.model.responses

data class GetReciptTemplateResponse(
    val description: String,
    val responseCode: String,
    val fileDataHtml: String,
    //val fileData: String

)