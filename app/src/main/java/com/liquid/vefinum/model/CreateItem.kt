package com.liquid.vefinum.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class CreateItem(
    val label: String,
    val content: String,
    val icon: String,
    val mediaList: JsonArray,
    val extensions: Extensions = Extensions(false, listOf()),
    val address: String? = null,
    val latitude: Int = 0,
    val longitude: Int = 0,
    val eventSource: String = "GlobalComposeMenu",
    val keywords: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    @Serializable
    data class Extensions(val fansOnly: Boolean, val props: List<String>)
}
