package com.es.marocapp.model.responses

data class GetUserProfileNameResponse(
    val description: String,
    val responseCode: String,
    val userProfilesMap: UserProfilesNameMap
)

data class UserProfilesNameMap(
    val agent: List<ProfileNameAgent>
)

data class ProfileNameAgent(
    val id: String,
    val profileName: String
)