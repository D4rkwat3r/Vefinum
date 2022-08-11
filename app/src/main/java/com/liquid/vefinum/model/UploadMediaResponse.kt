package com.liquid.vefinum.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadMediaResponse(
    @SerialName("api:statuscode")
    val apiStatusCode: Int,
    val mediaValue: String?
)
