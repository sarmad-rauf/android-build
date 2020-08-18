package com.es.marocapp.model.responses

data class GetFaqsResponse(
    val description: String,
    val faqList: List<Faq>,
    val responseCode: String
)

data class Faq(
    val answerAR: String,
    val answerEN: String,
    val answerFR: String,
    val id: String,
    val questionAR: String,
    val questionEN: String,
    val questionFR: String
)