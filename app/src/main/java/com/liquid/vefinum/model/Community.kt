package com.liquid.vefinum.model

import kotlinx.serialization.Serializable

@Serializable
data class Community(
    val name: String,
    val ndcId: Int,
    val icon: String,
    val themeColor: String,
    val cover: String?
)
