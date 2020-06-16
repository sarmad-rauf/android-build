package com.es.marocapp.model.responses.translations

data class TranslationApiResponse(
	val labelList: Map<String?, TranslationInnerObject?>? = null,
	val description: String? = null,
	val responseCode: String? = null
)
