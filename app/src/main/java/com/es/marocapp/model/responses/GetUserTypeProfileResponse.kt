package com.es.marocapp.model.responses

data class GetUserTypeProfileResponse(
    val description: String,
    val responseCode: String,
    val userProfilesMap: UserTypeUserProfilesMap
)

data class UserTypeUserProfilesMap(
    val consumer: List<UserTypeConsumer>
)

data class UserTypeConsumer(
    val id: String,
    val profileName: String
)