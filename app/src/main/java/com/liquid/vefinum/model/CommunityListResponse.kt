package com.liquid.vefinum.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityListResponse(
    @SerialName("api:statuscode")
    val apiStatusCode: Int,
    @SerialName("api:message")
    val apiMessage: String,
    val communityList: List<Community>
)
