package com.es.marocapp.model.responses

data class GetUserSimpleProfileResponse(
    val description: String,
    val responseCode: String,
    val userProfilesMap: UserProfilesMap
)

data class UserProfilesMap(
    val agent: List<Agent>,
    val consumer: List<Consumer>,
    val merchant: List<Merchant>
)

data class Agent(
    val id: String,
    val profileName: String
)

data class Consumer(
    val id: String,
    val profileName: String
)

data class Merchant(
    val id: String,
    val profileName: String
)