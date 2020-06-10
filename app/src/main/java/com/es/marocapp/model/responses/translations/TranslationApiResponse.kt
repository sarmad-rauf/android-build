package com.es.marocapp.model.responses.translations

data class TranslationApiResponse(
	val labelList: LabelList? = null,
	val description: String? = null,
	val responseCode: String? = null
)
