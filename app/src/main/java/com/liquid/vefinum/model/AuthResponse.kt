package com.liquid.vefinum.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("api:statuscode")
    val apiStatusCode: Int,
    @SerialName("api:message")
    val apiMessage: String,
    val userProfile: UserProfile?,
    val sid: String?
) {
    @Serializable
    data class UserProfile(
        val uid: String,
        val nickname: String,
        val role: Int,
        val icon: String?
    )
}
