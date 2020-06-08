package com.es.marocapp.model.responses

import com.squareup.moshi.Json


data class GetBalanceResponse(

	val currnecy: String? = null,
	val amount: String? = null,
	val description: String? = null,
	val responseCode: String? = null
)